package hk.edu.cuhk.ie.iems5722.a2_1155152392;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RefleshMsgList extends AsyncTask<Integer, Void, List<Msg>>{

    private int current_page,total_pages;

    interface RefleshCallBack{
        void getData(List<Msg> list);
        void backData(int cp, int tp);
    }
    RefleshCallBack cb;

    public RefleshMsgList(RefleshCallBack cb){
        super();
        this.cb=cb;
    }

    @Override
    protected List<Msg> doInBackground(Integer... params) {
        //Log.d("InBackground","-----InBackground-----");
        int chatroom_id=params[0];
        int page=params[1];
        List<Msg> msglist = new ArrayList<Msg>();
        String json_string=null;
        JSONObject json = null;
        try {
            URL url = new URL("http://3.135.234.121/api/a2/get_messages?chatroom_id="+chatroom_id+"&page="+page);
            json_string=downloadUrl(url);
            json = new JSONObject(json_string);
            //String status = json.getString("status" ) ;
            JSONObject data = json.getJSONObject("data");
            current_page = data.getInt("current_page");
            total_pages = data.getInt("total_pages");
            JSONArray array = data.getJSONArray("messages");
            for ( int i = 0; i < array.length(); i++) {
                String username = array.getJSONObject(i).getString("name");
                String msg_content = array.getJSONObject(i).getString("message");
                String timestamp = array.getJSONObject(i).getString("message_time");
                msglist.add(new Msg(username,msg_content,timestamp));
            }
            return msglist;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Msg> result) {
        //Log.d("PostExecute","-----PostExecute-----");
        super.onPostExecute(result);
        cb.getData(result);
        cb.backData(current_page, total_pages);
    }

    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = "";
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = connection.getInputStream();
            if (stream != null) {
                String line ;
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                while ( (line = br.readLine()) != null ) {
                    result += line ;
                }
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        //System.out.println(result);
        return result;
    }
}
