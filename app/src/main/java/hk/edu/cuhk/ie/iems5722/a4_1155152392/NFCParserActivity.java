package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import hk.edu.cuhk.ie.iems5722.a4_1155152392.parser.NdefMessageParser;

import static hk.edu.cuhk.ie.iems5722.a4_1155152392.parser.NdefMessageParser.*;

public class NFCParserActivity extends AppCompatActivity {

    private NfcAdapter mNFCAdapter = null;
    private PendingIntent mPendingIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_parser);
        mNFCAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this,0,getIntent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNFCAdapter.enableForegroundDispatch(this,mPendingIntent,null,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNFCAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("NFCParser", "onNewIntent Activated.");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Log.i("NFCParser", "onNewIntent_DISCOVERED Activated.");
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            List<NdefMessage> messages = new ArrayList<>();
            for (int i = 0; i < rawMessages.length; i++) {
                messages.add((NdefMessage) rawMessages[i]);
            }
            String messagestring = parserNDEFMessage(messages);
            if (messagestring.split(",").length == 2) {
                Intent i = new Intent();
                i.putExtra("messagestring", messagestring);
                setResult(1, intent);
                finish();
            } else {
                Snackbar.make(NFCParserActivity.this.getWindow().getDecorView(), "Invalid NFC Tag.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(2,intent);
        finish();
    }
}