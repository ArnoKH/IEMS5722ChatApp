package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton mBtnBack;
    private ListView mLVfl;
    private FriendListAdapter arrayAdapter;
    private List<User> frienduserlist = new ArrayList<>();

    private String username, userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        mToolbar = findViewById(R.id.toolbar_f);
        setSupportActionBar(mToolbar);
        mBtnBack = findViewById(R.id.toolbar_f_btn_back);
        mLVfl = findViewById(R.id.lv_friend_list);
        arrayAdapter = new FriendListAdapter(FriendListActivity.this, R.layout.layout_friendlist_item, frienduserlist);
        mLVfl.setAdapter(arrayAdapter);
        username = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("userid");





        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friendlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_refresh_f:
                RefreshFriendList mTask = new RefreshFriendList(new RefreshFriendList.RefreshCallBack() {
                    @Override
                    public void getData(List<User> list) {
                        frienduserlist.addAll(list);
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
                mTask.execute(userid);
                return true;

            case R.id.item_showQRcode:
                return true;

            case R.id.item_scan:
                return true;

            case R.id.item_location:
                return true;

            case R.id.item_nfc:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}