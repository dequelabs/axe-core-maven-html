import xml.etree.ElementTree as ET
import sys
import os.path

ns = 'http://maven.apache.org/POM/4.0.0'
ss = '-SNAPSHOT'

# hack: register the namespace to avoid `.write`
# from writing the `ns0` all over the place
ET.register_namespace('', ns)

def make_version_snapshot(pom_dir):
    pom_path = os.path.join(pom_dir, 'pom.xml')
    tree = ET.parse(pom_path)

    root = tree.getroot()
    version_node = root.find('{%s}version' % ns)

    assert version_node is not None, 'No <version> key'

    if version_node.text.endswith(ss):
       sys.exit()

    version_node.text = version_node.text + ss

    parent_node = root.find('{%s}parent' % ns)
    if parent_node != None:
        parent_version_node = parent_node.find('{%s}version' % ns)
        parent_version_node.text = parent_version_node.text + ss

    deps = root.find('{%s}dependencies' % ns)
    if deps != None:
        for dep in deps.findall('{%s}dependency' % ns):
            group_id = dep.find('{%s}groupId' % ns).text
            if group_id == 'com.deque.html.axe-core':
                dep_version_node = dep.find('{%s}version' % ns)
                dep_version_node.text = dep_version_node.text + ss

    tree.write(pom_path, xml_declaration = True, encoding = 'utf-8', method = 'xml')
    print 'Added %s to version' % ss

pom_dirs = ['', 'selenium']

for dir in pom_dirs:
    make_version_snapshot(dir)