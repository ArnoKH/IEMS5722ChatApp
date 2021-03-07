package hk.edu.cuhk.ie.iems5722.a2_1155152392;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageButton mBtnBack, mBtnSend, mBtnReflesh;
    private TextView mTVtitle;
    private ListView mLvMSG;
    private EditText mEt;
    private int rid;

    private final List<Msg> msglist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Log.d("ChatActivity","-----ChatCreate-----");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final int[] cpage = new int[1];
        final int[] tpage = new int[1];
        final boolean[] scrolltrigger = {true};

        mBtnBack = findViewById(R.id.toolbar_btn_c);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnReflesh = findViewById(R.id.toolbar_btn_reflesh);
        mTVtitle = findViewById(R.id.toolbar_text_c);
        mEt = findViewById(R.id.et);
        mLvMSG = findViewById(R.id.lv_msg_list);

        String title=getIntent().getStringExtra("title");
        mTVtitle.setText(title);
        rid=Integer.parseInt(getIntent().getStringExtra("rid"));

        MsgListAdapter arrayAdapter = new MsgListAdapter(ChatActivity.this, R.layout.layout_msglist_item, msglist);
        mLvMSG.setAdapter(arrayAdapter);

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到MainActivity
                //Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                //startActivity(intent);
                //结束当前Activity
                finish();
            }
        });

        mBtnReflesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefleshMsgList mTask= new RefleshMsgList(new RefleshMsgList.RefleshCallBack(){
                    @Override
                    public void getData(List<Msg> list) {
                        for(int i=0;i<list.size();i++){
                            newMsg(list.get(i).getUserName(),list.get(i).getContent(),list.get(i).getTime());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void backData(int cp, int tp){
                        cpage[0] =cp;
                        tpage[0] =tp;
                    }
                });
                mTask.execute(rid,1);
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEt.getText().toString().length() != 0) {
                    //截取信息
                    String content = mEt.getText().toString();
                    //截取时间
                    Calendar cal = Calendar.getInstance();
                    int y = cal.get(Calendar.YEAR);
                    int mon = cal.get(Calendar.MONTH)+1;
                    int d = cal.get(Calendar.DATE);
                    int h = cal.get(Calendar.HOUR_OF_DAY);
                    int mi = cal.get(Calendar.MINUTE);
                    int sec = cal.get(Calendar.SECOND);
                    String time = y+"-"+(mon<10?"0":"")+mon+"-"+(d<10?"0":"")+d+" "+(h<10?"0":"")+h+":"+(mi<10?"0":"")+mi+":"+(sec<10?"0":"")+sec;
                    //发送至服务器
                    PostMsg mTask = new PostMsg();
                    mTask.execute(Integer.toString(rid),"1155152392","LIU Kaihang",content);
                    //添加新消息
                    Msg newmsg = new Msg("LIU Kaihang",content,time);
                    msglist.add(newmsg);
                    //Log.d("11111111", content);
                    arrayAdapter.notifyDataSetChanged();
                    mEt.setText("");
                    // close input method after send
                    final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEt.getWindowToken(), 0);
                }
            }
        });

        mLvMSG.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem==0 && cpage[0]<tpage[0] && scrolltrigger[0]){
                    scrolltrigger[0] =false;
                    int readpage=cpage[0]+1;
                    RefleshMsgList mTask= new RefleshMsgList(new RefleshMsgList.RefleshCallBack(){
                        @Override
                        public void getData(List<Msg> list) {
                            for(int i=0;i<list.size();i++){
                                newMsg(list.get(i).getUserName(),list.get(i).getContent(),list.get(i).getTime());
                            }
                            arrayAdapter.notifyDataSetChanged();
                        }
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
        super.onDestroy();
        //Log.d("ChatActivity","-----ChatDestroy-----");
    }

}