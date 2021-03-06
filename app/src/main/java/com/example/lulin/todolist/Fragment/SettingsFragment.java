package com.example.lulin.todolist.Fragment;

import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lulin.todolist.R;
import com.example.lulin.todolist.Activity.MainActivity;
import com.example.lulin.todolist.Utils.SPUtils;
import com.example.lulin.todolist.Utils.ToastUtils;
import com.example.lulin.todolist.Bean.User;

import java.util.List;
import java.util.Locale;

// import cn.bmob.v3.BmobUser;
// import cn.bmob.v3.exception.BmobException;
// import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;
import me.drakeet.materialdialog.MaterialDialog;

public class SettingsFragment extends PreferenceFragment {

    private RingtonePreference mRingtone;
    private PreferenceScreen preferenceScreen;
    private SwitchPreference mFocus;
    private static final String TAG = "setting";
    private UsageStatsManager usageStatsManager;
    private List<UsageStats> queryUsageStats;
    private Preference mChangePassWord;
    private Preference mExitLogin;
    private static final String KEY_RINGTONE = "ring_tone";
    private static final String KEY_FOCUS = "focus";
    private static final String KEY_PWD = "change_pwd";
    private static final String KEY_EXIT = "exit_login";
    private EditText oldPwd,newPwd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);
        intView();
        setChangeListener();

    }

    public void intView(){
        preferenceScreen = getPreferenceScreen();
        mRingtone = (RingtonePreference) preferenceScreen.findPreference(KEY_RINGTONE);
        mFocus = (SwitchPreference) preferenceScreen.findPreference(KEY_FOCUS);
        mChangePassWord = (Preference) preferenceScreen.findPreference(KEY_PWD);
        mExitLogin = (Preference) preferenceScreen.findPreference(KEY_EXIT);
        Uri uri = Uri.parse(SPUtils.get(getActivity(), KEY_RINGTONE, "").toString());
        Log.i(TAG, "??????" + getRingtonName(uri));
        if (getRingtonName(uri).equals("????????????")){
            mRingtone.setSummary("????????????");
        } else {
            mRingtone.setSummary(getRingtonName(uri));
        }
        mFocus.setChecked(getIsFocus(getActivity()));

    }

    public void setChangeListener(){
        mRingtone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                if (preference == mRingtone){
                    SPUtils.put(getActivity(), KEY_RINGTONE, value.toString());
                    Log.i(TAG, value.toString());
                    mRingtone.setSummary(getRingtonName(Uri.parse(value.toString())));
                }
                return false;
            }
        });

        mFocus.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object object) {
                if (preference == mFocus){
                    if (mFocus.isChecked() != (Boolean)object) {
                        if (Build.VERSION.SDK_INT > 20){
                            if (!isNoSwitch()){
                                RequestPromission();
                            }
                        }
                        boolean value = (Boolean)(object);
                        mFocus.setChecked(value);
                        SPUtils.put(getActivity(), KEY_FOCUS, value);
                    }
                }
                return true;
            }
        });

        mChangePassWord.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                User user = null;
                // User.getCurrentUser(User.class)
                if (user==null){
                    ToastUtils.showShort(getActivity(),"?????????");
                    Toasty.error(getActivity(), "????????????????????????", Toast.LENGTH_SHORT, true).show();
                } else {
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View textEntryView = layoutInflater.inflate(R.layout.dialog_reset_pwd, null);
                    oldPwd = (EditText) textEntryView.findViewById(R.id.old_pwd);
                    newPwd = (EditText)textEntryView.findViewById(R.id.new_pwd);
                    final MaterialDialog resetDialog = new MaterialDialog(getActivity());
                    resetDialog.setTitle("????????????");
                    resetDialog.setView(textEntryView);
                    resetDialog.setPositiveButton("??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String old_pwd = oldPwd.getText().toString();
                            String new_pwd = newPwd.getText().toString();
                            // BmobUser.updateCurrentUserPassword(old_pwd, new_pwd, new UpdateListener() {
                            //     @Override
                            //     public void done(BmobException e) {
                            //         if(e==null){
                            //             Toasty.success(getActivity(), "????????????", Toast.LENGTH_SHORT, true).show();
                            //             BmobUser.logOut();   //????????????????????????
                            //             Log.i(TAG, "??????");
                            //             Intent intent = new Intent(getActivity(), MainActivity.class);
                            //             getActivity().setResult(3,intent);
                            //             getActivity().finish();
                            //         }else{
                            //             Log.i(TAG, "done: ??????"+e.getMessage());
                            //             Toasty.error(getActivity(), "????????????", Toast.LENGTH_SHORT, true).show();
                            //         }
                            //     }
                            // });
                        }
                    });
                    resetDialog.setNegativeButton("??????", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            resetDialog.dismiss();
                        }
                    });
                    resetDialog.show();// ???????????????
                }

                return false;
            }
        });

        mExitLogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final MaterialDialog signOutDialog = new MaterialDialog(getActivity());
                signOutDialog.setTitle("?????????????????????")
                        .setPositiveButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // BmobUser.logOut();   //????????????????????????
                                SPUtils.put(getActivity(),"sync",false);
                                Log.i(TAG, "????????????");
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                getActivity().setResult(3,intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("??????", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                signOutDialog.dismiss();
                            }
                        })
                        .show();
                return false;
            }
        });

    }

    /**
     * ???????????????
     * @param uri
     * @return
     */
    public String getRingtonName(Uri uri) {
        Ringtone r = RingtoneManager.getRingtone(getActivity(), uri);
        return r.getTitle(getActivity());
    }

    public boolean getIsFocus(Context context){

        Boolean isFocus = (Boolean) SPUtils.get(context, KEY_FOCUS, false);

        return isFocus;

    }

    /**
     * ????????????????????????????????????????????????
     * @return
     */
    private boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        if(Build.VERSION.SDK_INT >=21){
            //noinspection ResourceType
            usageStatsManager = (UsageStatsManager)getActivity().getApplicationContext().getSystemService("usagestats");
            queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
        }
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * ?????????????????????????????????????????????
     */
    public void RequestPromission() {
        new AlertDialog.Builder(getActivity()).
                setTitle("??????").
                //setMessage("??????usagestats??????")
                        setMessage(String.format(Locale.US,"????????????????????????App??????????????????????????????"))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                        //finish();
                    }
                }).show();
    }

}

