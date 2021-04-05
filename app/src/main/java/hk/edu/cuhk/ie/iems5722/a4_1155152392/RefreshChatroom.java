package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RefreshChatroom extends AsyncTask<Void, Void, List<Chatroom>> {

    interface RefreshCallBack {
        void getData(List<Chatroom> list);
    }
    RefreshCallBack cb;

    public RefreshChatroom(RefreshCallBack cb){
        super();
        this.cb=cb;
    }

    @Override
    protected List<Chatroom> doInBackground(Void... params) {
        //Log.d("InBackground","-----InBackground-----");
        List<Chatroom> roomlist = new ArrayList<Chatroom>();
        String json_string=null;
        JSONObject json = null;
        try {
            URL url = new URL("http://34.96.208.254/api/a3/get_chatrooms");
            json_string=Download.downloadUrl(url);
            json = new JSONObject(json_string);
            //String status = json.getString("status" ) ;
            JSONArray array = json.getJSONArray("data");
            for ( int i = 0; i < array.length(); i++) {
                String roomname = array.getJSONObject(i).getString("name");
                int roomid = array.getJSONObject(i).getInt("id");
                roomlist.add(new Chatroom(roomname, Integer.toString(roomid)));
            }
            return roomlist;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Chatroom> result) {
        //Log.d("PostExecute","-----PostExecute-----");
        super.onPostExecute(result);
        cb.getData(result);
    }


}
