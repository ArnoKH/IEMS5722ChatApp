package hk.edu.cuhk.ie.iems5722.a4_1155152392;

public class User {
    private final String username;
    private final String userid;

    public User(String name,String id) {
        this.username=name;
        this.userid=id;
    }
    public String getUserName(){
        return username;
    }
    public String getUserID(){
        return userid;
    }
}
