package hk.edu.cuhk.ie.iems5722.a2_1155152392;

public class Chatroom {
    private final String roomname;
    private final String roomid;

    public Chatroom(String rm,String id){
        this.roomname = rm;
        this.roomid = id;
    }
    public String getRoomname(){
        return roomname;
    }
    public String getRoomID(){
        return roomid;
    }
}
