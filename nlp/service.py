#!/usr/bin/env python3

import http
import http.server
import json
import postgresql
import datetime
import doc2vec

DATABASE = 'pq://postgres:postgres@localhost:5432/codreams'

WORD2VEC_MODEL_PATH = "../models/GoogleNews-vectors-negative300.bin.gz"

class BackendService(http.server.BaseHTTPRequestHandler):

	class DBConnection(object):
		def __init__(self):
			global DATABASE
			self.pg = postgresql.open(DATABASE)
			self.ins = self.pg.prepare("INSERT INTO requests (username, location, request, time) VALUES ($1, $2, $3, $4)")

		def destroy(self):
			self.pg.close()

		def store_request(self, user, location, request):
			curtime = datetime.datetime.now()
			self.ins(user, location, request, str(curtime))

	def __init__(self):
		global WORD2VEC_MODEL_PATH
		#self.db = DBConnection()
		self.doc2vec = doc2vec.Doc2Vec(WORD2VEC_MODEL_PATH)

	def __process_request(self, request):
		username = request["username"]
		ip = request["ip"]
		location = request["location"]
		req = request["request"]
		self.db.store_request(username, location, req)

	def do_POST(self):
		content_len = self.headers['Content-Length']
		if content_len is None:
			post_body = "{}"
		else:
			post_body = self.rfile.read(int(content_len)).decode("utf-8")

		try:
			request = json.loads(post_body)
			self.__process_request(request)
		except Exception as e:
			self.send_response(400)
			self.end_headers()
			print("Problem: %s" % str(e))
			return


		self.send_response(200)
		self.send_header('Content-type','text/json')
		self.end_headers()

		response = {
			"data" : "hello world",
		}
		message = json.dumps(response)

		# Write content as utf-8 data
		self.wfile.write(bytes(message, "utf-8"))
		return

httpd = http.server.HTTPServer(("", 8000), BackendService)
httpd.serve_forever()

