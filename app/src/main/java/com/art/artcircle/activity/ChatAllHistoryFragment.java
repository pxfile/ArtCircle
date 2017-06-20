package com.art.artcircle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.art.artcircle.R;
import com.art.artcircle.adapter.ChatAllHistoryAdapter;
import com.art.artcircle.application.DemoApplication;
import com.art.artcircle.bean.ArtCircleContactNotifyConversation;
import com.art.artcircle.bean.ContactInfoData;
import com.art.artcircle.bean.ConversationInfo;
import com.art.artcircle.constant.Constant;
import com.art.artcircle.constant.EventEnum;
import com.art.artcircle.constant.IntentConstant;
import com.art.artcircle.constant.NetConstant;
import com.art.artcircle.constant.UmengConstant;
import com.art.artcircle.db.InviteMessageDao;
import com.art.artcircle.db.UserDao;
import com.art.artcircle.fragment.BaseFragment;
import com.art.artcircle.task.StringToBeanTask;
import com.art.artcircle.utils.ActivityUtils;
import com.art.artcircle.utils.DensityUtil;
import com.art.artcircle.utils.SizeUtils;
import com.art.artcircle.utils.StringUtils;
import com.art.artcircle.utils.UmengUtils;
import com.art.artcircle.widget.swipemenulistview.SwipeMenu;
import com.art.artcircle.widget.swipemenulistview.SwipeMenuCreator;
import com.art.artcircle.widget.swipemenulistview.SwipeMenuItem;
import com.art.artcircle.widget.swipemenulistview.SwipeMenuListView;
import com.art.hxmodule.controller.DemoHXSDKHelper;
import com.art.hxmodule.controller.HXSDKHelper;
import com.art.hxmodule.model.domain.User;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 */
public class ChatAllHistoryFragment extends BaseFragment implements View.OnClickListener {
    private InputMethodManager inputMethodManager;
    private SwipeMenuListView listView;
    private ChatAllHistoryAdapter adapter;
    private EditText query;
    private ImageButton clearSearch;
    public RelativeLayout errorItem;

    public TextView errorText;
    private boolean hidden;
    private List<EMConversation> conversationList = new ArrayList<>();
    private User robotUser;
    private String mTestName;

    private Map<String, ContactInfoData.DataEntity> mInfo = new HashMap<>();

    private View view;

    private UserDao mUserDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_history, container, false);
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initFoot() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        view = getView();
        if (view == null)
            return;
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

        conversationList.clear();
        conversationList.addAll(loadConversationsWithRecentChat());

        listView = (SwipeMenuListView) getView().findViewById(R.id.list);
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
        // 设置adapter
        listView.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(R.color.transparent_red_60);
                // set item width
                deleteItem.setWidth(DensityUtil.dip2px(getActivity(), 90));
                // set a icon
//                deleteItem.setIcon(R.drawable.mm_title_remove);
                deleteItem.setTitle(R.string.delete);
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(16);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        final EMConversation tobeDeleteCons = adapter.getItem(position);
                        EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), false);
                        mUserDao.deleteConversation(tobeDeleteCons.getUserName());
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        final String st2 = getResources().getString(R.string.Cant_chat_with_yourself);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                String username = conversation.getUserName();
                if (username.equals(DemoApplication.getInstance().getUserName())) {
                    Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                } else if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
                    //进入申请通知页面
                    UmengUtils.onEvent(getActivity(), EventEnum.New_Friend_Item_Click);
                    User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
                    user.setUnreadMsgCount(0);
                    startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
                } else if (username.equals(Constant.SYSTEM_USERNAME)) {
                    UmengUtils.onEvent(getActivity(), EventEnum.System_Message_Item_Click);
                    mSystemUser.setUnreadMsgCount(0);
                    //进入系统消息页面
                    startActivity(new Intent(getActivity(), SystemMsgListActivity.class));
                } else if (username.equals(Constant.CHAT_ROBOT)) {
                    //进入Robot列表页面
                    UmengUtils.onEvent(getActivity(), EventEnum.Robot_Item_Click);
                    robotUser.setUnreadMsgCount(0);
                    startActivity(new Intent(getActivity(), RobotsActivity.class));
                } else {
                    // 进入聊天页面
                    UmengUtils.onEvent(getActivity(), EventEnum.Chat_Item_Click);
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if (conversation.isGroup()) {
                        if (conversation.getType() == EMConversationType.ChatRoom) {
                            // it is group chat
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_CHATROOM);
                            intent.putExtra("groupId", username);
                        } else {
                            // it is group chat
                            intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                            intent.putExtra("groupId", username);
                        }

                    } else {
                        // it is single chat
                        intent.putExtra("userId", username);
                        if (mInfo.get(username) == null) {
                            intent.putExtra(IntentConstant.CHAT_REAL_NAME, username);
                        } else {
                            intent.putExtra(IntentConstant.CHAT_REAL_NAME, !TextUtils.equals(String.valueOf(mInfo.get(username).getIs_friend()), Constant.IS_FRIEND) ?
                                    (mInfo.get(username).getReal_name()) : StringUtils.getString(mInfo.get(username).getReal_name(), getString(R.string.temporary_session)));
                            String headUrl;
                            ContactInfoData.DataEntity userInfo = mInfo.get(username);
                            headUrl = userInfo.getHeadimg();
                            intent.putExtra(IntentConstant.CHAT_USER_HEAD_IMG, headUrl);
                        }
                    }
                    startActivity(intent);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                UmengUtils.onEvent(getActivity(), EventEnum.Chat_Item_Long_Click);
                final EMConversation tobeDeleteCons = adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.delete).setItems(R.array.deleteArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                //删除会话
                                UmengUtils.onEvent(getActivity(), EventEnum.Delete_Conversation);
                                EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), false);
                                mUserDao.deleteConversation(tobeDeleteCons.getUserName());
                                break;
                            case 1:
                                // 删除会话和消息
                                UmengUtils.onEvent(getActivity(), EventEnum.Delete_Conversation_Message);
                                EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), true);
                                InviteMessageDao inviteMessageDao = new InviteMessageDao(getActivity());
                                inviteMessageDao.deleteMessage(tobeDeleteCons.getUserName());
                                mUserDao.deleteConversation(tobeDeleteCons.getUserName());
                                break;
                        }
                        adapter.remove(tobeDeleteCons);
                        adapter.notifyDataSetChanged();

                        // 更新消息未读数
                        ((MainActivity) getActivity()).updateUnreadLabel();
                    }
                }).setNegativeButton(R.string.cancel, null).show();
                return true;
            }
        });

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                hideSoftKeyboard();
                return false;
            }

        });
        TextView mTvBack = (TextView) getView().findViewById(R.id.tv_back);
        mTvBack.setVisibility(View.GONE);
        TextView mTitle = (TextView) getView().findViewById(R.id.tv_title);
        mTitle.setText(R.string.session);
        TextView mTitleRight = (TextView) getView().findViewById(R.id.tv_right);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.add_icon);
        int size = (int) SizeUtils.dp2Px(getResources(), 24.0f);
        drawable.setBounds(0, 0, size, size);
        mTitleRight.setCompoundDrawables(drawable, null, null, null);
        mTitleRight.setOnClickListener(this);
        // 搜索框
        query = (EditText) getView().findViewById(R.id.query);
        String strSearch = getResources().getString(R.string.search);
        query.setHint(strSearch);
        // 搜索框中清除button
        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

    }

    void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        conversationList.clear();
        conversationList.addAll(loadConversationsWithRecentChat());
        getData();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void getData() {
        new HttpUtils().send(HttpRequest.HttpMethod.GET, NetConstant.HOST, NetConstant.getContactInfoParams(mContext, mTestName), new ContactInfoDataListener());
    }


    private User mSystemUser = new User(Constant.SYSTEM_USERNAME);

    /**
     * 获取所有会话
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        //好友申请与通知
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
        ArtCircleContactNotifyConversation newFriendsConversation = new ArtCircleContactNotifyConversation(user);
        //官方助手
        robotUser = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.CHAT_ROBOT);
        ArtCircleContactNotifyConversation robotConversation = new ArtCircleContactNotifyConversation(robotUser);

        //艺圈消息
        ArtCircleContactNotifyConversation newSystemMsgConversation = new ArtCircleContactNotifyConversation(mSystemUser);
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();

        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<>();
        synchronized (conversations) {
            mTestName = null;
            for (EMConversation conversation : conversations.values()) {
                if (mTestName != null) {
                    mTestName = StringUtils.getString(mTestName, ",", conversation.getUserName());
                } else {
                    mTestName = StringUtils.getString(conversation.getUserName());
                }
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<>();
        list.add(newFriendsConversation);
        list.add(robotConversation);
        list.add(newSystemMsgConversation);

        List<EMConversation> emConversationList = new ArrayList<>();
        Set<EMConversation> emConversationSet = new HashSet<>();

        List<ConversationInfo> mConversationInfoList = mUserDao.getConversationByTopTag();
        if (mConversationInfoList != null && mConversationInfoList.size() > 0) {
            for (int j = 0; j < mConversationInfoList.size(); j++) {
                for (int i = 0; i < sortList.size(); i++) {
                    if (TextUtils.equals(sortList.get(i).second.getUserName(), mConversationInfoList.get(j).getHxUserId())) {
                        emConversationList.add(sortList.get(i).second);
                    }
                }
            }
        }
        for (Pair<Long, EMConversation> sortItem : sortList) {
            emConversationList.add(sortItem.second);
        }
        for (int i = 0; i < emConversationList.size(); i++) {
            if (emConversationSet.add(emConversationList.get(i))) {
                list.add(emConversationList.get(i));
            }
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (TextUtils.equals(String.valueOf(con1.first), String.valueOf(con2.first))) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        UmengUtils.onPageStart(mUMengPagerName);
        super.onResume();
        if (!hidden && !((MainActivity) getActivity()).isConflict) {
            refresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengUtils.onPageEnd(mUMengPagerName);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                UmengUtils.onEvent(getActivity(), EventEnum.Add_New_Chat);
                Intent intent = new Intent(v.getContext(), OnceConnectionRegisteredListActivity.class);
                intent.putExtra(IntentConstant.JUMP_ACTIVITY_FLAG, IntentConstant.FRIEND_LIST);
                ActivityUtils.jump(v.getContext(), intent);
                break;
        }
    }

    @Override
    protected void getArgumentsData() {

    }

    @Override
    protected void initData() {
        mUMengPagerName = UmengConstant.PAGENAME_CHAT_ALL_HISTORY;
        mUserDao = new UserDao(mContext);
    }

    @Override
    protected int setLayoutViewId() {
        return 0;
    }

    @Override
    protected void initContent() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void attachData() {

    }


    /**
     * 提示官方助手有新消息
     */
    public void onRobotNewMessage() {
        if (robotUser == null) {
            robotUser = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.CHAT_ROBOT);
        }
        robotUser.setUnreadMsgCount(1);
        refresh();
    }

    /**
     * 提示官方助手有新消息
     */
    public void onNewSystemInvatedMessage() {
        if (mSystemUser == null) {
            mSystemUser = new User(Constant.SYSTEM_USERNAME);
        }
        mSystemUser.setUnreadMsgCount(1);
        refresh();
    }

    private class ContactInfoDataListener extends RequestCallBack<String> implements StringToBeanTask.ConvertListener<ContactInfoData> {

        @Override
        public void onFailure(HttpException e, String s) {
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            StringToBeanTask<ContactInfoData> task = new StringToBeanTask<>(ContactInfoData.class, this);
            task.execute(responseInfo.result);
        }

        @Override
        public void onConvertFailed(String json) {
        }

        @Override
        public void onConvertSuccess(ContactInfoData response) {
            if (!response.getError()) {
                if (response.getData() != null && response.getData().size() > 0) {
                    for (ContactInfoData.DataEntity dataEntity : response.getData()) {
                        mInfo.put(dataEntity.getUser_id(), dataEntity);
                    }
                }
                adapter.setmReturnInfo(mInfo);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
