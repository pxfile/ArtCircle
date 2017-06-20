package com.art.artcircle.receiver;

import android.content.Context;
import android.content.Intent;

import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.service.TPushService;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * Created by kakaxicm on 2015/9/7.
 */
public class TPushTextMessageReceiver extends XGPushBaseReceiver {
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {

        //通过后台服务做消息处理
        Intent intent = new Intent(context, TPushService.class);
        String content = xgPushTextMessage.getContent();
        intent.putExtra(IntentConstant.KEY_PUSH_RAW_MESSAGE, content);
        context.startService(intent);
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }
}
