package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RefreshFriendList extends AsyncTask<String, Void, List<User>> {

    interface RefreshCallBack{
        void getData(List<User> list);
    }
    RefreshFriendList.RefreshCallBack cb;

    public RefreshFriendList(RefreshFriendList.RefreshCallBack cb){
        super();
        this.cb=cb;
    }

    @Override
    protected List<User> doInBackground(String... params) {
        List<User> frienduserlist = new ArrayList<User>();
        String userid = params[0];
        String json_string=null;
        JSONObject json = null;
        try {
            URL url = new URL("http://34.96.208.254/api/a3/get_friend_list?userid="+userid);
            json_string=Download.downloadUrl(url);
            json = new JSONObject(json_string);
            //String status = json.getString("status" ) ;
            JSONArray array = json.getJSONArray("data");
            for ( int i = 0; i < array.length(); i++) {
                String friendname = array.getJSONObject(i).getString("friendusername");
                int friendid = array.getJSONObject(i).getInt("frienduserid");
                frienduserlist.add(new User(friendname, Integer.toString(friendid)));
            }
            return frienduserlist;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<User> result) {
        //Log.d("PostExecute","-----PostExecute-----");
        super.onPostExecute(result);
        cb.getData(result);
    }
}
