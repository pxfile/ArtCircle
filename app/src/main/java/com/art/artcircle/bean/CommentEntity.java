package com.art.artcircle.bean;

public class CommentEntity {
    /**
     * obj_id : 1
     * user_id : 1
     * father_name :
     * user_name :
     * real_name : 郭红
     * id : 1
     * father_id : 0
     * content : xxxx
     */
    private String obj_id;
    private String user_id;
    private String father_name;
    private String user_name;
    private String real_name;
    private String id;
    private String father_id;
    private String content;

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFather_id(String father_id) {
        this.father_id = father_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getObj_id() {
        return obj_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFather_name() {
        return father_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getReal_name() {
        return real_name;
    }

    public String getId() {
        return id;
    }

    public String getFather_id() {
        return father_id;
    }

    public String getContent() {
        return content;
    }
}
