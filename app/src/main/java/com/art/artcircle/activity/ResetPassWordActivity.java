package com.art.artcircle.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.art.artcircle.R;
import com.art.artcircle.application.DemoApplication;
import com.art.artcircle.bean.ArtCircleLoginResponse;
import com.art.artcircle.bean.ExportSIMContactResponse;
import com.art.artcircle.bean.FetchContactResponse;
import com.art.artcircle.bean.NormalMessageResponse;
import com.art.artcircle.constant.Constant;
import com.art.artcircle.constant.EventEnum;
import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.constant.NetConstant;
import com.art.artcircle.db.UserDao;
import com.art.artcircle.dialog.CommonDialog;
import com.art.artcircle.dialog.ProgressDialog;
import com.art.artcircle.listener.XGPushCallback;
import com.art.artcircle.manager.ArtCircleBaseInfoManager;
import com.art.artcircle.manager.ArtCircleUserInfoManager;
import com.art.artcircle.task.StringToBeanTask;
import com.art.artcircle.utils.DialogUtils;
import com.art.artcircle.utils.FileUtils;
import com.art.artcircle.utils.NetUtils;
import com.art.artcircle.utils.StringUtils;
import com.art.artcircle.utils.UmengUtils;
import com.art.hxmodule.controller.DemoHXSDKHelper;
import com.art.hxmodule.controller.HXSDKHelper;
import com.art.hxmodule.model.domain.User;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.android.tpush.XGPushManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ResetPassWordActivity extends BaseActivity {
    private EditText mOldPaswdEt;
    private EditText mNewPaswdEt1;
    private EditText mNewPaswdEt2;
    private TextView mCommitBtn;

    private ProgressDialog mProgressDialog;

    private boolean isSubmit;

    private String mCode;
    private String mMobile;
    private String mPassword;

    private String mCurrentUsername;
    private String mEMChatUserId;

    private TelephonyManager mTelephonyManager;
    private ContentResolver mResolver;

    private List<FetchContactResponse.DataEntity> contactList;

    private CommonDialog mCommonDialog;
    private View.OnClickListener mSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            jump(mContext, LoginActivity.class);
            finish();
        }
    };
    private View.OnClickListener mCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCommonDialog.dismiss();
        }
    };

    @Override
    protected void getIntentData() {
        mCode = getIntent().getStringExtra(IntentConstant.RESET_PASSWORD_CODE);
        mMobile = getIntent().getStringExtra(IntentConstant.RESET_PASSWORD_MOBILE);
    }

    @Override
    protected int setLayoutViewId() {
        return R.layout.activity_edit_password;
    }

    @Override
    protected void initTitle() {
        TextView backView = (TextView) findViewById(R.id.tv_back);
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        titleView.setText(getString(R.string.reset_password));
        setOnClickListener(backView);
    }

    @Override
    protected void initData() {
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mResolver = mContext.getContentResolver();
        contactList = new ArrayList<>();
    }

    @Override
    protected void initContent() {
        mOldPaswdEt = (EditText) findViewById(R.id.et_old_password);
        mOldPaswdEt.setVisibility(View.GONE);
        mNewPaswdEt1 = (EditText) findViewById(R.id.et_new_password1);
        mNewPaswdEt2 = (EditText) findViewById(R.id.et_new_password2);
        mCommitBtn = (TextView) findViewById(R.id.btn_commit);
        mCommitBtn.setText(R.string.login);
        setOnClickListener(mCommitBtn);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                backPreviousPage();
                break;
            case R.id.btn_commit:
                if (!NetUtils.isNetworkConnected(this)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.network_is_not_available);
                    return;
                }
                String newPswd1 = mNewPaswdEt1.getText().toString().trim();
                String newPswd2 = mNewPaswdEt2.getText().toString().trim();
                if (newPswd1.length() < 6 || newPswd1.length() > 20) {
                    DialogUtils.showShortPromptToast(mContext, R.string.new_password_between);
                    return;
                }
                if (TextUtils.isEmpty(newPswd2) || !newPswd2.equals(newPswd1)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.two_input_password_different);
                    return;
                }
                mProgressDialog = DialogUtils.showProgressDialog(mContext, getString(R.string.Is_landing));
                mPassword = newPswd1;
                commitEditPasswordRequest();
                UmengUtils.onEvent(mContext, EventEnum.Reset_Password_Login);
                break;
        }
    }

    private void commitEditPasswordRequest() {
        new HttpUtils().configCurrentHttpCacheExpiry(0).send(HttpRequest.HttpMethod.GET, NetConstant.HOST, NetConstant.getResetPasswordParams(mContext, mMobile, mCode, mPassword),
                new EditPasswordListener());
    }


    private class EditPasswordListener extends RequestCallBack<String> implements StringToBeanTask.ConvertListener<NormalMessageResponse> {

        @Override
        public void onConvertSuccess(NormalMessageResponse data) {
            if (data == null || data.getError()) {
                DialogUtils.showShortPromptToast(mContext, R.string.reset_password_fail);
                return;
            }
            isSubmit = true;
            DialogUtils.showShortPromptToast(mContext, R.string.reset_password_success);
            // 重新登陆
            //step1 登陆自己的服务器
            //step2 登录成功后调用sdk登陆方法登陆聊天服务器
            new HttpUtils().configCurrentHttpCacheExpiry(0).send(HttpRequest.HttpMethod.GET, NetConstant.HOST, NetConstant.getLoginParams(mMobile, mPassword),
                    new LoginArtCircleServerListener());
        }

        @Override
        public void onConvertFailed(String json) {
            DialogUtils.showShortPromptToast(mContext, R.string.reset_password_fail);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            StringToBeanTask<NormalMessageResponse> task = new StringToBeanTask<>(NormalMessageResponse.class, this);
            task.execute(responseInfo.result);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            DialogUtils.showShortPromptToast(mContext, R.string.reset_password_fail);
        }
    }

    /**
     * 选择提示对话框
     */
    private void showPickDialog() {
        mCommonDialog = DialogUtils.showCommonDialog(mContext, getString(R.string.prompt), getString(R.string.cancel_reset_auth_code),
                getString(R.string.continue_reset), getString(R.string.confirm_cancel), mSubmitListener, mCancelListener, View.GONE, View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backPreviousPage();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回到上一页面
     */
    private void backPreviousPage() {
        if (isSubmit) {
            finish();
        } else {
            showPickDialog();
        }
    }

    private class LoginArtCircleServerListener extends RequestCallBack<String> implements StringToBeanTask.ConvertListener<ArtCircleLoginResponse> {

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            StringToBeanTask<ArtCircleLoginResponse> task = new StringToBeanTask<>(ArtCircleLoginResponse.class, this);
            task.execute(responseInfo.result);
        }

        @Override
        public void onFailure(final HttpException e, String s) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(mContext, StringUtils.getString(getString(R.string.Login_failed), e.getMessage()), Toast.LENGTH_SHORT).show();
//            DialogUtils.showShortPromptToast(mContext, StringUtils.getString(getString(R.string.Login_failed), e.getMessage()));
        }

        @Override
        public void onConvertSuccess(final ArtCircleLoginResponse response) {
            if (!response.isError()) {
                mCurrentUsername = response.getData().getUser_name();
                mEMChatUserId = response.getData().getUser_id();//环信用户名
                if (TextUtils.isEmpty(mEMChatUserId)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.user_not_exist);
                    return;
                }
                EMChatManager.getInstance().login(mEMChatUserId, mPassword, new ArtCircleEMCallBack(response));
            } else {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Toast.makeText(mContext, StringUtils.getString(getString(R.string.Login_failed), response.getMessage()), Toast.LENGTH_SHORT).show();
//                DialogUtils.showShortPromptToast(mContext, StringUtils.getString(getString(R.string.Login_failed), response.getMessage()));
            }
        }

        @Override
        public void onConvertFailed(String json) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    private class ArtCircleEMCallBack implements EMCallBack {
        private ArtCircleLoginResponse response;

        public ArtCircleEMCallBack(ArtCircleLoginResponse resp) {
            response = resp;
        }

        @Override
        public void onSuccess() {
            // 登陆成功，保存用户名密码
            DemoApplication.getInstance().setUserName(mEMChatUserId);
            DemoApplication.getInstance().setPassword(mPassword);
            //保存昵称、Token、用户id、头像、手机号
            ArtCircleUserInfoManager artCircleUserInfoManager = ArtCircleUserInfoManager.getsInstance();
            artCircleUserInfoManager.saveUserId(mContext, response.getData().getUser_id());
            artCircleUserInfoManager.saveArtCircleUserId(mContext, response.getData().getUser_id());
            artCircleUserInfoManager.saveRealName(mContext, response.getData().getReal_name());
            artCircleUserInfoManager.saveHeadImageUrl(mContext, response.getData().getHeadimg());
            artCircleUserInfoManager.savePhone(mContext, response.getData().getMobile());
            artCircleUserInfoManager.saveToken(mContext, response.getData().getToken());
            artCircleUserInfoManager.saveStep(mContext, response.getData().getStep());
            artCircleUserInfoManager.saveUserPassword(mContext, mPassword);
            artCircleUserInfoManager.savePhone(mContext, mCurrentUsername);
            artCircleUserInfoManager.savePerfect(mContext, response.getData().getPerfect());

            saveBaseInfo(response);
            //登陆成功注册信鸽的单点推送
            XGPushManager.registerPush(mContext, DemoApplication.getPushName(mContext), new XGPushCallback(DemoApplication.getPushName(mContext)));
            try {
                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                // 处理好友和群组
                initializeContacts();
            } catch (final Exception e) {
                // 取好友或者群聊失败，不让进入主页面
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        ArtCircleUserInfoManager.getsInstance().logout(mContext);
                        DemoHXSDKHelper.getInstance().logout(true, null);
                        Toast.makeText(mContext, getString(R.string.login_failure_failed), Toast.LENGTH_SHORT).show();
//                        DialogUtils.showShortPromptToast(mContext, R.string.login_failure_failed);

                    }
                });
                return;
            }
            // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
//            boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
//                    DemoApplication.currentUserNick.trim());
//            if (!updatenick) {
//                LogForTest.logE(TAG, "update current user nick fail");
//            }

            //判断是否超过一天时间
            String preTimeStr =
                    FileUtils.readStringFromFileCache(mContext, StringUtils.getString(Constant.SAVE_SIM_CONTACT_FILE, ArtCircleUserInfoManager.getArtCircleUserId(mContext)));
            if (!TextUtils.isEmpty(preTimeStr)) {
                long curTime = System.currentTimeMillis();
                long preTime = Long.valueOf(preTimeStr);
                if (curTime - preTime > 24 * 60 * 60 * 1000) {
                    //获取手机联系人
                    getAllContact();
                }
            } else {
                //获取手机联系人
                getAllContact();
            }
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            finish();
            if (TextUtils.equals(Constant.JUMP_PERSONALINFOACTIVITY, ArtCircleUserInfoManager.getUserStep(mContext))) {
                //进入个人资料页面
                jump(mContext, PersonalInfoActivity.class);
            } else if (TextUtils.equals(Constant.JUMP_INVITEFRIENDACTIVITY, ArtCircleUserInfoManager.getUserStep(mContext))) {
                //进入推荐大咖页面
                Intent intent = new Intent(mContext, AttentionArtistActivity.class);
                intent.putExtra(IntentConstant.ACTIVITY_TITLE, getString(R.string.recommend_artist));
                intent.putExtra(IntentConstant.ACTIVITY_TYPE, IntentConstant.JUMP_TO_RECOMMEND_ARTIST);
                jump(intent);
            } else if (TextUtils.equals(Constant.JUMP_ATTENTIONARTISTACTIVITY, ArtCircleUserInfoManager.getUserStep(mContext))) {
                //进入拓展社交圈
                Intent intent = new Intent(mContext, InviteFriendActivity.class);
                intent.putExtra(IntentConstant.ACTIVITY_TYPE, IntentConstant.JUMP_TO_INVITED_FRIENDS_PLAY);
                intent.putExtra(IntentConstant.ACTIVITY_TITLE, getString(R.string.invited_friends_play));
                jump(intent);
            } else {
                // 进入主页面
                jump(mContext, MainActivity.class);
            }
        }

        @Override
        public void onProgress(int progress, String status) {
        }

        @Override
        public void onError(final int code, final String message) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    Toast.makeText(mContext, StringUtils.getString(getString(R.string.login_failure_failed), message), Toast.LENGTH_SHORT).show();
//                    DialogUtils.showShortPromptToast(mContext, StringUtils.getString(getString(R.string.Login_failed), message));
                }
            });
        }
    }

    private void initializeContacts() {
        Map<String, User> userlist = new HashMap<>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(R.string.application_and_notify);
        newFriends.setNick(strChat);
        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);

        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        User robotUser = new User();
        String strRobot = getResources().getString(R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(mContext);
        List<User> users = new ArrayList<>(userlist.values());
        dao.saveContactList(users);
    }

    /**
     * 获取所有手机通讯录联系人
     */
    private void getAllContact() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        //获取手机通讯录联系人
        getPhoneContacts();
        //获取SIM卡联系人
        switch (mTelephonyManager.getSimState()) {
            case TelephonyManager.SIM_STATE_READY:
                getSIMContactList();
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                DialogUtils.showShortPromptToast(mContext, R.string.sim_invalid);
                break;
            default:
                DialogUtils.showShortPromptToast(mContext, R.string.sim_clocked);
                break;
        }
        //  导出所有联系人通讯录
        exportAllContact(contactList);
    }

    /**
     * 得到手机通讯录联系人信息
     **/
    private void getPhoneContacts() {
        contactList.clear();
        // 获取手机联系人
        Cursor phoneCursor = mResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, Constant.PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                FetchContactResponse.DataEntity contact = new FetchContactResponse.DataEntity();
                //得到手机号码
                String phoneNumber = phoneCursor.getString(Constant.PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(Constant.PHONES_DISPLAY_NAME_INDEX);
                contact.setTel(phoneNumber);
                contact.setName(contactName);
                contactList.add(contact);
            }
            phoneCursor.close();
        }
    }

    /**
     * 获取SIM卡联系人
     */
    private void getSIMContactList() {
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = mResolver.query(uri, Constant.PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                FetchContactResponse.DataEntity contact = new FetchContactResponse.DataEntity();
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(Constant.PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor.getString(Constant.PHONES_DISPLAY_NAME_INDEX);
                contact.setTel(phoneNumber);
                contact.setName(contactName);
                contactList.add(contact);
            }
            phoneCursor.close();
        }
    }

    //导出SIM联系人通讯录
    private void exportAllContact(List<FetchContactResponse.DataEntity> contactList) {
        String username = "";
        StringBuilder simContactStringBuilder = new StringBuilder();
        if (contactList == null || contactList.size() == 0) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            DialogUtils.showShortPromptToast(mContext, R.string.export_sim_contact_empty);
            return;
        }
        for (FetchContactResponse.DataEntity user : contactList) {
            try {
                username = URLEncoder.encode(user.getName(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            simContactStringBuilder.append(username).append(":").append(user.getTel().replaceAll(" ", "")).append(";");
        }
        String str = simContactStringBuilder.substring(0, simContactStringBuilder.length() - 1);
        new HttpUtils().configCurrentHttpCacheExpiry(0).send(HttpRequest.HttpMethod.POST, NetConstant.HOST,
                NetConstant.getCommitSIMContactParams(mContext, str), new ExportAllContactListener());
    }

    private class ExportAllContactListener extends RequestCallBack<String> implements StringToBeanTask.ConvertListener<ExportSIMContactResponse> {

        @Override
        public void onConvertSuccess(ExportSIMContactResponse data) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (data == null || data.isError() || data.getData().size() == 0) {
                Toast.makeText(mContext, R.string.export_sim_contact_fail, Toast.LENGTH_SHORT).show();
//                DialogUtils.showShortPromptToast(mContext, R.string.export_sim_contact_fail);
                return;
            }
            //记录导出联系人时间
            long saveTime = System.currentTimeMillis(); //获取当前时间
            FileUtils.writeStringToFileCache(mContext, StringUtils.getString(Constant.SAVE_SIM_CONTACT_FILE, ArtCircleUserInfoManager.getArtCircleUserId(mContext)),
                    String.valueOf(saveTime));
        }

        @Override
        public void onConvertFailed(String json) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(mContext, R.string.export_sim_contact_fail, Toast.LENGTH_SHORT).show();
//            DialogUtils.showShortPromptToast(mContext, R.string.export_sim_contact_fail);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            StringToBeanTask<ExportSIMContactResponse> task = new StringToBeanTask<>(ExportSIMContactResponse.class, this);
            task.execute(responseInfo.result);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            Toast.makeText(mContext, R.string.export_sim_contact_fail, Toast.LENGTH_SHORT).show();
//            DialogUtils.showShortPromptToast(mContext, R.string.export_sim_contact_fail);
        }
    }

    /**
     * 保存用户基本信息
     */
    private void saveBaseInfo(ArtCircleLoginResponse response) {
        Properties properties = new Properties();
        properties.put(Constant.SP_KEY_USER_NAME, response.getData().getUser_name());
        properties.put(Constant.SP_KEY_USER_MAJOR, response.getData().getMajor());
        properties.put(Constant.SP_KEY_USER_HONOR, response.getData().getHonor());
        properties.put(Constant.SP_KEY_USER_SEX, response.getData().getSex());
        properties.put(Constant.SP_KEY_USER_INFLUENCE, response.getData().getInfluence());
        properties.put(Constant.SP_KEY_USER_BACKGROUND, response.getData().getBackground());
        properties.put(Constant.SP_KEY_USER_POSITION, response.getData().getPosition());
        properties.put(Constant.SP_KEY_USER_CERTIFICATE, response.getData().getCertificate());
        properties.put(Constant.SP_KEY_USER_RANKING, String.valueOf(response.getData().getRanking()));
        properties.put(Constant.SP_KEY_USER_TYPE, String.valueOf(response.getData().getType()));
        properties.put(Constant.SP_KEY_USER_FANS_NUM, response.getData().getFans());
        properties.put(Constant.SP_KEY_USER_ON_FANS_NUM, response.getData().getOn_fans());
        properties.put(Constant.SP_KEY_USER_IS_NEW_FANS, String.valueOf(response.getData().getIs_new_fans()));
        properties.put(Constant.SP_KEY_USER_PHOTO_NUM, response.getData().getPhoto());
        ArtCircleBaseInfoManager.getsInstance().saveBaseInfo(mContext, properties);
    }
}
