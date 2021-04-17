package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class SearchUser extends AsyncTask<String, Void, User> {

    interface SearchUserCallBack{
        void getData(User result);
    }
    SearchUser.SearchUserCallBack cb;

    public SearchUser(SearchUser.SearchUserCallBack cb){
        super();
        this.cb=cb;
    }

    @Override
    protected User doInBackground(String... params) {
        String uid = params[0];
        String json_string = null;
        JSONObject json = null;
        try {
            URL url = new URL("http://34.96.208.254/api/a3/search_users?uid="+uid);
            json_string=Download.downloadUrl(url);
            json = new JSONObject(json_string);
            String status = json.getString("status" ) ;
            if(status.equals("OK")){
                JSONArray array = json.getJSONArray("data");
                String uname = array.getJSONObject(0).getString("name");
                return new User(uname,uid);
            } else return null;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(User result) {
        //Log.d("PostExecute","-----PostExecute-----");
        super.onPostExecute(result);
        cb.getData(result);
    }
}
