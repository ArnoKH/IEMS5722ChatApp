package hk.edu.cuhk.ie.iems5722.a2_1155152392;

public class Msg {

    private final String msg_username;
    private final String msg_content;
    private final String msg_time;

    public Msg(String name,String msg_content,String time){
        this.msg_username = name;
        this.msg_content = msg_content;
        this.msg_time = time;//(time_h<10?"0":"") + time_h +":"+ (time_m<10?"0":"") + time_m;
    }
    public String getUserName(){
        return msg_username;
    }
    public String getContent(){
        return msg_content;
    }
    public String getTime(){
        return msg_time;
    }
}
