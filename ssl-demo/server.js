"use strict";
var https = require("https");
var fs = require("fs");
var path = require("path");
var minimumTLSVersion = require('minimum-tls-version');
var port = process.argv[2] || 8043;

var server;

var options = {
  key: fs.readFileSync("/home/bpaulin/localhost.key"),
  cert: fs.readFileSync("/home/bpaulin/localhost.crt"),
  secureOptions: minimumTLSVersion('tlsv11')
};

function app(req, res) {
  res.setHeader('Content-Type', 'text/plain');
  res.end('Hello, encrypted world!');
}

server = https.createServer(options, app).listen(port, function() {
  port = server.address().port;
});
