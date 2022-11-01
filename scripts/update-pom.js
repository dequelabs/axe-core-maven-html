const xj = require("xml-js");

// project.version
const getVersionElement = (xml) => {
  const elements = xml.elements[0].elements;
  const version = elements.find(
    (e) => e.name === "version" && e.type === "element"
  );
  return version;
};

// project.parent.version
const getParentVersionElement = (xml) => {
  const elements = xml.elements[0].elements;
  const parent = elements.find(
    (e) => e.name === "parent" && e.type === "element"
  );
  const version = parent?.elements.find(
    (e) => e.name === "version" && e.type === "element"
  );
  return version || null;
};

// project.dependencies.dependency[groupdId=com.deque.html.axe-core]
const getAxeCoreDependencyVersionElement = (xml) => {
  const elements = xml.elements[0].elements;
  const dependencies = elements
    .find((e) => e.name === "dependencies" && e.type === "element")
    ?.elements.filter((e) => e.name === "dependency" && e.type === "element");

  for (const dependency of dependencies || []) {
    const groupId = dependency.elements.find(
      (e) => e.name === "groupId" && e.type === "element"
    );
    if (groupId.elements[0].text !== "com.deque.html.axe-core") {
      continue;
    }
    const version = dependency.elements.find(
      (e) => e.name === "version" && e.type === "element"
    );
    return version;
  }
};

exports.readVersion = (contents) => {
  const xml = xj.xml2js(contents, { compact: false });
  const versionElement = getVersionElement(xml);
  return versionElement.elements[0].text;
};

exports.writeVersion = (contents, version) => {
  const xml = xj.xml2js(contents, { compact: false });
  const versionElement = getVersionElement(xml);
  versionElement.elements[0].text = version;

  const parentElement = getParentVersionElement(xml);
  if (parentElement) {
    parentElement.elements[0].text = version;
  }

  const dependency = getAxeCoreDependencyVersionElement(xml);
  if (dependency) {
    dependency.elements[0].text = version;
  }

  return xj.js2xml(xml, { indentText: false, spaces: 2, compact: false });
};
