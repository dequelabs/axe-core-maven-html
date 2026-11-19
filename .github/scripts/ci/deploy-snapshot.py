import base64
import hashlib
import os
import sys
import urllib.error
import urllib.parse
import urllib.request
import xml.etree.ElementTree as ET

ARTIFACT_SUFFIXES = ('.jar', '.pom')
CHECKSUMS = {'md5': hashlib.md5, 'sha1': hashlib.sha1}


def fail(message):
    print('::error::' + message, file=sys.stderr)
    raise SystemExit(1)


def env(name):
    value = os.environ.get(name)
    if not value:
        fail(name + ' must be set')
    return value


class RefuseRedirects(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, request, fp, code, message, headers, new_url):
        fail(f'{request.full_url} redirected to {new_url}; refusing to resend credentials')


OPENER = urllib.request.build_opener(RefuseRedirects)


class Repository:
    def __init__(self, url, username, password):
        parsed = urllib.parse.urlparse(url)
        if parsed.scheme != 'https' and parsed.hostname not in ('127.0.0.1', 'localhost'):
            fail(f'{url} is not an https URL')
        self.url = url.rstrip('/') + '/'
        token = base64.b64encode(f'{username}:{password}'.encode()).decode()
        self.authorization = 'Basic ' + token

    def get(self, path):
        request = urllib.request.Request(self.url + path, headers={'Authorization': self.authorization})
        try:
            with OPENER.open(request) as response:
                return response.read()
        except urllib.error.HTTPError as error:
            if error.code == 404:
                return None
            raise

    def put(self, path, data):
        request = urllib.request.Request(
            self.url + path, data=data, method='PUT',
            headers={'Authorization': self.authorization, 'Content-Type': 'application/octet-stream'})
        try:
            with OPENER.open(request):
                pass
        except urllib.error.HTTPError as error:
            fail(f'PUT {path} returned HTTP {error.code}')
        print('Uploaded ' + path)

    def put_with_checksums(self, path, data):
        self.put(path, data)
        for extension, algorithm in CHECKSUMS.items():
            self.put(f'{path}.{extension}', algorithm(data).hexdigest().encode())


def namespace_of(root):
    return root.tag[:root.tag.index('}') + 1] if root.tag.startswith('{') else ''


def merge_artifact_metadata(remote, local):
    if remote is None:
        return local
    remote_root = ET.fromstring(remote)
    local_root = ET.fromstring(local)
    rns = namespace_of(remote_root)
    lns = namespace_of(local_root)
    if rns:
        ET.register_namespace('', rns[1:-1])
    remote_versioning = remote_root.find(rns + 'versioning')
    local_versioning = local_root.find(lns + 'versioning')
    if remote_versioning is None:
        return local

    versions = [v.text for v in remote_versioning.iter(rns + 'version')]
    for version in local_versioning.iter(lns + 'version'):
        if version.text not in versions:
            versions.append(version.text)

    versions_node = remote_versioning.find(rns + 'versions')
    if versions_node is None:
        versions_node = ET.SubElement(remote_versioning, rns + 'versions')
    for child in list(versions_node):
        versions_node.remove(child)
    for version in versions:
        ET.SubElement(versions_node, rns + 'version').text = version

    for field in ('latest', 'lastUpdated'):
        value = local_versioning.find(lns + field)
        if value is None:
            continue
        node = remote_versioning.find(rns + field)
        if node is None:
            node = ET.SubElement(remote_versioning, rns + field)
        node.text = value.text
    return ET.tostring(remote_root, encoding='utf-8', xml_declaration=True)


def collect(staging):
    artifacts, version_metadata, artifact_metadata = [], [], []
    for directory, _, files in os.walk(staging):
        for name in sorted(files):
            path = os.path.relpath(os.path.join(directory, name), staging).replace(os.sep, '/')
            if name == 'maven-metadata.xml':
                parent = os.path.basename(directory)
                (version_metadata if parent.endswith('-SNAPSHOT') else artifact_metadata).append(path)
            elif not name.startswith('maven-metadata'):
                artifacts.append(path)
    return sorted(artifacts), sorted(version_metadata), sorted(artifact_metadata)


def main(staging):
    username = env('CENTRAL_TOKEN_USERNAME')
    password = env('CENTRAL_TOKEN_PASSWORD')
    url = os.environ.get('SNAPSHOT_REPOSITORY_URL') or 'https://central.sonatype.com/repository/maven-snapshots/'

    artifacts, version_metadata, artifact_metadata = collect(staging)
    primaries = [a for a in artifacts if a.endswith(ARTIFACT_SUFFIXES)]
    if not primaries:
        fail('no artifacts to deploy under ' + staging)
    for artifact in primaries:
        if not artifact.split('/')[-2].endswith('-SNAPSHOT'):
            fail(artifact + ' is not a snapshot')
        if artifact + '.asc' not in artifacts:
            fail(artifact + ' has no signature')

    repository = Repository(url, username, password)
    for path in artifacts:
        with open(os.path.join(staging, path), 'rb') as f:
            repository.put(path, f.read())
    for path in version_metadata:
        with open(os.path.join(staging, path), 'rb') as f:
            repository.put_with_checksums(path, f.read())
    for path in artifact_metadata:
        with open(os.path.join(staging, path), 'rb') as f:
            merged = merge_artifact_metadata(repository.get(path), f.read())
        repository.put_with_checksums(path, merged)


if __name__ == '__main__':
    if len(sys.argv) != 2:
        fail('usage: deploy-snapshot.py STAGING_DIR')
    main(sys.argv[1])
