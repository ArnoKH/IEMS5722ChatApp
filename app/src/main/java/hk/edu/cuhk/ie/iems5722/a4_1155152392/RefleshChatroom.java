package hk.edu.cuhk.ie.iems5722.a4_1155152392;

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

public class RefleshChatroom extends AsyncTask<Void, Void, List<Chatroom>> {

    interface RefleshCallBack{
        public void getData(List<Chatroom> list);
    }
    RefleshCallBack cb;

    public RefleshChatroom(RefleshCallBack cb){
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
            json_string=downloadUrl(url);
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
