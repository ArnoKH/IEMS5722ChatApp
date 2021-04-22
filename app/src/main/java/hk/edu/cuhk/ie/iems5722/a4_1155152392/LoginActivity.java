package hk.edu.cuhk.ie.iems5722.a4_1155152392;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.regex.*;

public class LoginActivity extends AppCompatActivity {

    //各类控件
    private Button mBtnSign, mBtnLogin;
    private EditText mEtUsername, mEtUserid, mEtPsw;
    private String regex = "^[A-Za-z0-9]+$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //绑定控件
        mBtnSign = findViewById(R.id.btn_signup);
        mBtnLogin = findViewById(R.id.btn_login);
        mEtUsername = findViewById(R.id.et_username);
        mEtUserid = findViewById(R.id.et_userid);
        mEtPsw = findViewById(R.id.et_psw);

        //注册按钮点击监听
        mBtnSign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //确认没有漏填
                if(mEtUsername.getText().toString().equals("") || mEtUserid.getText().toString().equals("") || mEtPsw.getText().toString().equals("")){
                    Snackbar.make(v, "Please fill in your Username/UserID/Password.", Snackbar.LENGTH_LONG).show();
                } else {
                    if (!mEtUsername.getText().toString().matches(regex)) {
                        Snackbar.make(v, "Username can only consist of letters and numbers.", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        if(mEtUsername.getText().toString().length()>20 || mEtUserid.getText().toString().length() >10){
                            Snackbar.make(v, "Username or User ID is too long.", Snackbar.LENGTH_LONG).show();
                        } else {
                            Signup mTask = new Signup(new Signup.SignupCallBack() {
                                @Override
                                public void getData(String resultstatus) {
                                    //状态不成功则显示，成功则直接登录
                                    if (!resultstatus.equals("OK")) {
                                        Snackbar.make(v, resultstatus, Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("username", mEtUsername.getText().toString());
                                        intent.putExtra("userid", mEtUserid.getText().toString());
                                        startActivity(intent);
                                    }
                                }
                            });
                            mTask.execute(mEtUsername.getText().toString(), mEtUserid.getText().toString(), mEtPsw.getText().toString());
                        }
                    }
                }
                //送出后非选中输入框
                final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEtUsername.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mEtUserid.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mEtPsw.getWindowToken(), 0);
            }
        });

        //登录按钮点击监听
        mBtnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //确认名字和ID至少一项不为空
                if(mEtUsername.getText().toString().equals("") && mEtUserid.getText().toString().equals("")){
                    Snackbar.make(v, "Please fill in your Username or UserID.", Snackbar.LENGTH_LONG).show();
                } else {
                    if (!mEtUsername.getText().toString().equals("") && !mEtUsername.getText().toString().matches(regex)) {
                        Snackbar.make(v, "Username can only consist of letters and numbers.", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        if(mEtUsername.getText().toString().length()>20 || mEtUserid.getText().toString().length() >10){
                            Snackbar.make(v, "Username or User ID is too long.", Snackbar.LENGTH_LONG).show();
                        } else {
                            if(mEtPsw.getText().toString().equals("")){
                                Snackbar.make(v, "Please fill in your Password.", Snackbar.LENGTH_LONG).show();
                            } else {
                                Login mTask = new Login(new Login.LoginCallBack() {
                                    @Override
                                    public void getData(JSONObject result) {
                                        try {
                                            String status = result.getString("status");
                                            if (status.equals("OK")) {
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("username", result.getString("username"));
                                                intent.putExtra("userid", result.getString("userid"));
                                                startActivity(intent);
                                            } else {
                                                Snackbar.make(v, status, Snackbar.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                if (mEtUsername.getText().toString().equals("")) {
                                    mTask.execute("0", mEtUserid.getText().toString(), mEtPsw.getText().toString());
                                } else {
                                    mTask.execute("1", mEtUsername.getText().toString(), mEtPsw.getText().toString());
                                }
                            }
                        }
                    }
                }
                //送出后非选中输入框
                final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEtUsername.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mEtUserid.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mEtPsw.getWindowToken(), 0);
            }
        });
    }
}