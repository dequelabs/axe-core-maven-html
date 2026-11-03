import base64
import os
import sys
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

root, log_dir, username, password = sys.argv[1:5]
expected = 'Basic ' + base64.b64encode(f'{username}:{password}'.encode()).decode()


class Handler(BaseHTTPRequestHandler):
    protocol_version = 'HTTP/1.1'

    def log_message(self, *args):
        pass

    def target(self):
        path = self.path.split('?')[0].lstrip('/')
        return os.path.join(root, *[p for p in path.split('/') if p not in ('', '.', '..')])

    def log(self, method):
        with open(os.path.join(log_dir, 'requests.log'), 'a') as f:
            f.write(f'{method} {self.path} {self.headers.get("Authorization") == expected}\n')

    def do_GET(self):
        self.log('GET')
        if os.path.exists(os.path.join(log_dir, 'redirect')) and not self.path.startswith('/elsewhere'):
            self.send_response(302)
            self.send_header('Location', open(os.path.join(log_dir, 'redirect')).read())
            self.send_header('Content-Length', '0')
            self.end_headers()
            return
        path = self.target()
        if os.path.isfile(path):
            data = open(path, 'rb').read()
            self.send_response(200)
            self.send_header('Content-Length', str(len(data)))
            self.end_headers()
            self.wfile.write(data)
        else:
            self.send_response(404)
            self.send_header('Content-Length', '0')
            self.end_headers()

    def do_HEAD(self):
        self.log('HEAD')
        path = self.target()
        self.send_response(200 if os.path.isfile(path) else 404)
        self.send_header('Content-Length', '0')
        self.end_headers()

    def do_PUT(self):
        self.log('PUT')
        body = self.rfile.read(int(self.headers.get('Content-Length', 0)))
        if self.headers.get('Authorization') != expected:
            self.send_response(401)
            self.send_header('WWW-Authenticate', 'Basic realm="stub"')
            self.send_header('Content-Length', '0')
            self.end_headers()
            return
        path = self.target()
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, 'wb') as f:
            f.write(body)
        self.send_response(201)
        self.send_header('Content-Length', '0')
        self.end_headers()


server = ThreadingHTTPServer(('127.0.0.1', 0), Handler)
with open(os.path.join(log_dir, 'port'), 'w') as f:
    f.write(str(server.server_address[1]))
server.serve_forever()
