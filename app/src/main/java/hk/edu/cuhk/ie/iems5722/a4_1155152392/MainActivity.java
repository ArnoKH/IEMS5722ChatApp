package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ChatroomListAdapter.Callback {

    private List<Chatroom> ctroomlist = new ArrayList<>();
    private ChatroomListAdapter arrayAdapter;
    private ListView mLVct;
    private TextView mTvLoginID;
    private Toolbar mToolbar;

    private String username, userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.d("MainActivity","-----MainCreate-----");
        mToolbar = findViewById(R.id.toolbar_m);
        setSupportActionBar(mToolbar);
        username = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("userid");
        mLVct = findViewById(R.id.lv_chatroom_list);
        mTvLoginID = findViewById(R.id.tv_loginas_username);
        mTvLoginID.setText("Logged in as "+username);
        arrayAdapter = new ChatroomListAdapter(MainActivity.this, R.layout.layout_chatroomlist_item, ctroomlist, this);
        mLVct.setAdapter(arrayAdapter);
        RefreshChatroom mTask = new RefreshChatroom(new RefreshChatroom.RefreshCallBack() {
            //回调：更新房间列表
            @Override
            public void getData(List<Chatroom> list) {
                ctroomlist.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }
        });
        mTask.execute();
        mLVct.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_userinfo) {
            Intent intent = new Intent(MainActivity.this, FriendListActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("userid", userid);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
    }

    @Override
    public void click(View v) {
        Button mBtn_room = v.findViewById(R.id.btn_roomname);
        TextView mTv_rid = ((View) v.getParent()).findViewById(R.id.tv_roomid);
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("title", mBtn_room.getText());
        intent.putExtra("rid", mTv_rid.getText());
        intent.putExtra("username", username);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }

    private void newChatroom(String rm, String id) {
        Chatroom newchatroom = new Chatroom(rm, id);
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