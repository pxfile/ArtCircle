package com.art.artcircle.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.art.artcircle.R;
import com.art.artcircle.activity.EditEducationExperienceActivity;
import com.art.artcircle.activity.EducationExperienceActivity;
import com.art.artcircle.bean.EducationExperienceResponse;
import com.art.artcircle.bean.NormalMessageResponse;
import com.art.artcircle.constant.EventEnum;
import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.constant.NetConstant;
import com.art.artcircle.task.StringToBeanTask;
import com.art.artcircle.utils.ActivityUtils;
import com.art.artcircle.utils.DialogUtils;
import com.art.artcircle.utils.StringUtils;
import com.art.artcircle.utils.UmengUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

/**
 * Created by Fang on 2015/8/26.
 */
public class EducationExperienceAdapter extends ArrayAdapter<EducationExperienceResponse.DataEntity> implements View.OnClickListener {
    private List<EducationExperienceResponse.DataEntity> mData;
    private Context mContext;
    private LayoutInflater mInflater;

    public EducationExperienceAdapter(Context context, int resource, List<EducationExperienceResponse.DataEntity> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mData = objects;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_experience, parent, false);
        }
        ContentHolder vh = (ContentHolder) convertView.getTag();
        if (vh == null) {
            vh = new ContentHolder(convertView);
            convertView.setTag(vh);
        }
        vh.mExperienceEditView.setVisibility(View.VISIBLE);
        vh.mExperienceDeleteView.setVisibility(View.VISIBLE);

        EducationExperienceResponse.DataEntity dataEntity = mData.get(position);
        vh.mSchoolTvView.setText(dataEntity.getSchool());
        vh.mPositionTvView.setVisibility(View.VISIBLE);
        vh.mPositionTvView.setText(dataEntity.getPosition());
        vh.mStartTimeTvView.setText(dataEntity.getStime());
        vh.mEndTimeTvView.setText(dataEntity.getEtime());
        vh.mExperienceDesTvView.setText(dataEntity.getNote());
        vh.mExperienceEditView.setTag(position);
        vh.mExperienceEditView.setOnClickListener(this);
        vh.mExperienceAcademicTvView.setVisibility(View.VISIBLE);
        vh.mExperienceAcademicTvView.setText(dataEntity.getAcademic());
        vh.mExperienceMajorTvView.setVisibility(View.VISIBLE);
        vh.mExperienceMajorTvView.setText(dataEntity.getMajor());
        vh.mExperienceDeleteView.setTag(position);
        vh.mExperienceDeleteView.setOnClickListener(this);

        vh.mSchoolTvView.setVisibility(TextUtils.isEmpty(dataEntity.getSchool()) ? View.GONE : View.VISIBLE);
        vh.mPositionTvView.setVisibility(TextUtils.isEmpty(dataEntity.getPosition()) ? View.GONE : View.VISIBLE);
        vh.mStartTimeTvView.setVisibility(TextUtils.isEmpty(dataEntity.getStime()) ? View.GONE : View.VISIBLE);
        vh.mEndTimeTvView.setVisibility(TextUtils.isEmpty(dataEntity.getEtime()) ? View.GONE : View.VISIBLE);
        vh.mExperienceDesTvView.setVisibility(TextUtils.isEmpty(dataEntity.getNote()) ? View.GONE : View.VISIBLE);
        vh.mExperienceAcademicTvView.setVisibility(TextUtils.isEmpty(dataEntity.getAcademic()) ? View.GONE : View.VISIBLE);
        vh.mExperienceMajorTvView.setVisibility(TextUtils.isEmpty(dataEntity.getMajor()) ? View.GONE : View.VISIBLE);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        final int position;
        final EducationExperienceResponse.DataEntity dataEntity;
        switch (v.getId()) {
            case R.id.tv_edit:
                position = (int) v.getTag();
                dataEntity = mData.get(position);
                Intent intent = new Intent(mContext, EditEducationExperienceActivity.class);
                intent.putExtra(IntentConstant.JUMP_ACTIVITY_FLAG, IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT);
                intent.putExtra(EducationExperienceActivity.EDUCATION_SCHOOL, dataEntity.getSchool());
                intent.putExtra(EducationExperienceActivity.EDUCATION_START_TIME, dataEntity.getStime());
                intent.putExtra(EducationExperienceActivity.EDUCATION_END_TIME, dataEntity.getEtime());
                intent.putExtra(EducationExperienceActivity.EDUCATION_NOTE, dataEntity.getNote());
                intent.putExtra(EducationExperienceActivity.EDUCATION_ACADEMIC, dataEntity.getAcademic());
                intent.putExtra(EducationExperienceActivity.EDUCATION_MAJOR, dataEntity.getMajor());
                intent.putExtra(EducationExperienceActivity.EDUCATION_ID, dataEntity.getId());
                intent.putExtra(EducationExperienceActivity.EDUCATION_INDEX, position);
                ActivityUtils.jump(mContext, intent);
                UmengUtils.onEvent(mContext, EventEnum.Study_Experience_Edit);
                break;
            case R.id.tv_delete:
                position = (int) v.getTag();
                dataEntity = mData.get(position);
                DialogUtils.showConfirmDialog(mContext, "", mContext.getString(R.string.sure_you_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(dataEntity.getId())) {
                            DialogUtils.showShortPromptToast(mContext, R.string.delete_study_experience_failed);
                            return;
                        }
                        new HttpUtils().configCurrentHttpCacheExpiry(0)
                                .send(HttpRequest.HttpMethod.GET, NetConstant.HOST, NetConstant.getDeleteEducationExperienceParams(mContext, dataEntity.getId()),
                                        new DeleteEducationExperienceListener(position));
                    }
                });
                UmengUtils.onEvent(mContext, EventEnum.Study_Experience_Delete);
                break;
        }
    }

    private class ContentHolder {
        private TextView mSchoolTvView;
        private TextView mPositionTvView;
        private TextView mStartTimeTvView;
        private TextView mEndTimeTvView;
        private TextView mExperienceMajorTvView;
        private TextView mExperienceAcademicTvView;
        private TextView mExperienceDesTvView;
        private TextView mExperienceEditView;
        private TextView mExperienceDeleteView;


        public ContentHolder(View itemView) {
            mSchoolTvView = (TextView) itemView.findViewById(R.id.tv_school);
            mPositionTvView = (TextView) itemView.findViewById(R.id.tv_position);
            mStartTimeTvView = (TextView) itemView.findViewById(R.id.tv_start_time);
            mEndTimeTvView = (TextView) itemView.findViewById(R.id.tv_end_time);
            mExperienceMajorTvView = (TextView) itemView.findViewById(R.id.tv_experience_major);
            mExperienceAcademicTvView = (TextView) itemView.findViewById(R.id.tv_experience_academic);
            mExperienceDesTvView = (TextView) itemView.findViewById(R.id.tv_experience_des);
            mExperienceEditView = (TextView) itemView.findViewById(R.id.tv_edit);
            mExperienceDeleteView = (TextView) itemView.findViewById(R.id.tv_delete);
        }
    }

    private class DeleteEducationExperienceListener extends RequestCallBack<String> implements StringToBeanTask.ConvertListener<NormalMessageResponse> {
        private int mPosition;

        public DeleteEducationExperienceListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onConvertSuccess(NormalMessageResponse data) {
            if (data == null || data.getError()) {
                DialogUtils.showShortPromptToast(mContext, R.string.delete_study_experience_failed);
                return;
            }
            DialogUtils.showShortPromptToast(mContext, R.string.delete_study_experience_success);
            mData.remove(mData.get(mPosition));
            notifyDataSetChanged();
        }

        @Override
        public void onConvertFailed(String json) {
            DialogUtils.showShortPromptToast(mContext, R.string.delete_study_experience_failed);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            StringToBeanTask<NormalMessageResponse> task = new StringToBeanTask<>(NormalMessageResponse.class, this);
            task.execute(responseInfo.result);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            DialogUtils.showShortPromptToast(mContext,
                    StringUtils.getString(mContext.getString(R.string.delete_study_experience_failed), mContext.getString(R.string.colon), e.getMessage()));
        }
    }
}

