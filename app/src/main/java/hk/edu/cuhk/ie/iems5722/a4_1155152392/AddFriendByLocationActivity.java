package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddFriendByLocationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private Toolbar mToolbar;
    private ImageButton mBtnBack;
    private ListView mLVnul;
    private FriendListAdapter arrayAdapter;
    private List<User> nearbyuserlist = new ArrayList<>();
    private String userLocation, username, userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        mToolbar = findViewById(R.id.toolbar_f);
        setSupportActionBar(mToolbar);
        mBtnBack = findViewById(R.id.toolbar_f_btn_back);
        mLVnul = findViewById(R.id.lv_friend_list);
        arrayAdapter = new FriendListAdapter(AddFriendByLocationActivity.this, R.layout.layout_friendlist_item, nearbyuserlist);
        mLVnul.setAdapter(arrayAdapter);
        username = getIntent().getStringExtra("username");
        userid = getIntent().getStringExtra("userid");
        userLocation = getIntent().getStringExtra("userLocation");
        UploadLocation mTask1 = new UploadLocation();
        mTask1.execute(username,userid,userLocation);
        NearbyUsersRequest mTask2 = new NearbyUsersRequest(new NearbyUsersRequest.NearbyUsersCallBack() {
            @Override
            public void getData(List<User> list) {
                nearbyuserlist.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }
        });
        mTask2.execute(username,userid,userLocation);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLVnul.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        TextView tvUsername = v.findViewById(R.id.fl_username);
        TextView tvUserid = v.findViewById(R.id.fl_userid);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to add "+tvUsername.getText()+" as friend?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    AddFriend(tvUsername.getText().toString(),tvUserid.getText().toString());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void AddFriend(String targetname, String targetid) {
        AddFriendRequest mTask = new AddFriendRequest(new AddFriendRequest.AddFriendCallBack(){
            @Override
            public void getData(String status) {
                if(status.equals("OK")) {
                    Snackbar.make(AddFriendByLocationActivity.this.getWindow().getDecorView(), "Succeed.", Snackbar.LENGTH_LONG).show();
                }
                else {
                    Snackbar.make(AddFriendByLocationActivity.this.getWindow().getDecorView(), status, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mTask.execute(username,userid,targetname,targetid);
    }

    protected class UploadLocation  extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String hostusername = params[0];
            String hostuserid = params[1];
            String hostlocation = params[2];
            String json_string = null;
            JSONObject json = null;
            try {
                URL url = new URL("http://34.96.208.254/api/a3/update_location?hname="+hostusername+"&hid="+hostuserid+"&location="+hostlocation);
                json_string=Download.downloadUrl(url);
                json = new JSONObject(json_string);
                String status = json.getString("status" ) ;
                return status;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
            if(status.equals("OK")){
                Snackbar.make(AddFriendByLocationActivity.this.getWindow().getDecorView(), "Location upload succeed.", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(AddFriendByLocationActivity.this.getWindow().getDecorView(), "Location upload failed.", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}