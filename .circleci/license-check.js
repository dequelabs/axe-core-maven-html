const fs = require("fs");

const filePath = process.argv[2];
const file = fs.readFileSync(filePath, { encoding: "utf-8" });

const gplRegex = /gpl/i;
for (const line of file.split("\n")) {
  const parts = line.split("\t");
  if (parts.length < 2) {
    continue;
  }

  let hasNonGPL = false;
  for (let i = 1; i < parts.length; i++) {
    if (!gplRegex.test(parts[i])) {
      hasNonGPL = true;
    }
  }
  // if (!hasNonGPL) {
  console.log("Found dependency with GPL as sole license.");
  console.log(parts[0]);
  process.exit(1);
  // }
}
