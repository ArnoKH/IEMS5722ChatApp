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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
    private boolean via_location_swich = false;
    private boolean camera_swich = false;
    private boolean nfc_swich = true;
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
        askpermissions();
        TaskRefreshFriendList();
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void askpermissions() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        List<String> mPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(FriendListActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {
            camera_swich = true;
            via_location_swich = true;
            getLocation();
        } else {
            permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(FriendListActivity.this, permissions, 4);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 4) {
            //检查相机权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                camera_swich = true;
            } else {
                camera_swich = false;
                Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Scan QR Code feature disabled.", Snackbar.LENGTH_LONG).show();
            }
            //检查定位权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                via_location_swich = true;
                getLocation();
            } else {
                via_location_swich = false;
                Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Add friend via location feature disabled.", Snackbar.LENGTH_LONG).show();
            }
        }
        //???
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    protected void getLocation() {
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
            try {
                List<Address> addresses = geocoder.getFromLocation(locations[0].getLatitude(), locations[0].getLongitude(), 1);
                if ((addresses != null) && (addresses.size() > 0)) {
                    if (addresses.get(0).getAdminArea() == null) {
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
                TaskRefreshFriendList();
                return true;

            case R.id.item_searchbyid:
                LayoutInflater inflater_id = LayoutInflater.from(FriendListActivity.this);
                View dialog_id_view = inflater_id.inflate(R.layout.dialog_search_uid,null);
                AlertDialog.Builder builder_id = new AlertDialog.Builder(this);
                builder_id.setView(dialog_id_view);
                builder_id.setTitle("Search User by ID");
                builder_id.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder_id.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uid = ((EditText)dialog_id_view.findViewById(R.id.et_searchuid)).getText().toString();
                        if(!uid.equals("")){
                            searchandadd(uid);
                        }
                    }
                });
                AlertDialog dialog_id = builder_id.create();
                dialog_id.show();
                return true;

            case R.id.item_showQRcode:
                Bitmap pic = QRCodeGenerator.createQRCode(username + ',' + userid);
                AlertDialog.Builder builder_qr = new AlertDialog.Builder(this);
                builder_qr.setView(R.layout.dialog_show_qr_code);
                AlertDialog dialog_qr = builder_qr.create();
                dialog_qr.show();
                ImageView mIVqrc = dialog_qr.findViewById(R.id.IV_qrc);
                mIVqrc.setImageBitmap(pic);
                return true;

            case R.id.item_scan:
                if (camera_swich) {
                    Intent intent = new Intent(FriendListActivity.this, CodeScannerActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "This feature is unavailable for now.", Snackbar.LENGTH_LONG).show();
                }
                return true;

            case R.id.item_location:
                if (via_location_swich) {
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
                if (nfc_swich) {
                    Intent intent = new Intent(FriendListActivity.this, NFCActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("userid", userid);
                    startActivityForResult(intent, 2);
                } else {
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "This feature is unavailable for now.", Snackbar.LENGTH_LONG).show();
                }
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
            if(friendinfo.length == 2){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure want to add " + friendinfo[0] + " as friend?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (userid.equals(friendinfo[1])) {
                            Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Cannot add yourself as friend.", Snackbar.LENGTH_LONG).show();
                        } else {
                            AddFriend(friendinfo[0],friendinfo[1]);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Invalid QR Code.", Snackbar.LENGTH_LONG).show();
            }
        }
        //NFC的回传
        if (requestCode == 2 && resultCode == 2) {
            nfc_swich = false;
            Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "NFC Functions unavailable.", Snackbar.LENGTH_LONG).show();
        }
        //下面可加其他回传
    }

    protected void AddFriend(String fname, String fid) {
        AddFriendRequest mTask = new AddFriendRequest(new AddFriendRequest.AddFriendCallBack() {
            @Override
            public void getData(String status) {
                if (status.equals("OK")) {
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Succeed.", Snackbar.LENGTH_LONG).show();
                    frienduserlist.add(new User(fname, fid));
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), status, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mTask.execute(username, userid, fname, fid);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //用户切出去开权限回来时的操作
        if (!via_location_swich && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            via_location_swich = true;
            getLocation();
        }
        if (!camera_swich && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            camera_swich = true;
        }
        if (!nfc_swich && ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_GRANTED) {
            nfc_swich = true;
        }
        TaskRefreshFriendList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //用户切出去开权限回来时的操作
        if (!via_location_swich && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            via_location_swich = true;
            getLocation();
        }
        if (!camera_swich && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            camera_swich = true;
        }
        if (!nfc_swich && ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_GRANTED) {
            nfc_swich = true;
        }
        TaskRefreshFriendList();
    }

    protected void TaskRefreshFriendList() {
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

    protected void searchandadd(String uid){
        if(uid.equals(userid)){
            Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Cannot add yourself as friend.", Snackbar.LENGTH_LONG).show();
        } else {
            SearchUser mTask = new SearchUser(new SearchUser.SearchUserCallBack() {
                @Override
                public void getData(User result) {
                    if(result != null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendListActivity.this);
                        builder.setMessage("Are you sure want to add " + result.getUserName() + " as friend?");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AddFriend(result.getUserName(),result.getUserID());
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Snackbar.make(FriendListActivity.this.getWindow().getDecorView(), "Invalid User ID", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            mTask.execute(uid);
        }
    }
}