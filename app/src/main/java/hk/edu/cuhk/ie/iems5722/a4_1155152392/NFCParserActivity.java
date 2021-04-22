package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hk.edu.cuhk.ie.iems5722.a4_1155152392.parser.NdefMessageParser;

import static hk.edu.cuhk.ie.iems5722.a4_1155152392.parser.NdefMessageParser.*;

public class NFCParserActivity extends AppCompatActivity {

    public static final String TAG = NFCParserActivity.class.getSimpleName();

    private NfcAdapter mNFCAdapter = null;
    private PendingIntent mPendingIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_parser);
        mNFCAdapter = NfcAdapter.getDefaultAdapter(this);
        //mPendingIntent = PendingIntent.getActivity(this,0,getIntent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mNFCAdapter.enableForegroundDispatch(this,mPendingIntent,null,null);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNFCAdapter != null)
            mNFCAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNFCAdapter != null) {
            mNFCAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("NFCParser", "onNewIntent Activated.");
        /*if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
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
                Snackbar.make(NFCParserActivity.this.findViewById(R.id.tv_nfc_parser), "Invalid NFC Tag.", Snackbar.LENGTH_LONG).show();
            }
        }*/
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        patchTag(tag);
        if (tag != null) {
            readFromNFC(tag, intent);
        }
    }

    protected void returnresult(String messagestring) {
        if (messagestring.split(",").length == 2) {
            Intent i = new Intent();
            i.putExtra("messagestring", messagestring);
            setResult(1, i);
            finish();
        } else {
            Snackbar.make(NFCParserActivity.this.findViewById(R.id.tv_nfc_parser), "Invalid NFC Tag.", Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(2,intent);
        finish();
    }

    public Tag patchTag(Tag oTag) {
        if (oTag == null)
            return null;

        String[] sTechList = oTag.getTechList();

        Parcel oParcel, nParcel;

        oParcel = Parcel.obtain();
        oTag.writeToParcel(oParcel, 0);
        oParcel.setDataPosition(0);

        int len = oParcel.readInt();
        byte[] id = null;
        if (len >= 0) {
            id = new byte[len];
            oParcel.readByteArray(id);
        }
        int[] oTechList = new int[oParcel.readInt()];
        oParcel.readIntArray(oTechList);
        Bundle[] oTechExtras = oParcel.createTypedArray(Bundle.CREATOR);
        int serviceHandle = oParcel.readInt();
        int isMock = oParcel.readInt();
        IBinder tagService;
        if (isMock == 0) {
            tagService = oParcel.readStrongBinder();
        } else {
            tagService = null;
        }
        oParcel.recycle();

        int nfca_idx = -1;
        int mc_idx = -1;

        for (int idx = 0; idx < sTechList.length; idx++) {
            if (sTechList[idx] == NfcA.class.getName()) {
                nfca_idx = idx;
            } else if (sTechList[idx] == MifareClassic.class.getName()) {
                mc_idx = idx;
            }
        }

        if (nfca_idx >= 0 && mc_idx >= 0 && oTechExtras[mc_idx] == null) {
            oTechExtras[mc_idx] = oTechExtras[nfca_idx];
        } else {
            return oTag;
        }

        nParcel = Parcel.obtain();
        nParcel.writeInt(id.length);
        nParcel.writeByteArray(id);
        nParcel.writeInt(oTechList.length);
        nParcel.writeIntArray(oTechList);
        nParcel.writeTypedArray(oTechExtras, 0);
        nParcel.writeInt(serviceHandle);
        nParcel.writeInt(isMock);
        if (isMock == 0) {
            nParcel.writeStrongBinder(tagService);
        }
        nParcel.setDataPosition(0);
        Tag nTag = Tag.CREATOR.createFromParcel(nParcel);
        nParcel.recycle();

        return nTag;
    }

    private void readFromNFC(Tag tag, Intent intent) {

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();

                if (ndefMessage != null) {
                    /*String message = new String(ndefMessage.getRecords()[0].getPayload());
                    Log.d(TAG, "NFC found.. "+"readFromNFC: "+message );
                    tvNFCMessage.setText(message);*/

                    Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                    if (messages != null) {
                        NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                        for (int i = 0; i < messages.length; i++) {
                            ndefMessages[i] = (NdefMessage) messages[i];
                        }
                        NdefRecord record = ndefMessages[0].getRecords()[0];

                        byte[] payload = record.getPayload();
                        String text = new String(payload);
                        returnresult(text);

                        Log.e("tag", "vahid  -->  " + text);
                        ndef.close();

                    }

                } else {
                    Toast.makeText(this, "Not able to read from NFC, Please try again...", Toast.LENGTH_LONG).show();

                }
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        NdefMessage ndefMessage = ndef.getNdefMessage();

                        if (ndefMessage != null) {
                            String message = new String(ndefMessage.getRecords()[0].getPayload());
                            Log.d(TAG, "NFC found.. " + "readFromNFC: " + message);
                            returnresult(message);
                            ndef.close();
                        } else {
                            Toast.makeText(this, "Not able to read from NFC, Please try again...", Toast.LENGTH_LONG).show();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "NFC is not readable", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}