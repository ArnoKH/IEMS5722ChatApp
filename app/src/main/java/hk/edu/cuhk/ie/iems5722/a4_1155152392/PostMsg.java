package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PostMsg extends AsyncTask<String,Void,Void> {
    @Override
    protected Void doInBackground(String... params) {
        //设置参数
        List<String> para_name = new ArrayList<String>();
        para_name.add("chatroom_id");
        para_name.add("user_id");
        para_name.add("name");
        para_name.add("message");
        List<String> para_values = new ArrayList<String>();
        para_values.add(params[0]);
        para_values.add(params[1]);
        para_values.add(params[2]);
        para_values.add(params[3]);
        try {
            upload(para_name,para_values);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void upload(List<String> para_name,List<String> para_values) throws IOException {
        java.net.URL url = new URL("http://34.96.208.254/api/a3/send_message");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter write = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
        Uri.Builder builder = new Uri.Builder();

        for(int i=0;i<para_name.size();i++){
            builder.appendQueryParameter(para_name.get(i),para_values.get(i));
        }
        String query = builder.build().getEncodedQuery();
        //System.out.println(query);
        write.write(query);
        write.flush();
        write.close();
        os.close();

        int responseCode = conn.getResponseCode();//没有这行会post失败
    }
}
