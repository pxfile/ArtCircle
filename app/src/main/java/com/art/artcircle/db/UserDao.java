/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.art.artcircle.db;

import android.content.Context;

import com.art.artcircle.bean.ContactResponse;
import com.art.artcircle.bean.ConversationInfo;
import com.art.artcircle.bean.SchoolListBeanResponse;
import com.art.hxmodule.model.domain.RobotUser;
import com.art.hxmodule.model.domain.User;

import java.util.List;
import java.util.Map;

public class UserDao {
    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    public static final String PREF_TABLE_NAME = "pref";
    public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public static final String ROBOT_TABLE_NAME = "robots";
    public static final String ROBOT_COLUMN_NAME_ID = "username";
    public static final String ROBOT_COLUMN_NAME_NICK = "nick";
    public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";

    public static final String CONTACT_TABLE_NAME = "contacts";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String HONOR = "honor";
    public static final String REAL_NAME = "real_name";
    public static final String HEADING = "heading";
    public static final String MOBILE = "mobile";
    public static final String CERTIFICATE = "certificate";
    public static final String RIGHT_NAME = "right_name";
    public static final String FILTER_STR = "filter_str";

    public static final String CONVERSATION_TABLE_NAME = "conversations";
    public static final String CONVERSATION_ID = "conversation_id";
    public static final String CONVERSATION_COLUMN_NAME_AVATAR = "avatar";
    public static final String CONVERSATION_REAL_NAME = "real_name";
    public static final String CONVERSATION_MESSAGE = "message";
    public static final String HX_USERID = "hx_userid";
    public static final String IDS_FRIEND = "IDS_FRIEND";
    public static final String CONVERSATION_HONOR = "con_honor";
    public static final String CONVERSATION_USERNAME = "user_name";
    public static final String CONVERSATION_CERTIFICATE = "con_certificate";
    public static final String CONVERSATION_MOBILE = "con_mobile";
    public static final String CONVERSATION_TIME = "con_time";
    public static final String CONVERSATION_TOP_TAG = "con_top_tag";
    public static final String CONVERSATION_TOP_TIME = "con_top_time";

    public static final String HX_USERID_INDEX = "hx_userid_index";

    public static final String SCHOOLLIST_TABLE_NAME = "schoollist";
    public static final String SCHOOLLIST_ID = "schoollist_id";
    public static final String SCHOOLLIST_NAME = "schoollist_name";
    public static final String SCHOOLLIST_LETTER = "schoollist_letter";

    public UserDao(Context context) {
        DemoDBManager.getInstance().onInit(context);
    }

    /**
     * 保存好友list
     *
     * @param contactList
     */
    public void saveContactList(List<User> contactList) {
        DemoDBManager.getInstance().saveContactList(contactList);
    }

    /**
     * 获取好友list
     *
     * @return
     */
    public Map<String, User> getContactList() {

        return DemoDBManager.getInstance().getContactList();
    }

    /**
     * 删除一个联系人
     *
     * @param username
     */
    public void deleteContact(String username) {
        DemoDBManager.getInstance().deleteContact(username);
    }

    /**
     * 保存一个联系人
     *
     * @param user
     */
    public void saveContact(User user) {
        DemoDBManager.getInstance().saveContact(user);
    }

    public void setDisabledGroups(List<String> groups) {
        DemoDBManager.getInstance().setDisabledGroups(groups);
    }

    public List<String> getDisabledGroups() {
        return DemoDBManager.getInstance().getDisabledGroups();
    }

    public void setDisabledIds(List<String> ids) {
        DemoDBManager.getInstance().setDisabledIds(ids);
    }

    public List<String> getDisabledIds() {
        return DemoDBManager.getInstance().getDisabledIds();
    }

    public Map<String, RobotUser> getRobotUser() {
        return DemoDBManager.getInstance().getRobotList();
    }

    public void saveRobotUser(List<RobotUser> robotList) {
        DemoDBManager.getInstance().saveRobotList(robotList);
    }

    /**
     * 保存学校列表
     *
     * @param schoolList
     */
    public void saveSchoolList(List<SchoolListBeanResponse.DataEntity> schoolList) {
        DemoDBManager.getInstance().saveSchoolList(schoolList);
    }

    /**
     * 获取学校列表
     */
    public List<SchoolListBeanResponse.DataEntity> getSchoolList() {
        return DemoDBManager.getInstance().getSchoolList();
    }

    /**
     * 获取所有历史搜索的人脉
     *
     * @return
     */
    public List<ContactResponse.DataEntity.ListEntity> getSearchRecordList() {
        return DemoDBManager.getInstance().getSearchRecordList();
    }

    /**
     * 保存一个人脉
     *
     * @param user
     */
    public long saveSearchRecord(ContactResponse.DataEntity.ListEntity user) {
        return DemoDBManager.getInstance().saveSearchRecord(user);
    }

    /**
     * 通过表名删除表中的所有数据
     */
    public void deleteDBDataByName(String dbName) {
        DemoDBManager.getInstance().deleteDBDataByName(dbName);
    }

    /**
     * 保存对话人列表list
     *
     * @param conversationinfo
     */
    public void saveConversationList(ConversationInfo conversationinfo) {
        DemoDBManager.getInstance().saveConversationList(conversationinfo);

    }

    public ConversationInfo getConversation(String username) {
        return DemoDBManager.getInstance().getConversation(username);
    }

    public List<ConversationInfo> getLastThreeConversations() {
        return DemoDBManager.getInstance().getLastThreeConversations();
    }

    public List<ConversationInfo> getConversationByTopTag() {
        return DemoDBManager.getInstance().getConversationsByTopTag();
    }

    public List<ConversationInfo> getLastThreeConversationByTopTag() {
        return DemoDBManager.getInstance().getLastThreeConversationsByTopTag();
    }

    public void updateConversation(String username, ConversationInfo conversationInfo) {
        DemoDBManager.getInstance().updateConversation(username, conversationInfo);
    }

    public void updateConversationByTopTag(String username,String topTag, String topTime) {
        DemoDBManager.getInstance().updateConversationByTopTag(username,topTag, topTime);
    }

    public int deleteConversation(String username) {
        return DemoDBManager.getInstance().deleteConversation(username);
    }
}
