package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Signup extends AsyncTask<String,Void,String> {

    interface SignupCallBack{
        void getData(String resultstatus);
    }
    Signup.SignupCallBack cb;

    public Signup(Signup.SignupCallBack cb){
        super();
        this.cb=cb;
    }

    @Override
    protected String doInBackground(String... params) {
        String username = params[0];
        String userid = params[1];
        String e_psw = MD5.encrypt(params[2]);
        String json_string=null;
        JSONObject json = null;
        try {
            URL url = new URL("http://34.96.208.254/api/a3/signup?username=" + username + "&userid=" + userid + "&epsw=" + e_psw);
            json_string = Download.downloadUrl(url);
            json = new JSONObject(json_string);
            String resultstatus = json.getString("status" ) ;
            return resultstatus;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String resultstatus) {
        //Log.d("PostExecute","-----PostExecute-----");
        super.onPostExecute(resultstatus);
        cb.getData(resultstatus);
    }
}
