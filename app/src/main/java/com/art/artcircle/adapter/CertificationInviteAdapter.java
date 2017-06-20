package com.art.artcircle.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.art.artcircle.R;
import com.art.artcircle.activity.CertificationInviteActivity;
import com.art.artcircle.activity.ContactDetailActivity;
import com.art.artcircle.activity.InvitedFriendDetailActivity;
import com.art.artcircle.bean.CertificationInviteResponse;
import com.art.artcircle.bean.NormalMessageResponse;
import com.art.artcircle.constant.Constant;
import com.art.artcircle.constant.EventEnum;
import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.constant.NetConstant;
import com.art.artcircle.task.StringToBeanTask;
import com.art.artcircle.utils.ActivityUtils;
import com.art.artcircle.utils.DialogUtils;
import com.art.artcircle.utils.StringUtils;
import com.art.artcircle.utils.UmengUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertificationInviteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private ArrayList<CertificationInviteResponse.DataEntity.ListEntity> mData;

    private Context mContext;

    private Map<Integer, Integer> mPosition;

    private int mFlags;

    private int mInviteStatus;


    public CertificationInviteAdapter(Context context, int mFlags) {
        this.mFlags = mFlags;
        this.mContext = context;
        mData = new ArrayList<>();
        mPosition = new HashMap<>();
    }


    public int getmFlags() {
        return mFlags;
    }

    public void setmFlags(int mFlags) {
        this.mFlags = mFlags;
    }

    public void addData(List<CertificationInviteResponse.DataEntity.ListEntity> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void resetData(List<CertificationInviteResponse.DataEntity.ListEntity> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentHolder(View.inflate(parent.getContext(), R.layout.row_certification_invite, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ContentHolder contentHolder = (ContentHolder) holder;
        CertificationInviteResponse.DataEntity.ListEntity listEntity = mData.get(position);
        contentHolder.portraitView.setTag(listEntity.getHeadimg());
        if (!TextUtils.isEmpty((String) contentHolder.portraitView.getTag()) && TextUtils.equals(listEntity.getHeadimg(), (String) contentHolder.portraitView.getTag())) {
            Uri uri = Uri.parse(listEntity.getHeadimg());
            contentHolder.portraitView.setImageURI(uri);
        } else {
            contentHolder.portraitView.setImageResource(R.drawable.icon_default_avatar);
        }
        contentHolder.nameView.setText(listEntity.getReal_name());
        contentHolder.descView.setText((!TextUtils.isEmpty(listEntity.getHonor()) && listEntity.getHonor().length() > Constant.HONOR_MAX_LENGTH) ?
                listEntity.getHonor().substring(0, Constant.HONOR_MAX_LENGTH) : listEntity.getHonor());

        if (!TextUtils.isEmpty(listEntity.getCertificate()) && TextUtils.equals(listEntity.getCertificate(), "1")) {
            contentHolder.certificationView.setVisibility(View.VISIBLE);
        } else {
            contentHolder.certificationView.setVisibility(View.GONE);
        }
        if (listEntity.getInvite() == 1) {
            contentHolder.inviteView.setBackgroundResource(R.drawable.btn_gray_shape);
            contentHolder.inviteView.setText(mContext.getString(R.string.have_invited));
            contentHolder.inviteView.setEnabled(false);
        } else {
            contentHolder.inviteView.setBackgroundResource(R.drawable.bg_orange_red_button);
            contentHolder.inviteView.setText(mContext.getString(R.string.invite));
            contentHolder.inviteView.setEnabled(true);
        }
        if (mFlags == CertificationInviteActivity.ALL_INVITE) {
            for (int i = 0; i < mData.size(); i++) {
                if (mPosition.containsKey(position)) {
                    contentHolder.inviteView.setBackgroundResource(mPosition.get(position) == 0 ? R.drawable.bg_dialog_submit_orange : R.drawable.btn_gray_shape);
                    contentHolder.inviteView.setText((mPosition.get(position) == 0 ? mContext.getString(R.string.invited) : mContext.getString(R.string.have_invited)));
                    contentHolder.inviteView.setEnabled(mPosition.get(position) == 0 ? true : false);
                }
            }
        } else {
            if (mPosition.containsKey(position)) {
                contentHolder.inviteView.setBackgroundResource(mPosition.get(position) == 0 ? R.drawable.bg_dialog_submit_orange : R.drawable.btn_gray_shape);
                contentHolder.inviteView.setText((mPosition.get(position) == 0 ? mContext.getString(R.string.invited) : mContext.getString(R.string.have_invited)));
                contentHolder.inviteView.setEnabled(mPosition.get(position) == 0 ? true : false);
            }
        }
        contentHolder.inviteView.setTag(R.id.tag_id, position);
        contentHolder.inviteView.setTag(position);
        contentHolder.inviteView.setOnClickListener(this);
        contentHolder.containerView.setTag(position);
        contentHolder.containerView.setOnClickListener(this);

        contentHolder.splitLineView.setVisibility(TextUtils.isEmpty(listEntity.getHonor()) ? View.GONE : View.VISIBLE);
        contentHolder.longLineView.setVisibility(mData.size() > 0 ? (mData.size() - 1 == position ? View.GONE : View.VISIBLE) : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onClick(View v) {
        int position;
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_invite:
                mInviteStatus = (int) v.getTag(R.id.tag_id);
                position = (int) v.getTag();
                String id = mData.get(position).getId();
                inviteCertification(id);
                UmengUtils.onEvent(mContext, EventEnum.Certification_Invite);
                break;
            default:
                position = (int) v.getTag();
                String userId = mData.get(position).getUser_id();
                String mId = mData.get(position).getId();
                if (TextUtils.isEmpty(userId)) {
                    String userName = mData.get(position).getReal_name();
                    String mobile = mData.get(position).getMobile();
                    intent = new Intent(v.getContext(), InvitedFriendDetailActivity.class);
                    intent.putExtra(IntentConstant.USER_NAME, userName);
                    intent.putExtra(IntentConstant.USER_MOBILE, mobile);
                    intent.putExtra(IntentConstant.INVITED_DETAIL_FROM, 1);
                    intent.putExtra(IntentConstant.ID, mId);
                    ActivityUtils.jump(v.getContext(), intent);
                } else {
                    intent = new Intent(v.getContext(), ContactDetailActivity.class);
                    intent.putExtra(IntentConstant.USER_ID, userId);
                    ActivityUtils.jump(v.getContext(), intent);
                }
                UmengUtils.onEvent(mContext, EventEnum.Certification_Invite_Contact_Detail);
                break;
        }
    }

    private class ContentHolder extends RecyclerView.ViewHolder {
        private View containerView;
        private SimpleDraweeView portraitView;
        private TextView nameView;
        private ImageView certificationView;
        private TextView descView;
        private TextView inviteView;
        private View splitLineView;
        private View longLineView;

        public ContentHolder(View itemView) {
            super(itemView);
            containerView = itemView;
            portraitView = (SimpleDraweeView) itemView.findViewById(R.id.img_head);
            nameView = (TextView) itemView.findViewById(R.id.tv_name);
            certificationView = (ImageView) itemView.findViewById(R.id.img_certification_icon);
            descView = (TextView) itemView.findViewById(R.id.tv_des);
            inviteView = (TextView) itemView.findViewById(R.id.tv_invite);
            splitLineView = itemView.findViewById(R.id.split_line);
            longLineView = itemView.findViewById(R.id.long_line);
        }
    }

    /**
     * 邀请认证
     */
    private void inviteCertification(String id) {
        new HttpUtils().configCurrentHttpCacheExpiry(0)
                .send(HttpRequest.HttpMethod.POST, NetConstant.HOST, NetConstant.getInviteCertificationParams(mContext, id), new InviteCertificationListener());
    }

    private class InviteCertificationListener extends RequestCallBack<String> implements StringToBeanTask.ConvertListener<NormalMessageResponse> {

        @Override
        public void onConvertSuccess(NormalMessageResponse data) {
            if (data == null || data.getError()) {
                DialogUtils.showShortPromptToast(mContext, R.string.invite_failed);
                return;
            }
            DialogUtils.showShortPromptToast(mContext, R.string.invite_success);
            mPosition.put(mInviteStatus, Constant.HAVE_INVATED);
            notifyItemChanged(mInviteStatus);
        }

        @Override
        public void onConvertFailed(String json) {
            DialogUtils.showShortPromptToast(mContext, R.string.invite_failed);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            StringToBeanTask<NormalMessageResponse> task = new StringToBeanTask<>(NormalMessageResponse.class, this);
            task.execute(responseInfo.result);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            DialogUtils.showShortPromptToast(mContext, StringUtils.getString(R.string.invite_failed, mContext.getString(R.string.colon), e.getMessage()));
        }
    }
}
