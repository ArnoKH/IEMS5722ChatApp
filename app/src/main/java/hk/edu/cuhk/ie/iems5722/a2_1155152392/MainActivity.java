package hk.edu.cuhk.ie.iems5722.a2_1155152392;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,ChatroomListAdapter.Callback {

    private final List<Chatroom> ctroomlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.d("MainActivity","-----MainCreate-----");
        ListView mLVct = findViewById(R.id.lv_chatroom_list);
        ChatroomListAdapter arrayAdapter = new ChatroomListAdapter(MainActivity.this,R.layout.layout_chatroomlist_item,ctroomlist,this);
        mLVct.setAdapter(arrayAdapter);
        RefleshChatroom mTask = new RefleshChatroom(new RefleshChatroom.RefleshCallBack() {

            @Override
            public void getData(List<Chatroom> list) {
                for(int i=0;i<list.size();i++){
                    newChatroom(list.get(i).getRoomname(),list.get(i).getRoomID());
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
        mTask.execute();
        mLVct.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

    }

    @Override
    public void click(View v){
        Button mBtn_room=v.findViewById(R.id.btn_roomname);
        TextView mTv_rid=((View)v.getParent()).findViewById(R.id.tv_roomid);
        Intent intent = new Intent(MainActivity.this,ChatActivity.class);
        intent.putExtra("title",mBtn_room.getText());
        intent.putExtra("rid", mTv_rid.getText());
        startActivity(intent);
    }

    private void newChatroom(String rm, String id) {
        Chatroom newchatroom = new Chatroom(rm,id);
        ctroomlist.add(newchatroom);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("MainActivity","-----MainStart-----");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("MainActivity","-----MainRestart-----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("MainActivity","-----MainResume-----");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d("MainActivity","-----MainStop-----");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("MainActivity","-----MainPause-----");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d("MainActivity","-----MainDestroy-----");
    }
}