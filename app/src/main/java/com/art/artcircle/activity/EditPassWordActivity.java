package com.art.artcircle.activity;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.art.artcircle.R;
import com.art.artcircle.application.DemoApplication;
import com.art.artcircle.bean.NormalMessageResponse;
import com.art.artcircle.constant.Constant;
import com.art.artcircle.constant.EventEnum;
import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.constant.NetConstant;
import com.art.artcircle.db.UserDao;
import com.art.artcircle.dialog.CommonDialog;
import com.art.artcircle.listener.XGPushCallback;
import com.art.artcircle.manager.ArtCircleUserInfoManager;
import com.art.artcircle.task.StringToBeanTask;
import com.art.artcircle.utils.DialogUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPassWordActivity extends BaseActivity {
    private EditText mOldPaswdEt;
    private EditText mNewPaswdEt1;
    private EditText mNewPaswdEt2;
    private View mCommitBtn;

    private boolean isSubmit;

    private String mJumpFlag;

    private CommonDialog mCommonDialog;
    private View.OnClickListener mSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!TextUtils.isEmpty(mJumpFlag) && TextUtils.equals(IntentConstant.ACCOUNT_CONFLICT, mJumpFlag)) {
                String username = DemoApplication.getInstance().getUserName();
                String password = DemoApplication.getInstance().getPassword();
                EMChatManager.getInstance().login(username, password, new ArtCircleEMCallBack());
            }
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
        mJumpFlag = getIntent().getStringExtra(IntentConstant.ACTIVITY_TYPE);
    }

    @Override
    protected int setLayoutViewId() {
        return R.layout.activity_edit_password;
    }

    @Override
    protected void initTitle() {
        TextView backView = (TextView) findViewById(R.id.tv_back);
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        titleView.setText(getString(R.string.modify_password));
        setOnClickListener(backView);
    }

    @Override
    protected void initContent() {
        mOldPaswdEt = (EditText) findViewById(R.id.et_old_password);
        mNewPaswdEt1 = (EditText) findViewById(R.id.et_new_password1);
        mNewPaswdEt2 = (EditText) findViewById(R.id.et_new_password2);
        mCommitBtn = findViewById(R.id.btn_commit);
        setOnClickListener(mCommitBtn);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                backPreviousPage();
                break;
            case R.id.btn_commit:
                if (!ArtCircleUserInfoManager.getsInstance().checkUserLogin(mContext)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.you_have_not_logged_in);
                    jump(mContext, LoginActivity.class);
                    return;
                }
                String oldPswd = mOldPaswdEt.getText().toString().trim();
                String newPswd1 = mNewPaswdEt1.getText().toString().trim();
                String newPswd2 = mNewPaswdEt2.getText().toString().trim();
                if (TextUtils.isEmpty(oldPswd)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.fill_in_original_password);
                    return;
                }
                if (newPswd1.length() < 6 || newPswd1.length() > 20) {
                    DialogUtils.showShortPromptToast(mContext, R.string.new_password_between);
                    return;
                }
                if (TextUtils.isEmpty(newPswd2) || !newPswd2.equals(newPswd1)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.two_input_password_different);
                    return;
                }
                UmengUtils.onEvent(mContext, EventEnum.SUBMIT_EDIT_USER_PASSWORD);
                commitEditPasswordRequest(oldPswd, newPswd1);
                break;
        }
    }

    private void commitEditPasswordRequest(String oldPwd, String newPwd) {
        new HttpUtils().configCurrentHttpCacheExpiry(0)
                .send(HttpRequest.HttpMethod.GET, NetConstant.HOST, NetConstant.getCommitEditPasswordParams(mContext, oldPwd, newPwd), new EditPasswordListener());
    }


    private class EditPasswordListener extends RequestCallBack<String> implements StringToBeanTask.ConvertListener<NormalMessageResponse> {

        @Override
        public void onConvertSuccess(NormalMessageResponse data) {
            if (data == null || data.getError()) {
                DialogUtils.showShortPromptToast(mContext, R.string.modify_password_fail);
                return;
            }
            isSubmit = true;
            DialogUtils.showShortPromptToast(mContext, R.string.modify_password_success);
            DemoHXSDKHelper.getInstance().logout(false, null);
            ArtCircleUserInfoManager.getsInstance().logout(mContext);
            // 重新显示登陆页面
            finish();
            jump(mContext, LoginActivity.class);
        }

        @Override
        public void onConvertFailed(String json) {
            DialogUtils.showShortPromptToast(mContext, R.string.modify_password_fail);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            StringToBeanTask<NormalMessageResponse> task = new StringToBeanTask<>(NormalMessageResponse.class, this);
            task.execute(responseInfo.result);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            DialogUtils.showShortPromptToast(mContext, R.string.modify_password_fail);
        }
    }

    /**
     * 选择提示对话框
     */
    private void showPickDialog() {
        mCommonDialog = DialogUtils.showCommonDialog(mContext, getString(R.string.prompt), getString(R.string.cancel_modify_password),
                getString(R.string.continue_modify), getString(R.string.confirm_cancel), mSubmitListener, mCancelListener, View.GONE, View.GONE);
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

    /**
     * 重新登录回调
     */
    private class ArtCircleEMCallBack implements EMCallBack {
        @Override
        public void onSuccess() {
            //登陆成功注册信鸽的单点推送
            XGPushManager.registerPush(mContext, DemoApplication.getPushName(mContext), new XGPushCallback(DemoApplication.getPushName(mContext)));
            try {
                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                // 处理好友和群组
                initializeContacts();
            } catch (Exception e) {
                // 取好友或者群聊失败，不让进入主页面
                runOnUiThread(new Runnable() {
                    public void run() {
                        ArtCircleUserInfoManager.getsInstance().logout(mContext);
                        DemoHXSDKHelper.getInstance().logout(true, null);
                        DialogUtils.showShortPromptToast(mContext, R.string.relogin_failed);
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
            DialogUtils.showShortPromptToast(mContext, R.string.relogin_success);
        }

        @Override
        public void onProgress(int progress, String status) {
        }

        @Override
        public void onError(final int code, final String message) {
            runOnUiThread(new Runnable() {
                public void run() {
                    DialogUtils.showShortPromptToast(mContext, StringUtils.getString(getString(R.string.relogin_failed), message));
                }
            });
        }
    }

    /**
     * 处理好友和群组
     */
    private void initializeContacts() {
        Map<String, User> userList = new HashMap<>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(R.string.application_and_notify);
        newFriends.setNick(strChat);
        userList.put(Constant.NEW_FRIENDS_USERNAME, newFriends);

        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userList.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        User robotUser = new User();
        String strRobot = getResources().getString(R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userList.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userList);
        // 存入db
        UserDao dao = new UserDao(EditPassWordActivity.this);
        List<User> users = new ArrayList<>(userList.values());
        dao.saveContactList(users);
    }
}
