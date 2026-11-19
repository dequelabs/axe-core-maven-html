import os.path
import sys
import xml.etree.ElementTree as ET

NS = 'http://maven.apache.org/POM/4.0.0'
SUFFIX = '-SNAPSHOT'
GROUP_ID = 'com.deque.html.axe-core'
POM_DIRS = ['', 'selenium', 'playwright', 'utilities']

ET.register_namespace('', NS)


def tag(name):
    return '{%s}%s' % (NS, name)


def snapshot(node):
    if not node.text.endswith(SUFFIX):
        node.text += SUFFIX


def make_version_snapshot(pom_path):
    tree = ET.parse(pom_path)
    root = tree.getroot()

    version = root.find(tag('version'))
    if version is None:
        raise SystemExit('%s has no <version>' % pom_path)
    snapshot(version)

    parent = root.find(tag('parent'))
    if parent is not None:
        snapshot(parent.find(tag('version')))

    dependencies = root.find(tag('dependencies'))
    if dependencies is not None:
        for dependency in dependencies.findall(tag('dependency')):
            if dependency.find(tag('groupId')).text == GROUP_ID:
                snapshot(dependency.find(tag('version')))

    tree.write(pom_path, xml_declaration=True, encoding='utf-8', method='xml')


def main(root_dir):
    for pom_dir in POM_DIRS:
        make_version_snapshot(os.path.join(root_dir, pom_dir, 'pom.xml'))


if __name__ == '__main__':
    main(sys.argv[1] if len(sys.argv) > 1 else '.')
