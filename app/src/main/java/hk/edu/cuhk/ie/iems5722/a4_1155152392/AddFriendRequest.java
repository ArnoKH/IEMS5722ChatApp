package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AddFriendRequest extends AsyncTask<String, Void, String> {

    interface AddFriendCallBack{
        void getData(String status);
    }
    AddFriendRequest.AddFriendCallBack cb;

    public AddFriendRequest(AddFriendRequest.AddFriendCallBack cb){
        super();
        this.cb=cb;
    }

    @Override
    protected String doInBackground(String... params) {
        String hostusername = params[0];
        String hostuserid = params[1];
        String friendusername = params[2];
        String frienduserid = params[3];
        String json_string = null;
        JSONObject json = null;
        try {
            URL url = new URL("http://34.96.208.254/api/a3/add_friend?hname="+hostusername+"&hid="+hostuserid+"&fname="+friendusername+"&fid="+frienduserid);
            json_string=Download.downloadUrl(url);
            json = new JSONObject(json_string);
            return json.getString("status" );
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String status) {
        //Log.d("PostExecute","-----PostExecute-----");
        super.onPostExecute(status);
        cb.getData(status);
    }
}
