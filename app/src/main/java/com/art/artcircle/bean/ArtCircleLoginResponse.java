package com.art.artcircle.bean;

/**
 * Created by kakaxicm on 2015/8/20.
 */
public class ArtCircleLoginResponse {

    /**
     * error : false
     * data : {"user_id":"255","user_name":"jinqi_255","real_name":"大方","headimg":"http://c2c.ig365.cn/data/files/old_shop/gh_255/store_255/art/20151105/201511051740473638.png@400w_400h","mobile":"15102273220","token":"94b4PNk7x1bBZoVQ0O%2F838i1%2BSPRbbn4aJImC55M2qPN%2F6JL%2FsndKIA8","major":"12,13,14","honor":"","sex":"0","ranking":1,"influence":"10","position":"总裁","certificate":"0","background":"http://c2c.ig365.cn/data/files/old_shop/gh_0/store_0/jianbao/20151106/201511061058356847.jpg@400w_400h","fans":"0","is_new_fans":0,"on_fans":"4","photo":"88","type":12,"step":"3","perfect":100}
     * message :
     */

    private boolean error;
    /**
     * user_id : 255
     * user_name : jinqi_255
     * real_name : 大方
     * headimg : http://c2c.ig365.cn/data/files/old_shop/gh_255/store_255/art/20151105/201511051740473638.png@400w_400h
     * mobile : 15102273220
     * token : 94b4PNk7x1bBZoVQ0O%2F838i1%2BSPRbbn4aJImC55M2qPN%2F6JL%2FsndKIA8
     * major : 12,13,14
     * honor :
     * sex : 0
     * ranking : 1
     * influence : 10
     * position : 总裁
     * certificate : 0
     * background : http://c2c.ig365.cn/data/files/old_shop/gh_0/store_0/jianbao/20151106/201511061058356847.jpg@400w_400h
     * fans : 0
     * is_new_fans : 0
     * on_fans : 4
     * photo : 88
     * type : 12
     * step : 3
     * perfect : 100
     */

    private DataEntity data;
    private String message;

    public void setError(boolean error) {
        this.error = error;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public DataEntity getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public static class DataEntity {
        private String user_id;
        private String user_name;
        private String real_name;
        private String headimg;
        private String mobile;
        private String token;
        private String major;
        private String honor;
        private String sex;
        private int ranking;
        private String influence;
        private String position;
        private String certificate;
        private String background;
        private String fans;
        private int is_new_fans;
        private String on_fans;
        private String photo;
        private int type;
        private String step;
        private int perfect;
        private String two_message_receive;
        private String two_friend_receive;

        public void setTwo_message_receive(String two_message_receive) {
            this.two_message_receive = two_message_receive;
        }

        public void setTwo_friend_receive(String two_friend_receive) {
            this.two_friend_receive = two_friend_receive;
        }

        public String getTwo_message_receive() {
            return two_message_receive;
        }

        public String getTwo_friend_receive() {
            return two_friend_receive;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public void setHonor(String honor) {
            this.honor = honor;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }

        public void setInfluence(String influence) {
            this.influence = influence;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public void setCertificate(String certificate) {
            this.certificate = certificate;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public void setFans(String fans) {
            this.fans = fans;
        }

        public void setIs_new_fans(int is_new_fans) {
            this.is_new_fans = is_new_fans;
        }

        public void setOn_fans(String on_fans) {
            this.on_fans = on_fans;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setStep(String step) {
            this.step = step;
        }

        public void setPerfect(int perfect) {
            this.perfect = perfect;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public String getReal_name() {
            return real_name;
        }

        public String getHeadimg() {
            return headimg;
        }

        public String getMobile() {
            return mobile;
        }

        public String getToken() {
            return token;
        }

        public String getMajor() {
            return major;
        }

        public String getHonor() {
            return honor;
        }

        public String getSex() {
            return sex;
        }

        public int getRanking() {
            return ranking;
        }

        public String getInfluence() {
            return influence;
        }

        public String getPosition() {
            return position;
        }

        public String getCertificate() {
            return certificate;
        }

        public String getBackground() {
            return background;
        }

        public String getFans() {
            return fans;
        }

        public int getIs_new_fans() {
            return is_new_fans;
        }

        public String getOn_fans() {
            return on_fans;
        }

        public String getPhoto() {
            return photo;
        }

        public int getType() {
            return type;
        }

        public String getStep() {
            return step;
        }

        public int getPerfect() {
            return perfect;
        }
    }
}
