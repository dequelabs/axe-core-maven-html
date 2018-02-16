/*
 * Test target application that just serves a simple HTML page
 */
require('http').createServer(function (req, res) {
	if(req.url === '/shadow-error.html') {
		res.writeHead(200, { 'Content-Type': 'text/html' });
        res.end(`
			<!doctype html>
			<html lang="en">
				<head>
					<title>Test Page</title>
				</head>
				<body>
					<div role="main" id="host">
						<h1>This is a test</h1>
						<p>This is a test page with no violations</p>
					</div>
					<div role="contentinfo" id="upside-down"></divid>
					<script>
						var shadow = document.getElementById("upside-down").attachShadow({mode: "open"});
						shadow.innerHTML = '<h2 id="shadow">SHADOW DOM</h2><ul><p>Not a List Item 1</p></ul>'
					</script>
				</body>
			</html>
		`);
	} else {
        res.writeHead(200, { 'Content-Type': 'text/html' });
        res.end(`
			<!doctype html>
			<html lang="en">
				<head>
					<title>Test Page</title>
				</head>
				<body>
					<div role="main" id="host">
						<h1>This is a test</h1>
						<p>This is a test page with no violations</p>
					</div>
					<div role="contentinfo" id="upside-down"></divid>
					<script>
						var shadow = document.getElementById("upside-down").attachShadow({mode: "open"});
						shadow.innerHTML = '<h2 id="shadow">SHADOW DOM</h2><ul><li>Shadow Item 1</li></ul>'
					</script>
				</body>
			</html>
	`);
	}

}).listen('5005');