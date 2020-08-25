import xml.etree.ElementTree as ET
import sys

ns = 'http://maven.apache.org/POM/4.0.0'
ss = '-SNAPSHOT'

# hack: register the namespace to avoid `.write`
# from writing the `ns0` all over the place
ET.register_namespace('', ns)
tree = ET.parse('pom.xml')

root = tree.getroot()
version_node = root.find('{%s}version' % ns)

assert version_node is not None, 'No <version> key'

if version_node.text.endswith(ss):
   sys.exit()

version_node.text = version_node.text + ss

tree.write('pom.xml', xml_declaration = True, encoding = 'utf-8', method = 'xml')
print 'Added %s to version' % ss

