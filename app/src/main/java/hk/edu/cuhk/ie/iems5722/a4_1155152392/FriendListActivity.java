package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton mBtnBack;
    private ListView mLVfl;
    private FriendListAdapter arrayAdapter;
    private List<User> frienduserlist = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private String userLocation;
    private boolean via_location_swich = true;
    private boolean camera_swich = true;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //检查相机权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            camera_swich = true;
        } else {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},1);
        }
        //检查定位权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            via_location_swich = true;
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }
        RefreshFriendList mTask = new RefreshFriendList(new RefreshFriendList.RefreshCallBack() {
            @Override
            public void getData(List<User> list) {
                frienduserlist.clear();
                frienduserlist.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }
        });
        mTask.execute(userid);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            //相机
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    camera_swich = true;
                } else {
                    camera_swich = false;
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Scan QR Code feature disabled.", Snackbar.LENGTH_LONG).show();
                }
            //定位
            case 2:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    via_location_swich = true;
                    getLocation();
                } else {
                    via_location_swich = false;
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Add friend via location feature disabled.", Snackbar.LENGTH_LONG).show();
                }
            //???
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    protected void getLocation(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    getCityName mTask = new getCityName();
                    mTask.execute(location);
                } else {
                    via_location_swich = false;
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Error happened when getting location. Add friend via location feature disabled.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public class getCityName extends AsyncTask<Location, Void, String> {
        @Override
        protected String doInBackground(Location... locations) {
            Geocoder geocoder = new Geocoder(FriendListActivity.this);
            try{
                List<Address> addresses = geocoder.getFromLocation(locations[0].getLatitude(), locations[0].getLongitude(), 1);
                if ((addresses !=  null) && ( addresses.size( ) > 0)){
                    if(addresses.get(0).getAdminArea() == null){
                        via_location_swich = false;
                        Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Cannot resolve city name. Add friend via location feature disabled.", Snackbar.LENGTH_LONG).show();
                    } else {
                        via_location_swich = true;
                        userLocation = addresses.get(0).getAdminArea().trim();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
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
                        frienduserlist.clear();
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
                if(camera_swich){
                    Intent intent = new Intent(FriendListActivity.this, CodeScannerActivity.class);
                    startActivityForResult(intent,1);
                } else {
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "This feature is unavailable for now.", Snackbar.LENGTH_LONG).show();
                }
                return true;

            case R.id.item_location:
                if(via_location_swich){
                    Intent intent = new Intent(FriendListActivity.this, AddFriendByLocationActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("userid", userid);
                    intent.putExtra("userLocation", userLocation);
                    startActivity(intent);
                } else {
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "This feature is unavailable for now.", Snackbar.LENGTH_LONG).show();
                }
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
                    if(userid.equals(friendinfo[1])){
                        Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Cannot add yourself as friend.", Snackbar.LENGTH_LONG).show();
                    } else {
                        AddFriend(friendinfo);
                    }
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
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Succeed.", Snackbar.LENGTH_LONG).show();
                    frienduserlist.add(new User(finfo[0],finfo[1]));
                    arrayAdapter.notifyDataSetChanged();
                }
                else {
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), status, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mTask.execute(username,userid,finfo[0],finfo[1]);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!via_location_swich && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            via_location_swich = true;
            getLocation();
        }
        RefreshFriendList mTask = new RefreshFriendList(new RefreshFriendList.RefreshCallBack() {
            @Override
            public void getData(List<User> list) {
                frienduserlist.clear();
                frienduserlist.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }
        });
        mTask.execute(userid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!via_location_swich && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            via_location_swich = true;
            getLocation();
        }
        RefreshFriendList mTask = new RefreshFriendList(new RefreshFriendList.RefreshCallBack() {
            @Override
            public void getData(List<User> list) {
                frienduserlist.clear();
                frienduserlist.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }
        });
        mTask.execute(userid);
    }
}