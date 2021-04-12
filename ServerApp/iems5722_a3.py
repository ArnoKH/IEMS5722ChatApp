from flask import Flask
from flask import jsonify
from flask import request
from flask import g
from flask import json as fjson
import mysql.connector
import requests,json,datetime

app = Flask(__name__)
#app.config['DEBUG'] = True

class MyDatabase:
	conn = None
	cursor = None
	def __init__(self):
		self.connect()
		return
	def connect(self):
		self.conn = mysql.connector.connect(
			host = "localhost",
			port = 3306,
			user = "lkh",
			password = "123456",
			database = "iems5722",
		)
		self.cursor = self.conn.cursor(dictionary = True)
		return

class DateEncoder(json.JSONEncoder):
	def default(self, obj):
		if isinstance(obj,datetime.datetime):
			return fjson.JSONEncoder.default(self,obj)
		else:
			return json.JSONEncoder.default(self,obj)

@app.before_request
def before_request():
	g.mydb = MyDatabase()
	return

@app.teardown_request
def teardown_request(exception):
	mydb = getattr(g,"mydb", None)
	if mydb is not None:
		mydb.conn.close()
	return

@app.route('/api/a3/get_chatrooms')
def get_chatrooms():
	query = "SELECT * FROM chatrooms"
	g.mydb.cursor.execute(query)
	chatrooms = g.mydb.cursor.fetchall()
	return jsonify(status="OK", data=chatrooms)

@app.route('/api/a3/get_messages')
def get_messages():
	msgppage = 5
	chatroom_id = request.args.get("chatroom_id")
	page = int(request.args.get("page"))
	query = "SELECT * FROM messages WHERE chatroom_id = %s ORDER BY id DESC"
	params = (chatroom_id,)
	g.mydb.cursor.execute(query,params)
	allmessage = g.mydb.cursor.fetchall()
	totalmsg = g.mydb.cursor.rowcount
	i = (page-1)*msgppage
	r = totalmsg%msgppage
	if(r==0):
		totalpage = int(totalmsg/msgppage)
	else:
		totalpage = int((totalmsg//msgppage)+1)
	if(page>totalpage):
		return jsonify(status="ERROR", message="Page Not Exist")
	elif(page==totalpage):
		message = allmessage[i:]
		datas = {"current_page": page, "messages": message, "total_pages": totalpage}
		return jsonify(status="OK", data=datas)
	else:
		j=i+msgppage
		message = allmessage[i:j]
		datas = {"current_page": page, "messages": message, "total_pages": totalpage}
		return jsonify(status="OK", data=datas)

@app.route('/api/a3/send_message', methods=['POST'])
def send_message():
	chatroom_id = request.form.get("chatroom_id")
	user_id = request.form.get("user_id")
	name = request.form.get("name")
	message = request.form.get("message")
	if((chatroom_id is None) or (user_id is None) or (name is None) or (message is None)):
		return jsonify(status="ERROR", message="Parameters Not Correct")
	else:
		query = "INSERT INTO messages(chatroom_id,user_id,name,message,message_time) VALUES (%s,%s,%s,%s,NOW())"
		params = (int(chatroom_id),int(user_id),name,message)
		g.mydb.cursor.execute(query,params)
		g.mydb.conn.commit()
		query = "SELECT * FROM messages WHERE chatroom_id = %s ORDER BY id DESC LIMIT 1"
		params = (chatroom_id,)
		g.mydb.cursor.execute(query,params)
		topmessage = g.mydb.cursor.fetchall()
		payload = {"chatroom_id": chatroom_id, "message": topmessage}
		resp = requests.post("http://localhost:8001/api/a4/broadcast_room", data = json.dumps(payload,cls=DateEncoder))
		return jsonify(status="OK")

@app.route('/api/a3/signup')
def signup():
	uname = request.args.get("username")
	uid = request.args.get("userid")
	epsw = request.args.get("epsw")
	query = "SELECT * FROM userinfos WHERE name = %s"
	params = (uname,)
	g.mydb.cursor.execute(query,params)
	t = g.mydb.cursor.fetchall()
	t1 = g.mydb.cursor.rowcount
	query = "SELECT * FROM userinfos WHERE uid = %s"
	params = (int(uid),)
	g.mydb.cursor.execute(query,params)
	t = g.mydb.cursor.fetchall()
	t2 = g.mydb.cursor.rowcount
	if(t1 > 0):
		return jsonify(status="Username already exist.")
	elif(t2 > 0):
		return jsonify(status="UserID already exist.")
	else:
		query = "INSERT INTO userinfos(name,uid,epsw) VALUES (%s,%s,%s)"
		params = (uname,int(uid),epsw)
		g.mydb.cursor.execute(query,params)
		g.mydb.conn.commit()
		return jsonify(status="OK")

@app.route('/api/a3/login')
def login():
	method = int(request.args.get("method"))
	user = request.args.get("user")
	epsw = request.args.get("epsw")
	if(method == 0):
		query = "SELECT name,epsw FROM userinfos WHERE uid = %s"
		params = (int(user),)
		g.mydb.cursor.execute(query,params)
		t = g.mydb.cursor.fetchall()
		r = g.mydb.cursor.rowcount
		if(r == 0):
			return jsonify(status="UserID does not exist.")
		elif(epsw == t[0]["epsw"]):
			return jsonify(status="OK", username=t[0]["name"], userid=int(user))
		else:
			return jsonify(status="Password incorrect.")
	else:
		query = "SELECT uid,epsw FROM userinfos WHERE name = %s"
		params = (user,)
		g.mydb.cursor.execute(query,params)
		t = g.mydb.cursor.fetchall()
		r = g.mydb.cursor.rowcount
		if(r == 0):
			return jsonify(status="Username does not exist.")
		elif(epsw == t[0]["epsw"]):
			return jsonify(status="OK", username=user, userid=t[0]["uid"])
		else:
			return jsonify(status="Password incorrect.")

@app.route('/api/a3/get_friend_list')
def get_friend_list():
	userid = request.args.get("userid")
	query = "SELECT friendusername,frienduserid FROM friends WHERE hostuserid = %s"
	params = (int(userid),)
	g.mydb.cursor.execute(query,params)
	data = g.mydb.cursor.fetchall()
	return jsonify(status="OK", data=data)

@app.route('/api/a3/add_friend')
def add_friend():
	hname = request.args.get("hname")
	hid = request.args.get("hid")
	fname = request.args.get("fname")
	fid = request.args.get("fid")
	query = "SELECT * FROM friends WHERE hostuserid = %s AND frienduserid = %s"
	params = (int(hid),int(fid))
	g.mydb.cursor.execute(query,params)
	t = g.mydb.cursor.fetchall()
	r1 = g.mydb.cursor.rowcount
	params = (int(fid),int(hid))
	g.mydb.cursor.execute(query,params)
	t = g.mydb.cursor.fetchall()
	r2 = g.mydb.cursor.rowcount
	if r1 == 1:
		return jsonify(status="Already friend.")
	if r1 == 0:
		query = "INSERT INTO friends(hostuserid,friendusername,frienduserid) VALUES (%s,%s,%s)"
		params = (int(hid),int(fid),fname)
		g.mydb.cursor.execute(query,params)
		g.mydb.conn.commit()
		if r2 == 0:
			query = "INSERT INTO friends(hostuserid,friendusername,frienduserid) VALUES (%s,%s,%s)"
			params = (int(fid),int(hid),hname)
			g.mydb.cursor.execute(query,params)
			g.mydb.conn.commit()
		return jsonify(status="OK")
	return jsonify(status="ERROR")

if __name__ == '__main__':
	app.run(host='127.0.0.1', port=8000)