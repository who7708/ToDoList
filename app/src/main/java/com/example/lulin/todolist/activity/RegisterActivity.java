package com.example.lulin.todolist.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lulin.todolist.R;
import com.example.lulin.todolist.utils.User;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RegisterActivity extends BasicActivity{
    private EditText mEtUserName = null;
    private EditText mEtPassWord = null;
    private Button mBtnGoLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_register);
        mEtUserName = (EditText) findViewById(R.id.et_user_name);
        mEtPassWord = (EditText) findViewById(R.id.et_user_pwd);
        mBtnGoLogin = (Button) findViewById(R.id.btn_go_login);
        mBtnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void btnShow(View v) {
        final String username = mEtUserName.getText().toString();
        final String password = mEtPassWord.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEtUserName.length() < 4) {
            Toast.makeText(this, "用户名不能低于4位", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEtPassWord.length() < 6) {
            Toast.makeText(this, "密码不能低于6位", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * Bmob注册
         */
        final User user = new User();
        final String path = this.getApplicationContext().getFilesDir().getAbsolutePath() + "/default_head.png";
        Log.i("register", path);
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){

                    Log.i("register", "上传成功！" + bmobFile.getUrl());
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setNickName(username);
                    user.setAutograph("个性签名");
                    user.setImg(bmobFile);
                    user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User s, BmobException e) {
                            if(e==null){
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                Log.i("register", e.getMessage());
                            }
                        }
                    });

                }else {
                    Log.i("register", "失败！ " + e.getMessage() + path);
                }

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });


//        final BmobUser user = new BmobUser();
//        user.setUsername(username);
//        user.setPassword(password);
//        user.signUp(new SaveListener<BmobUser>() {
//            @Override
//            public void done(BmobUser bmobUser, BmobException e) {
//
//                if(e==null){
////                    BmobQuery<BmobUser> bmobQuery = new BmobQuery();
////                    bmobQuery.getObject(bmobUser.getObjectId(), new QueryListener<BmobUser>() {
////                        @Override
////                        public void done(BmobUser bmobUser, BmobException e) {
////
////                            User user = BmobUser.getCurrentUser(User.class);
////                            user.setNickName(username);
////                            user.setAutograph("个性签名");
//                            Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//                                    + getResources().getResourcePackageName(R.drawable.default_photo) + "/"
//                                    + getResources().getResourceTypeName(R.drawable.default_photo) + "/"
//                                    + getResources().getResourceEntryName(R.drawable.default_photo));
//                            String picPath = uri.getPath();
//                            BmobFile bmobFile = new BmobFile(new File(picPath));
////                            user.setImg(bmobFile);
////                            user.update(new UpdateListener() {
////                                @Override
////                                public void done(BmobException e) {
////                                    if (e==null){
////
////                                    } else {
////                                        Log.i("MainActivity", e.getMessage());
////                                    }
////                                }
////                            });
////
////                        }
////                    });
//
//                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else{
//
//                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
//                    Log.i("MainActivity", e.getMessage());
//                }
//            }
//        });
    }

    /**
     * 设置状态栏透明
     */
    private void setStatusBar(){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
}