from flask import Flask
from flask import jsonify
from flask import request
from flask_socketio import SocketIO, emit, join_room, leave_room
import json

app = Flask(__name__)
#app.config['DEBUG'] = True
app.config['SECRET_KEY'] = 'iems5722'
socketio = SocketIO(app)

@app.route('/api/a4/broadcast_room', methods=['POST'])
def broadcast_room():
	tempdata = request.data.decode('utf-8')
	jsondata = json.loads(tempdata)
	if((jsondata['chatroom_id'] is None) or (jsondata['message'] is None)):
		return jsonify(status="ERROR", message="Parameters Not Correct")
	else:
		socketio.emit("NewBroadcast", jsondata, broadcast=True)
		return jsonify(status="OK")

@socketio.on('connect')
def connect_handler():
	print("Client Connected")

@socketio.on('disconnect')
def disconnect_handler():
	print("Client Disonnected")

@socketio.on('join')
def on_join(data):
	join_room(data)
	emit("ServerMSG","Someone has entered the room")

@socketio.on('leave')
def on_leave(data):
	leave_room(data)
	emit("ServerMSG","Someone has left the room")
	
if __name__ == '__main__':
	socketio.run(app, host='127.0.0.1', port=8001)