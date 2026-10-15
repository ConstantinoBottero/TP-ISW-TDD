'use strict';

// Small local-only server used to review the prototype and seek inside MP4 files.
const http = require('http');
const fs = require('fs');
const path = require('path');
const root = path.join(__dirname, 'dist');
const types = { '.css': 'text/css; charset=utf-8', '.html': 'text/html; charset=utf-8', '.jpg': 'image/jpeg', '.js': 'text/javascript; charset=utf-8', '.mp4': 'video/mp4', '.webm': 'video/webm', '.png': 'image/png', '.ttf': 'font/ttf' };

http.createServer((request, response) => {
  const urlPath = decodeURIComponent((request.url || '/').split('?')[0]);
  const relative = urlPath === '/' ? 'index.html' : urlPath.replace(/^[/\\]+/, '');
  const target = path.resolve(root, relative);
  if (!target.startsWith(root + path.sep) && target !== root) { response.writeHead(403).end(); return; }
  fs.stat(target, (statError, stat) => {
    if (statError || !stat.isFile()) { response.writeHead(404).end(); return; }
    const headers = { 'Content-Type': types[path.extname(target).toLowerCase()] || 'application/octet-stream', 'Accept-Ranges': 'bytes', 'Cache-Control': 'no-store' };
    const range = request.headers.range;
    if (!range) { response.writeHead(200, { ...headers, 'Content-Length': stat.size }); fs.createReadStream(target).pipe(response); return; }
    const match = /^bytes=(\d*)-(\d*)$/.exec(range);
    const start = match && match[1] ? Number(match[1]) : 0;
    const end = match && match[2] ? Math.min(Number(match[2]), stat.size - 1) : stat.size - 1;
    if (!match || start > end || start >= stat.size) { response.writeHead(416, { 'Content-Range': `bytes */${stat.size}` }).end(); return; }
    response.writeHead(206, { ...headers, 'Content-Length': end - start + 1, 'Content-Range': `bytes ${start}-${end}/${stat.size}` });
    fs.createReadStream(target, { start, end }).pipe(response);
  });
}).listen(8766, '127.0.0.1', () => console.log('EcoHarmony local review: http://127.0.0.1:8766'));
