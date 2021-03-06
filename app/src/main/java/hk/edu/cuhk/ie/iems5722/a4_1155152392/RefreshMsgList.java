package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RefreshMsgList extends AsyncTask<Integer, Void, List<Msg>> {

    private int current_page,total_pages;
    private int result_code = 2;
    private final int MY_RESULT_OK = 1;
    private final int MY_RESULT_ERROR = 2;

    interface RefreshCallBack {
        void getData(List<Msg> list, int cp, int tp, int resultcode);
    }
    RefreshCallBack cb;

    public RefreshMsgList(RefreshCallBack cb){
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
            URL url = new URL("http://34.96.208.254/api/a3/get_messages?chatroom_id="+chatroom_id+"&page="+page);
            json_string=Download.downloadUrl(url);
            json = new JSONObject(json_string);
            String status = json.getString("status") ;
            if(status.equals("OK")){
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
                result_code = 1;
                return msglist;
            } else if(status.equals("ERROR")){
                return null;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Msg> result) {
        //Log.d("PostExecute","-----PostExecute-----");
        super.onPostExecute(result);
        if(result_code == 1) {
            cb.getData(result, current_page, total_pages, MY_RESULT_OK);
        } else {
            cb.getData(null, 0, 0, MY_RESULT_ERROR);
        }
    }

}
