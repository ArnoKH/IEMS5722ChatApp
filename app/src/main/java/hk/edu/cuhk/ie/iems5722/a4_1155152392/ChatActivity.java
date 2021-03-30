//聊天室
package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    //各类控件
    private ImageButton mBtnBack, mBtnSend, mBtnReflesh;
    private TextView mTVtitle;
    private ListView mLvMSG;
    private EditText mEt;
    //方便用的全局变量
    private MsgListAdapter arrayAdapter;
    private int rid;//房间ID
    private String username, userid;
    private List<Msg> msglist = new ArrayList<>();//信息总列表

    private Socket mSocket;

    //通知用变量
    NotificationManager manager;
    NotificationChannel channel;
    String Channel_ID = "NewMSGNotyChan";

    //OnCreat()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("ChatActivity","-----ChatCreate-----");
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//防止直接弹输入法
        //加载消息列表相关变量
        final int[] cpage = new int[1];//已加载的页数
        final int[] tpage = new int[1];//总页数
        final boolean[] scrolltrigger = {true};//滚动触发开关=防止重复触发用
        //绑定控件
        mBtnBack = findViewById(R.id.toolbar_btn_c);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnReflesh = findViewById(R.id.toolbar_btn_reflesh);
        mTVtitle = findViewById(R.id.toolbar_text_c);
        mEt = findViewById(R.id.et);
        mLvMSG = findViewById(R.id.lv_msg_list);
        //设置房间信息
        String title=getIntent().getStringExtra("title");
        mTVtitle.setText(title);
        rid=Integer.parseInt(getIntent().getStringExtra("rid"));
        username = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("userid");
        //初始化ListView
        arrayAdapter = new MsgListAdapter(ChatActivity.this, R.layout.layout_msglist_item, msglist);
        mLvMSG.setAdapter(arrayAdapter);
        //启动WebSocket
        try {
            mSocket = IO.socket("http://34.96.208.254:8001");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        //mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on("ServerMSG", onServerMSG);
        mSocket.on("NewBroadcast", onNewBroadcast);
        mSocket.connect();
        //初始化通知渠道（Android8.0以上）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channel = new NotificationChannel(Channel_ID, "New Message", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("New Message Notification");
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //返回键的点击监听
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到MainActivity
                //Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                //startActivity(intent);
                //结束当前Activity
                mSocket.emit("leave", rid);
                finish();
            }
        });

        //刷新键的点击监听
        mBtnReflesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefleshMsgList mTask= new RefleshMsgList(new RefleshMsgList.RefleshCallBack(){
                    //回调：更新消息列表
                    @Override
                    public void getData(List<Msg> list) {
                        for(int i=0;i<list.size();i++){
                            newMsg(list.get(i).getUserName(),list.get(i).getContent(),list.get(i).getTime());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                    //回调：更新已加载页数和总页数
                    @Override
                    public void backData(int cp, int tp){
                        cpage[0] =cp;
                        tpage[0] =tp;
                    }
                });
                mTask.execute(rid,1);//只用于进房间首次刷新=加载第一页
            }
        });

        //发送键的点击监听
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEt.getText().toString().length() != 0) {
                    //截取信息
                    String content = mEt.getText().toString();
                    //截取时间
                    /*Calendar cal = Calendar.getInstance();
                    int y = cal.get(Calendar.YEAR);
                    int mon = cal.get(Calendar.MONTH)+1;
                    int d = cal.get(Calendar.DATE);
                    int h = cal.get(Calendar.HOUR_OF_DAY);
                    int mi = cal.get(Calendar.MINUTE);
                    int sec = cal.get(Calendar.SECOND);
                    String time = y+"-"+(mon<10?"0":"")+mon+"-"+(d<10?"0":"")+d+" "+(h<10?"0":"")+h+":"+(mi<10?"0":"")+mi+":"+(sec<10?"0":"")+sec;*/
                    //发送至服务器
                    PostMsg mTask = new PostMsg();
                    mTask.execute(Integer.toString(rid), userid, username, content);
                    //在本地消息列表添加新消息
                    //会触发服务器广播所以注释掉，以免重复添加
                    /*Msg newmsg = new Msg("LIU Kaihang",content,time);
                    msglist.add(newmsg);
                    //Log.d("11111111", content);
                    arrayAdapter.notifyDataSetChanged();*/
                    mEt.setText("");
                    //送出后非选中输入框
                    final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEt.getWindowToken(), 0);
                }
            }
        });

        //消息列表滚动监听
        mLvMSG.setOnScrollListener(new AbsListView.OnScrollListener() {
            //滚动状态改变监听
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            //滚动触发
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //到顶&有未加载页&开关开启时触发
                if(firstVisibleItem==0 && cpage[0]<tpage[0] && scrolltrigger[0]){
                    scrolltrigger[0] =false;//关闭开关防止连续触发
                    int readpage=cpage[0]+1;
                    RefleshMsgList mTask= new RefleshMsgList(new RefleshMsgList.RefleshCallBack(){
                        //回调：更新消息列表
                        @Override
                        public void getData(List<Msg> list) {
                            for(int i=0;i<list.size();i++){
                                newMsg(list.get(i).getUserName(),list.get(i).getContent(),list.get(i).getTime());
                            }
                            arrayAdapter.notifyDataSetChanged();
                        }
                        //回调：更新页数和开关设置
                        @Override
                        public void backData(int cp, int tp){
                            cpage[0] =cp;
                            tpage[0] =tp;
                            scrolltrigger[0] = true;
                        }
                    });
                    mTask.execute(rid,readpage);
                }
            }
        });
    }

    //扩展消息表
    private void newMsg(String name, String content, String time) {
        Msg newmsg = new Msg(name, content, time);
        msglist.add(0,newmsg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d("ChatActivity","-----ChatStart-----");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("ChatActivity","-----ChatRestart-----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("ChatActivity","-----ChatResume-----");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d("ChatActivity","-----ChatStop-----");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("ChatActivity","-----ChatPause-----");
    }

    @Override
    protected void onDestroy() {
        //Log.d("ChatActivity","-----ChatDestroy-----");
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT,onConnect);
        //mSocket.off(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.off("ServerMSG", onServerMSG);
        mSocket.off("NewBroadcast", onNewBroadcast);
        super.onDestroy();
    }

    //WebSocket连接成功后触发
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("join", rid);
        }
    };

    /*private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("leave", rid);
        }
    };*/

    //接收服务器消息Debug用
    private Emitter.Listener onServerMSG = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("ServerINFO", (String) args[0]);
        }
    };

    //收到服务器广播时触发
    private Emitter.Listener onNewBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                JSONArray array = data.getJSONArray("message");
                String username = array.getJSONObject(0).getString("name");
                String msg_content = array.getJSONObject(0).getString("message");
                String timestamp = array.getJSONObject(0).getString("message_time");
                Msg newmsg = new Msg(username,msg_content,timestamp);
                msglist.add(newmsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arrayAdapter.notifyDataSetChanged();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Notification.Builder builder = new Notification.Builder(ChatActivity.this, Channel_ID);
                            builder.setSmallIcon(R.drawable.cuhk);
                            builder.setContentTitle(username);
                            builder.setContentText(msg_content);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ChatActivity.this);
                            notificationManager.notify(NotificationID.getID(), builder.build());
                        } else {
                            Notification.Builder builder = new Notification.Builder(ChatActivity.this);
                            builder.setSmallIcon(R.drawable.cuhk);
                            builder.setContentTitle(username);
                            builder.setContentText(msg_content);
                            builder.setPriority(Notification.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ChatActivity.this);
                            notificationManager.notify(NotificationID.getID(), builder.build());
                        }
                    }
                });


            } catch(JSONException e) {
                throw new RuntimeException(e);
            }
        }
    };

}