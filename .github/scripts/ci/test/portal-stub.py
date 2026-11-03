import json
import os
import sys
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, urlparse

log_dir = sys.argv[1]
states = sys.argv[2].split(',')
status_calls = 0


class Handler(BaseHTTPRequestHandler):
    def log_message(self, *args):
        pass

    def do_POST(self):
        global status_calls
        url = urlparse(self.path)
        body = self.rfile.read(int(self.headers.get('Content-Length', 0)))
        if url.path == '/api/v1/publisher/upload':
            with open(os.path.join(log_dir, 'upload.body'), 'wb') as f:
                f.write(body)
            with open(os.path.join(log_dir, 'upload.json'), 'w') as f:
                json.dump({
                    'query': parse_qs(url.query),
                    'authorization': self.headers.get('Authorization'),
                    'content_type': self.headers.get('Content-Type'),
                }, f)
            self.send_response(201)
            self.end_headers()
            self.wfile.write(b'deployment-123')
        elif url.path == '/api/v1/publisher/status':
            state = states[min(status_calls, len(states) - 1)]
            status_calls += 1
            with open(os.path.join(log_dir, 'status.log'), 'a') as f:
                f.write(parse_qs(url.query)['id'][0] + '\n')
            payload = {'deploymentId': 'deployment-123', 'deploymentState': state}
            if state == 'FAILED':
                payload['errors'] = {'pkg': ['Invalid signature']}
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            self.wfile.write(json.dumps(payload).encode())
        else:
            self.send_response(404)
            self.end_headers()


server = ThreadingHTTPServer(('127.0.0.1', 0), Handler)
with open(os.path.join(log_dir, 'port'), 'w') as f:
    f.write(str(server.server_address[1]))
server.serve_forever()
