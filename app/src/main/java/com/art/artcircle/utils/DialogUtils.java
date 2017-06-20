package com.art.artcircle.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.art.artcircle.R;
import com.art.artcircle.dialog.CommonDialog;
import com.art.artcircle.dialog.CustomerAlertDialog;
import com.art.artcircle.dialog.ProgressDialog;
import com.art.artcircle.dialog.QRCodeDialog;

public class DialogUtils {
    private static Toast mShortPromptToast;
    private static Toast mLongPromptToast;

    public static void showShortPromptToast(final Context context, final int resid) {
        final Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mShortPromptToast == null) {
                    mShortPromptToast = Toast.makeText(activity, resid, Toast.LENGTH_SHORT);
                }
                mShortPromptToast.setText(resid);
                mShortPromptToast.show();
            }
        });
    }

    public static void showShortPromptToast(final Context context, final String res) {
        final Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mShortPromptToast == null) {
                    mShortPromptToast = Toast.makeText(activity, res, Toast.LENGTH_SHORT);
                }
                mShortPromptToast.setText(res);
                mShortPromptToast.show();
            }
        });
        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }

    public static void showLongPromptToast(Context context, String... res) {
        StringBuilder content = new StringBuilder();
        for (String string : res) {
            content.append(string);
        }
        if (mLongPromptToast == null) {
            mLongPromptToast = Toast.makeText(context, content.toString(), Toast.LENGTH_SHORT);
        }
        mLongPromptToast.setText(content.toString());
        mLongPromptToast.show();
    }

    public static void showLongPromptToast(Context context, int resid) {
        if (mLongPromptToast == null) {
            mLongPromptToast = Toast.makeText(context, resid, Toast.LENGTH_LONG);
        }
        mLongPromptToast.setText(resid);
        mLongPromptToast.show();
    }

    /**
     * 弹出显示进度的dialog
     */
    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.show();
        return dialog;
    }

    /**
     * 弹出显示进度的dialog
     */
    public static ProgressDialog showProgressDialog(Context context, String title) {
        ProgressDialog dialog = new ProgressDialog(context, title);
        dialog.show();
        return dialog;
    }

//    /**
//     * 弹出调试信息对话框
//     */
//    public static DebugDialog showDebugInfoDialog(Context context, String info) {
//        DebugDialog dialog = new DebugDialog(context, info);
//        dialog.show();
//        return dialog;
//    }

//    /**
//     * 弹出调试信息对话框
//     */
//    public static DebugDialog showDebugInfoDialog(Context context) {
//        DebugDialog dialog = new DebugDialog(context);
//        dialog.show();
//        return dialog;
//    }

//    /**
//     * 弹出调试信息对话框
//     */
//    public static ChooseDialog showChooseDialog(Context context) {
//        ChooseDialog dialog = new ChooseDialog(context);
//        dialog.show();
//        return dialog;
//    }

//    /**
//     * 弹出免责声明对话框
//     */
//    public static ResponsibilityDialog showResponsibilityDialog(Context context, View.OnClickListener listener) {
//        ResponsibilityDialog dialog = new ResponsibilityDialog(context, listener);
//        dialog.show();
//        return dialog;
//    }

//    public static ScanCodeDialog showScanCodeDialog(Context context) {
//        ScanCodeDialog dialog = new ScanCodeDialog(context, R.style.CustomDialog);
//        ImageView ecodeImg = (ImageView) dialog.findViewById(R.id.img_qr_code);
//        TextView inviteCodeTv = (TextView) dialog.findViewById(R.id.tv_invite_code);
//        String userId = UserInfoUtils.getUserId(context);
//        inviteCodeTv.setText(userId);
//        inviteCodeTv.setVisibility(TextUtils.isEmpty(userId) ? View.GONE : View.VISIBLE);
//        ecodeImg.setImageResource(R.drawable.icon_scan_code);
//        dialog.show();
//        return dialog;
//    }

    public static void showConfirmDialog(Context context, int strTitleResId, int strMsgResId, final DialogInterface.OnClickListener listener) {
        if (listener != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(strTitleResId).setMessage(strMsgResId)
                    .setPositiveButton(R.string.positive, listener)
                    .setNegativeButton(R.string.negative, null).show();
        }
    }

    @NonNull
    public static void showConfirmDialog(Context context, String strTitle, String strMsg, final DialogInterface.OnClickListener listener) {
        if (listener != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(strTitle).setMessage(strMsg)
                    .setPositiveButton(R.string.positive, listener)
                    .setNegativeButton(R.string.negative, null).show();
        }
    }

    @NonNull
    public static void showConfirmDialog(Context context, int strTitleResId, final DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(strTitleResId).setPositiveButton(R.string.positive, listener)
                .setNegativeButton(R.string.negative, null).show();
    }

    @NonNull
    public static void showPickPhotoSrcDialog(Context context, String strTitle, String strMsg,
                                              final DialogInterface.OnClickListener takePhotoListener,
                                              final DialogInterface.OnClickListener pickPhotoListener) {
        if (takePhotoListener != null && pickPhotoListener != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(strTitle).setMessage(strMsg)
                    .setPositiveButton(R.string.attach_take_pic, takePhotoListener)
                    .setNegativeButton(R.string.attach_picture, pickPhotoListener).show();
        }
    }

    @NonNull
    public static Dialog showPickPhotoDialog(Context context, int layoutId, int selectCameraId, int selectPhotoId,
                                             View.OnClickListener takePhotoListener,
                                             View.OnClickListener pickPhotoListener) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
        dialogWindow.setAttributes(lp);
        dialog.setCancelable(true);
        dialog.show();

        View mSelectCamera = view.findViewById(selectCameraId);
        View mSelectPhoto = view.findViewById(selectPhotoId);

        if (mSelectCamera != null) {
            mSelectCamera.setOnClickListener(takePhotoListener);
        }
        if (mSelectPhoto != null) {
            mSelectPhoto.setOnClickListener(pickPhotoListener);
        }
        return dialog;
    }

    /**
     * 确认取消修改对话框
     */
    @NonNull
    public static void showConfirmCancelDialog(Context context, String strTitle, String strMsg, String confirmMsg, String cancelMsg,
                                               final DialogInterface.OnClickListener confirmListener,
                                               final DialogInterface.OnClickListener cancelListener) {
        if (confirmListener != null && cancelListener != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(strTitle).setMessage(strMsg)
                    .setPositiveButton(confirmMsg, confirmListener)
                    .setNegativeButton(cancelMsg, cancelListener).show();
        }
    }

    @NonNull
    public static void showConfirmCancelDialog(Context context, int strTitle, int strMsg, int confirmMsg, int cancelMsg,
                                               final DialogInterface.OnClickListener confirmListener,
                                               final DialogInterface.OnClickListener cancelListener) {
        if (confirmListener != null && cancelListener != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(strTitle).setMessage(strMsg)
                    .setCancelable(false)
                    .setPositiveButton(confirmMsg, confirmListener)
                    .setNegativeButton(cancelMsg, cancelListener).show();
        }
    }

    @NonNull
    public static void showConfirmDialog(Context context, int strTitle, int strMsg, int confirmMsg,
                                         final DialogInterface.OnClickListener confirmListener) {
        if (confirmListener != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(strTitle).setMessage(strMsg)
                    .setPositiveButton(confirmMsg, confirmListener)
                    .setNegativeButton(R.string.negative, null).show();
        }
    }

    @NonNull
    public static Dialog showConfirmDialog(Context context, int layoutId, int titleId, int contentId, int confirmId, String title, String content,
                                           String confirm,
                                           final View.OnClickListener confirmListener) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
        dialogWindow.setAttributes(lp);
        dialog.setCancelable(true);
        dialog.show();

        TextView titleView = (TextView) view.findViewById(titleId);
        TextView contentView = (TextView) view.findViewById(contentId);
        TextView mConfirmView = (TextView) view.findViewById(confirmId);
        titleView.setText(title);
        contentView.setText(content);
        mConfirmView.setText(confirm);
        mConfirmView.setOnClickListener(confirmListener);
        return dialog;
    }

    /**
     * 完善资料的弹窗
     */
    @NonNull
    public static Dialog showContactDetailConfirmDialog(Context context, int layoutId, int titleId, int contentId, int cancelId, int confirmId,
                                                        String title, String content, String cancel, String confirm,
                                                        final View.OnClickListener cancelListener, final View.OnClickListener confirmListener) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        final WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        TextView titleView = (TextView) view.findViewById(titleId);
        TextView contentView = (TextView) view.findViewById(contentId);
        TextView cancelView = (TextView) view.findViewById(cancelId);
        TextView confirmView = (TextView) view.findViewById(confirmId);
        titleView.setText(title);
        contentView.setText(content);
        cancelView.setText(cancel);
        confirmView.setText(confirm);
        confirmView.setOnClickListener(confirmListener);
        cancelView.setOnClickListener(cancelListener);
        return dialog;
    }

    /**
     * @param context
     * @param strTitle        标题，若为两个字则中间要有两个空格的距离
     * @param strContent      内容
     * @param strNeg          取消
     * @param strPos          确定
     * @param mSubmitListener 确定按钮的监听事件
     * @param visibleEdit     View.GONE,View.VISIBLE仅有两个取值，代表edittext是否显示
     * @param visibleImg      好评对话框时设置为显示
     * @return
     */
    @NonNull
    public static CommonDialog showCommonDialog(Context context, String strTitle, String strContent, String strNeg, String strPos,
                                                View.OnClickListener mSubmitListener, View.OnClickListener mCancelListener, int visibleEdit, int visibleImg) {
        CommonDialog dialog = new CommonDialog(context, strTitle, strContent, strNeg, strPos, mSubmitListener, mCancelListener, visibleEdit, visibleImg);
        dialog.show();
        return dialog;
    }

    @NonNull
    public static void showWarnDialog(Context context, String strTitle, String strMessage, final DialogInterface.OnClickListener listener) {
        if (listener != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(strTitle).setMessage(strMessage).setNegativeButton(R.string.positive, listener).show();
        }
    }

    @NonNull
    public static void showNOCancelConfirmDialog(Context context, String strTitle, String strMsg, final DialogInterface.OnClickListener listener) {
        if (listener != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(strTitle).setMessage(strMsg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.positive, listener).show();
        }
    }

    public static QRCodeDialog generateQRCodeDialog(Context context, String loadInfo) {
        QRCodeDialog dialog = new QRCodeDialog(context, loadInfo);
        dialog.show();
        return dialog;
    }

    public static CustomerAlertDialog generateCustomerAlertDialog(Context context, String mToUserId, String mToUserName, String mShowMsg, String mTitle, String mPath,
                                                                  String mEditText, int mPosition, int mActivityType, boolean mIsCancelTitle, boolean mIsCancelShow,
                                                                  boolean mIsEditTextShow) {
        CustomerAlertDialog dialog =
                new CustomerAlertDialog(context, mToUserId, mToUserName, mShowMsg, mTitle, mPath, mEditText, mPosition, mActivityType, mIsCancelTitle, mIsCancelShow,
                        mIsEditTextShow);
        dialog.show();
        return dialog;
    }

    @NonNull
    public static Dialog showImgHandleDialog(Context context, int layoutId, int savePhotoId, int frowardPhotoId,
                                             View.OnClickListener savePhotoListener,
                                             View.OnClickListener frowardPhotoListener) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER_VERTICAL);
        dialogWindow.setAttributes(lp);
        dialog.setCancelable(true);
        dialog.show();

        View mSavePhoto = view.findViewById(savePhotoId);
        View mFrowardPhoto = view.findViewById(frowardPhotoId);

        if (mSavePhoto != null) {
            mSavePhoto.setOnClickListener(savePhotoListener);
        }
        if (mFrowardPhoto != null) {
            mFrowardPhoto.setOnClickListener(frowardPhotoListener);
        }
        return dialog;
    }
}
