package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NearbyUsersRequest extends AsyncTask<String, Void, List<User>> {

    interface NearbyUsersCallBack{
        void getData(List<User> list);
    }
    NearbyUsersRequest.NearbyUsersCallBack cb;

    public NearbyUsersRequest(NearbyUsersRequest.NearbyUsersCallBack cb){
        super();
        this.cb=cb;
    }

    @Override
    protected List<User> doInBackground(String... params) {
        List<User> nearbyuserlist = new ArrayList<User>();
        String hostusername = params[0];
        String hostuserid = params[1];
        String hostlocation = params[2];
        String json_string = null;
        JSONObject json = null;
        try {
            URL url = new URL("http://34.96.208.254/api/a3/nearby_users?hname="+hostusername+"&hid="+hostuserid+"&location="+hostlocation);
            json_string=Download.downloadUrl(url);
            json = new JSONObject(json_string);
            //String status = json.getString("status" ) ;
            JSONArray array = json.getJSONArray("data");
            for ( int i = 0; i < array.length(); i++) {
                String username = array.getJSONObject(i).getString("username");
                int userid = array.getJSONObject(i).getInt("userid");
                nearbyuserlist.add(new User(username, Integer.toString(userid)));
            }
            return nearbyuserlist;
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
