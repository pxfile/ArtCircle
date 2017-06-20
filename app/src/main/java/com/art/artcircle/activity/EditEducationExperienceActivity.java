package com.art.artcircle.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.art.artcircle.R;
import com.art.artcircle.bean.NormalMessageResponse;
import com.art.artcircle.constant.Constant;
import com.art.artcircle.constant.EventEnum;
import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.constant.NetConstant;
import com.art.artcircle.task.StringToBeanTask;
import com.art.artcircle.utils.DateTimePickDialogUtils;
import com.art.artcircle.utils.DateUtils;
import com.art.artcircle.utils.DialogUtils;
import com.art.artcircle.utils.SizeUtils;
import com.art.artcircle.utils.StringUtils;
import com.art.artcircle.utils.UmengUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class EditEducationExperienceActivity extends BaseActivity {
    private RelativeLayout mMajorRlView;
    private RelativeLayout mEducationRlView;
    private RelativeLayout mSchoolRliew;

    private TextView mSchoolEtView;
    private TextView mMajorTvView;
    private TextView mSTimeTvView;
    private TextView mETimeTvView;
    private TextView mEducationTvView;
    private EditText mDesEtView;

    private int mJumpFlag;//判断是添加，还是编辑
    private String mSchoolStr;
    private String mStartTimeStr;
    private String mEndTimeStr;
    private String mMajorStr;
    private String mAcademicStr;
    private String mNoteStr;
    private String mIDStr;

    private String mSelectName;
    private String mSchoolSelectName;
    private int mJumpFrom;//判断是专业的添加，专业的编辑，学历的添加，学历的编辑

    private DateTimePickDialogUtils mDateTimePickDialogUtils;

    @Override
    protected void getIntentData() {
        mJumpFlag = getIntent().getIntExtra(IntentConstant.JUMP_ACTIVITY_FLAG, 0);
        if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD) {
            return;
        }
        mSchoolStr = getIntent().getStringExtra(EducationExperienceActivity.EDUCATION_SCHOOL);
        mStartTimeStr = getIntent().getStringExtra(EducationExperienceActivity.EDUCATION_START_TIME);
        mEndTimeStr = getIntent().getStringExtra(EducationExperienceActivity.EDUCATION_END_TIME);
        mMajorStr = getIntent().getStringExtra(EducationExperienceActivity.EDUCATION_MAJOR);
        mAcademicStr = getIntent().getStringExtra(EducationExperienceActivity.EDUCATION_ACADEMIC);
        mNoteStr = getIntent().getStringExtra(EducationExperienceActivity.EDUCATION_NOTE);
        mIDStr = getIntent().getStringExtra(EducationExperienceActivity.EDUCATION_ID);
    }

    @Override
    protected int setLayoutViewId() {
        return R.layout.activity_edit_education_experience;
    }

    @Override
    protected void initTitle() {
        String title = "";
        if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD) {
            title = getString(R.string.add_study_experience);
        } else if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT) {
            title = getString(R.string.et_study_experience);
        }
        TextView backView = (TextView) findViewById(R.id.tv_back);
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        titleView.setText(title);
        TextView saveView = (TextView) findViewById(R.id.tv_right);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.right_save_icon);
        int size = (int) SizeUtils.dp2Px(getResources(), 24.0f);
        drawable.setBounds(0, 0, size, size);
        saveView.setCompoundDrawables(drawable, null, null, null);
        setOnClickListener(backView, saveView);
    }

    @Override
    protected void initContent() {
        mMajorRlView = (RelativeLayout) findViewById(R.id.rl_position);
        mEducationRlView = (RelativeLayout) findViewById(R.id.rl_education);
        mSchoolRliew= (RelativeLayout) findViewById(R.id.rl_school);

        mSchoolEtView = (TextView) findViewById(R.id.et_school);
        mMajorTvView = (TextView) findViewById(R.id.et_position);
        mSTimeTvView = (TextView) findViewById(R.id.tv_stime);
        mETimeTvView = (TextView) findViewById(R.id.tv_etime);
        mEducationTvView = (TextView) findViewById(R.id.et_education);
        mDesEtView = (EditText) findViewById(R.id.et_des);

        setOnClickListener(mMajorRlView, mEducationRlView, mSTimeTvView, mETimeTvView,mSchoolRliew);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_school:
                intent = new Intent(mContext, AddSchoolActivity.class);
                if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD) {
                    intent.putExtra(IntentConstant.JUMP_ACTIVITY_FLAG, IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD_SCHOOL);
                } else if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT) {
                    intent.putExtra(IntentConstant.JUMP_ACTIVITY_FLAG, IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT_SCHOOL);
                    intent.putExtra(IntentConstant.EDUCATION_NAME, mSchoolEtView.getText().toString());
                }
                jump(intent, IntentConstant.SELECT_EDUCATION);
                break;
            case R.id.rl_position:
                intent = new Intent(mContext, ChooseMajorActivity.class);
                if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD) {
                    intent.putExtra(IntentConstant.JUMP_ACTIVITY_FLAG, IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD_MAJOR);
                } else if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT) {
                    intent.putExtra(IntentConstant.JUMP_ACTIVITY_FLAG, IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT_MAJOR);
                    intent.putExtra(IntentConstant.EDUCATION_NAME, mMajorTvView.getText().toString());
                }
                jump(intent, IntentConstant.SELECT_EDUCATION);
                UmengUtils.onEvent(mContext, EventEnum.Study_Experience_Edit_Position);
                break;
            case R.id.rl_education:
                intent = new Intent(mContext, ChooseMajorActivity.class);
                if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD) {
                    intent.putExtra(IntentConstant.JUMP_ACTIVITY_FLAG, IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD_EDUCATION);
                } else if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT) {
                    intent.putExtra(IntentConstant.JUMP_ACTIVITY_FLAG, IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT_EDUCATION);
                    intent.putExtra(IntentConstant.EDUCATION_NAME, mEducationTvView.getText().toString());
                }
                jump(intent, IntentConstant.SELECT_EDUCATION);
                UmengUtils.onEvent(mContext, EventEnum.Study_Experience_Edit_Education);
                break;
            case R.id.tv_right:
                String school = mSchoolEtView.getText().toString().trim();
                String major = mMajorTvView.getText().toString().trim();
                String stime = mSTimeTvView.getText().toString().trim();
                String etime = mETimeTvView.getText().toString().trim();
                String education = mEducationTvView.getText().toString().trim();
                String des = mDesEtView.getText().toString().trim();

                if (TextUtils.isEmpty(school)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.school_cannot_empty);
                    return;
                }
                if (TextUtils.isEmpty(major)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.major_cannot_empty);
                    return;
                }
                if (TextUtils.isEmpty(stime)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.start_time_cannot_empty);
                    return;
                }
                if (TextUtils.isEmpty(education)) {
                    DialogUtils.showShortPromptToast(mContext, R.string.academic_cannot_empty);
                    return;
                }

                String currentTimestamp = DateUtils.getTimestampStr();//当前时间戳
                String currentTime = DateUtils.timestampStrToFormatTime(currentTimestamp, Constant.SELECT_A_DATE_TIME_FORMAT);//yyyy-MM-dd格式的当前时间

                if (!TextUtils.isEmpty(stime) && !TextUtils.equals(stime, getString(R.string.so_far))) {
                    stime = DateUtils.timeStrToTimestampStr(stime, Constant.SELECT_A_DATE_TIME_FORMAT);//取前10位
                } else {
                    DialogUtils.showShortPromptToast(mContext, R.string.stime_not_so_for);
                    return;
                }
                if (!TextUtils.isEmpty(DateUtils.timeStrToTimestampStr(currentTime, Constant.SELECT_A_DATE_TIME_FORMAT)) &&
                        Long.valueOf(stime) > Long.valueOf(DateUtils.timeStrToTimestampStr(currentTime, Constant.SELECT_A_DATE_TIME_FORMAT))) {
                    DialogUtils.showShortPromptToast(mContext, R.string.stime_not_greater_than_current_time);
                    return;
                }
                if (!TextUtils.isEmpty(etime) && !TextUtils.equals(etime, getString(R.string.so_far))) {
                    if (!TextUtils.isEmpty(DateUtils.timeStrToTimestampStr(etime, Constant.SELECT_A_DATE_TIME_FORMAT))) {
                        etime = DateUtils.timeStrToTimestampStr(etime, Constant.SELECT_A_DATE_TIME_FORMAT);
                    }
                    if (Long.valueOf(stime) >= Long.valueOf(etime)) {
                        DialogUtils.showShortPromptToast(mContext, R.string.stime_not_greater_than_etime);
                        return;
                    }
                    if (!TextUtils.isEmpty(DateUtils.timeStrToTimestampStr(currentTime, Constant.SELECT_A_DATE_TIME_FORMAT)) &&
                            Long.valueOf(etime) > Long.valueOf(DateUtils.timeStrToTimestampStr(currentTime, Constant.SELECT_A_DATE_TIME_FORMAT))) {
                        DialogUtils.showShortPromptToast(mContext, R.string.etime_not_greater_than_current_time);
                        return;
                    }
                } else {
                    if (!TextUtils.isEmpty(DateUtils.timeStrToTimestampStr(currentTime, Constant.SELECT_A_DATE_TIME_FORMAT)) &&
                            Long.valueOf(stime) >= Long.valueOf(DateUtils.timeStrToTimestampStr(currentTime, Constant.SELECT_A_DATE_TIME_FORMAT))) {
                        DialogUtils.showShortPromptToast(mContext, R.string.stime_not_greater_than_etime);
                        return;
                    }
                }

                if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT) {
                    startEditStudyExperience(mIDStr, school, major, stime, etime, des, major, education);
                } else {
                    startAddStudyExperience(school, major, stime, etime, des, major, education);
                }
                UmengUtils.onEvent(mContext, EventEnum.Study_Experience_Edit_Save);
                break;
            case R.id.tv_stime:
                mDateTimePickDialogUtils = new DateTimePickDialogUtils(EditEducationExperienceActivity.this, mStartTimeStr);
                mDateTimePickDialogUtils.dateTimePicKDialog(mSTimeTvView);
                UmengUtils.onEvent(mContext, EventEnum.Study_Experience_Edit_Start_Time);
                break;
            case R.id.tv_etime:
                mDateTimePickDialogUtils = new DateTimePickDialogUtils(EditEducationExperienceActivity.this, mEndTimeStr);
                mDateTimePickDialogUtils.dateTimePicKDialog(mETimeTvView);
                UmengUtils.onEvent(mContext, EventEnum.Study_Experience_Edit_End_Time);
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IntentConstant.SELECT_EDUCATION:
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    Bundle bundle = data.getExtras();
                    mJumpFrom = bundle.getInt(IntentConstant.JUMP_ACTIVITY_FLAG, 0);
                    mSelectName = bundle.getString(ChooseMajorActivity.SELECT_NAME, "");
                    mSchoolSelectName = bundle.getString(AddSchoolActivity.SELECT_NAME,"");
                    int noEmptyStr = 0;
                    if (mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD_MAJOR ||
                            mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT_MAJOR) {
                        noEmptyStr = R.string.major_cannot_empty;

                    } else if (mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD_EDUCATION ||
                            mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT_EDUCATION) {
                        noEmptyStr = R.string.academic_cannot_empty;
                    }else if(mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD_SCHOOL ||
                            mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT_SCHOOL){
                        noEmptyStr = R.string.school_cannot_empty;
                    }
                    if (TextUtils.isEmpty(mSelectName)) {
                        DialogUtils.showShortPromptToast(mContext, noEmptyStr);
                        return;
                    }
                    if (TextUtils.isEmpty(mSchoolSelectName)) {
                        DialogUtils.showShortPromptToast(mContext, noEmptyStr);
                        return;
                    }
                    if (mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD_MAJOR ||
                            mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT_MAJOR) {
                        mMajorTvView.setText(mSelectName);

                    } else if (mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD_EDUCATION ||
                            mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT_EDUCATION) {
                        mEducationTvView.setText(mSelectName);
                    }
                    else if (mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD_SCHOOL ||
                            mJumpFrom == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_EDIT_SCHOOL) {
                        mSchoolEtView.setText(mSchoolSelectName);
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startAddStudyExperience(String school, String position, String stime, String etime, String note, String major, String academic) {
        new HttpUtils().configCurrentHttpCacheExpiry(0).send(HttpRequest.HttpMethod.POST, NetConstant.HOST,
                NetConstant.getAddEducationExperienceParams(mContext, school, position, stime, etime, note, major, academic),
                new AddEducationExperienceListener());
    }

    private void startEditStudyExperience(String id, String school, String position, String stime, String etime, String note, String major,
                                          String academic) {
        new HttpUtils().configCurrentHttpCacheExpiry(0).send(HttpRequest.HttpMethod.POST, NetConstant.HOST,
                NetConstant.getEditEducationExperienceParams(mContext, id, school, position, stime, etime, note, major, academic),
                new EditEducationExperienceListener());
    }

    @Override
    protected void attachData() {
        if (mJumpFlag == IntentConstant.JUMP_FROM_WORK_EXPERIENCE_ADD) {
            return;
        }
        mSchoolEtView.setText(mSchoolStr);
        mMajorTvView.setText(mMajorStr);
        mSTimeTvView.setText(mStartTimeStr);
        mETimeTvView.setText(mEndTimeStr);
        mEducationTvView.setText(mAcademicStr);
        mDesEtView.setText(mNoteStr);
    }

    private class EditEducationExperienceListener extends RequestCallBack<String> implements StringToBeanTask.ConvertListener<NormalMessageResponse> {

        @Override
        public void onConvertSuccess(NormalMessageResponse data) {
            if (data == null || data.getError()) {
                DialogUtils.showShortPromptToast(mContext, R.string.edit_study_experience_failed);
                return;
            }
            DialogUtils.showShortPromptToast(mContext, R.string.edit_study_experience_success);
            finish();
        }

        @Override
        public void onConvertFailed(String json) {
            DialogUtils.showShortPromptToast(mContext, R.string.edit_study_experience_failed);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            StringToBeanTask<NormalMessageResponse> task = new StringToBeanTask<>(NormalMessageResponse.class, this);
            task.execute(responseInfo.result);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            DialogUtils.showShortPromptToast(mContext, StringUtils.getString(R.string.edit_study_experience_failed));
        }
    }

    private class AddEducationExperienceListener extends RequestCallBack<String> implements StringToBeanTask.ConvertListener<NormalMessageResponse> {

        @Override
        public void onConvertSuccess(NormalMessageResponse data) {
            if (data == null || data.getError()) {
                if (data != null && data.getMessage() != null) {
                    DialogUtils.showShortPromptToast(mContext, StringUtils.getString(R.string.add_study_experience_failed));
                }
                return;
            }
            DialogUtils.showShortPromptToast(mContext, R.string.add_study_experience_success);
            finish();
        }

        @Override
        public void onConvertFailed(String json) {
            DialogUtils.showShortPromptToast(mContext, R.string.add_study_experience_failed);
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            StringToBeanTask<NormalMessageResponse> task = new StringToBeanTask<>(NormalMessageResponse.class, this);
            task.execute(responseInfo.result);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            DialogUtils.showShortPromptToast(mContext, StringUtils.getString(R.string.add_study_experience_failed));
        }
    }
}
