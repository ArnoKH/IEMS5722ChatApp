from flask import Flask
from flask import jsonify
from flask import request
from flask import g
import mysql.connector

app = Flask(__name__)

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
		return jsonify(status="OK")

if __name__ == '__main__':
	app.run()