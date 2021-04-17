package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.snackbar.Snackbar;

public class NFCActivity extends AppCompatActivity {

    private Button mBtnStartHCE, mBtnStopHCE, mBtnScanNFC;
    private ImageButton mBtnBack;
    private Toolbar mToolbar;
    private String username, userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mBtnStartHCE = findViewById(R.id.btn_start_hce);
        mBtnStopHCE = findViewById(R.id.btn_stop_hce);
        mBtnScanNFC = findViewById(R.id.btn_scan_nfc);
        mBtnBack = findViewById(R.id.toolbar_f_nfc_btn_back);
        mToolbar = findViewById(R.id.toolbar_f_nfc);
        setSupportActionBar(mToolbar);

        mBtnStartHCE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NFCActivity.this, CardService.class);
                intent.putExtra("ndefMessage", username+","+userid);
                startService(intent);
                Snackbar.make(NFCActivity.this.getWindow().getDecorView(), "HCE Started.", Snackbar.LENGTH_LONG).show();
            }
        });
        mBtnStopHCE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(NFCActivity.this, CardService.class));
                Snackbar.make(NFCActivity.this.getWindow().getDecorView(), "HCE Stopped.", Snackbar.LENGTH_LONG).show();
            }
        });
        mBtnScanNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NFCActivity.this,NFCParserActivity.class);
                startActivityForResult(intent,3);
            }
        });
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(1, intent);
                finish();
            }
        });

        //检查NFC权限
        PackageManager pm = getPackageManager();
        boolean nfc_exist = pm.hasSystemFeature(PackageManager.FEATURE_NFC);
        boolean hce_exist = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
        if (nfc_exist && hce_exist) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.NFC}, 3);
            }
        } else {
            Intent intent = new Intent();
            setResult(2, intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 3) {
            //NFC
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                setResult(2, intent);
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描NFC Tag的回传
        if (requestCode == 3 && resultCode == 1) {
            String[] friendinfo = data.getStringExtra("messagestring").split(",");
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
                        Snackbar.make(NFCActivity.this.getWindow().getDecorView(), "Cannot add yourself as friend.", Snackbar.LENGTH_LONG).show();
                    } else {
                        AddFriend(friendinfo[0],friendinfo[1]);
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    protected void AddFriend(String targetname, String targetid) {
        AddFriendRequest mTask = new AddFriendRequest(new AddFriendRequest.AddFriendCallBack(){
            @Override
            public void getData(String status) {
                if(status.equals("OK")) {
                    Snackbar.make(NFCActivity.this.getWindow().getDecorView(), "Succeed.", Snackbar.LENGTH_LONG).show();
                }
                else {
                    Snackbar.make(NFCActivity.this.getWindow().getDecorView(), status, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mTask.execute(username,userid,targetname,targetid);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(1, intent);
        finish();
    }
}