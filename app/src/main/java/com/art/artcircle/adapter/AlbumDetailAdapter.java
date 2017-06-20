package com.art.artcircle.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.art.artcircle.R;
import com.art.artcircle.activity.PhotoGalleryActivity;
import com.art.artcircle.bean.AlbumDetailResponse;
import com.art.artcircle.constant.Constant;
import com.art.artcircle.constant.EventEnum;
import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.utils.UmengUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import static com.art.artcircle.utils.ActivityUtils.jump;

/**
 * Created by Fang on 2015/8/26.
 */
public class AlbumDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AlbumDetailResponse.DataEntity.ListEntity> mData;
    private Context mContext;
    private ArrayList<String> mAlbumPaths = new ArrayList<>();
    private ArrayList<String> mAlbumNames = new ArrayList<>();
    private String mAlbumNotes;

    public AlbumDetailAdapter(Context context) {
        this.mContext = context;
        mData = new ArrayList<>();
    }

    public void resetData(List<AlbumDetailResponse.DataEntity.ListEntity> list, String note) {
        if (list != null) {
            mAlbumPaths.clear();
            mAlbumNames.clear();
            mData.clear();
            mData.addAll(list);
            notifyDataSetChanged();
            mAlbumNotes = note;
            for (AlbumDetailResponse.DataEntity.ListEntity listEntity : list) {
                mAlbumPaths.add(listEntity.getPhoto().getImg());
                mAlbumNames.add(listEntity.getName());
            }
        }
    }

    public void addData(List<AlbumDetailResponse.DataEntity.ListEntity> list, String note) {
        if (list != null) {
            mData.addAll(list);
            notifyDataSetChanged();
            mAlbumNotes = note;
            for (AlbumDetailResponse.DataEntity.ListEntity listEntity : list) {
                mAlbumPaths.add(listEntity.getPhoto().getImg());
                mAlbumNames.add(listEntity.getName());
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentHolder(View.inflate(parent.getContext(), R.layout.row_album_detail, null));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ContentHolder vh = (ContentHolder) holder;
        if (TextUtils.equals(Constant.IS_COVER, mData.get(position).getCover())) {
            vh.mCoverView.setVisibility(View.VISIBLE);
        } else {
            vh.mCoverView.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.get(position).getName())) {
            vh.mNameTvView.setText(mData.get(position).getName());
        }
        if (!TextUtils.isEmpty(mData.get(position).getPhoto().getImg())) {
            Uri uri = Uri.parse(mData.get(position).getPhoto().getImg());
            vh.mAlbumImgView.setImageURI(uri);
        }
        vh.mAlbumRLView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoGalleryActivity.class);
                intent.putExtra(IntentConstant.JUMP_ACTIVITY_FLAG, IntentConstant.JUMP_FORM_ALBUM_DETAIL_ADAPTER);
                intent.putExtra(Constant.ALBUM_PATH, mAlbumPaths);
                intent.putExtra(Constant.ALBUM_NAME, mAlbumNames);
                intent.putExtra(Constant.ALBUM_NOTE, mAlbumNotes);
                intent.putExtra(Constant.ALBUM_POSITION, position);
                jump(mContext, intent);
                UmengUtils.onEvent(mContext, EventEnum.Album_Detail_Select);
            }
        });
        vh.mAlbumRLBgView.setBackgroundResource(R.color.white);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private class ContentHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mAlbumRLView;
        private TextView mNameTvView;
        private SimpleDraweeView mAlbumImgView;
        private TextView mCoverView;
        private RelativeLayout mAlbumRLBgView;


        public ContentHolder(View itemView) {
            super(itemView);
            mAlbumRLView = (RelativeLayout) itemView.findViewById(R.id.rl_album_row);
            mNameTvView = (TextView) itemView.findViewById(R.id.tv_album_name);
            mAlbumImgView = (SimpleDraweeView) itemView.findViewById(R.id.img_album);
            mCoverView = (TextView) itemView.findViewById(R.id.tv_album_cover);
            mAlbumRLBgView = (RelativeLayout) itemView.findViewById(R.id.rl_album);
        }
    }
}

