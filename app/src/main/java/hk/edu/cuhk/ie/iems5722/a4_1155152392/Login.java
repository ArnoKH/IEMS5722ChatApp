package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class Login extends AsyncTask<String,Void,JSONObject> {

    interface LoginCallBack{
        void getData(JSONObject result);
    }
    Login.LoginCallBack cb;

    public Login(Login.LoginCallBack cb){
        super();
        this.cb=cb;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String method = params[0];
        String user = params[1];
        String e_psw = MD5.encrypt(params[2]);
        String json_string=null;
        JSONObject json = null;
        try {
            URL url = new URL("http://34.96.208.254/api/a3/login?method=" + method + "&user=" + user + "&epsw=" + e_psw);
            json_string = Download.downloadUrl(url);
            json = new JSONObject(json_string);
            return json;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        //Log.d("PostExecute","-----PostExecute-----");
        super.onPostExecute(result);
        cb.getData(result);
    }
}
