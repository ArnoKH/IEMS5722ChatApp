package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

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
                frienduserlist.clear();
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
                Bitmap pic = QRCodeGenerator.createQRCode(username+','+userid);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(R.layout.activity_show_qr_code);
                AlertDialog dialog = builder.create();
                dialog.show();
                ImageView mIVqrc = dialog.findViewById(R.id.IV_qrc);
                mIVqrc.setImageBitmap(pic);
                return true;

            case R.id.item_scan:
                Intent intent = new Intent(FriendListActivity.this, CodeScannerActivity.class);
                startActivityForResult(intent,1);
                return true;

            case R.id.item_location:
                return true;

            case R.id.item_nfc:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫二维码的回传
        if (requestCode == 1 && resultCode == 1) {
            String[] friendinfo = data.getStringExtra("result").split(",");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure want to add "+friendinfo[0]+" as friend?");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AddFriend(friendinfo);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        //下面可加其他回传
    }

    protected void AddFriend(String[] finfo) {
        AddFriendRequest mTask = new AddFriendRequest(new AddFriendRequest.AddFriendCallBack(){
            @Override
            public void getData(String status) {
                if(status.equals("OK")) {
                    Toast.makeText(FriendListActivity.this, "Succeed.", Toast.LENGTH_LONG).show();
                    frienduserlist.add(new User(finfo[0],finfo[1]));
                    arrayAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(FriendListActivity.this, status, Toast.LENGTH_LONG).show();
                }
            }
        });
        mTask.execute(username,userid,finfo[0],finfo[1]);
    }
}