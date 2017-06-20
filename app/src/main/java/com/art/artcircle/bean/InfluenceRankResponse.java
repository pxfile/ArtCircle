package com.art.artcircle.bean;

import java.util.List;

/**
 * Created by Fang on 2015/9/12.
 */
public class InfluenceRankResponse extends BaseResponse{


    /**
     * error : false
     * data : {"my":1,"list":[{"user_id":"134","user_name":"Star","real_name":"胡永胜","honor":"","headimg":"http://c2c.ig365.cn/data/files/old_shop/gh_134/store_134/art/20150914/201509141856547513.png@400w_400h","mobile":"","influence":"0","certificate":"0","id":"595","ranking":1}],"count":"6"}
     * message :
     */

    private DataEntity data;


    public void setData(DataEntity data) {
        this.data = data;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        /**
         * my : 1
         * list : [{"user_id":"134","user_name":"Star","real_name":"胡永胜","honor":"","headimg":"http://c2c.ig365.cn/data/files/old_shop/gh_134/store_134/art/20150914/201509141856547513.png@400w_400h","mobile":"","influence":"0","certificate":"0","id":"595","ranking":1}]
         * count : 6
         */

        private int my;
        private String count;
        private List<ListEntity> list;

        public void setMy(int my) {
            this.my = my;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public void setList(List<ListEntity> list) {
            this.list = list;
        }

        public int getMy() {
            return my;
        }

        public String getCount() {
            return count;
        }

        public List<ListEntity> getList() {
            return list;
        }

        public static class ListEntity {
            /**
             * user_id : 134
             * user_name : Star
             * real_name : 胡永胜
             * honor :
             * headimg : http://c2c.ig365.cn/data/files/old_shop/gh_134/store_134/art/20150914/201509141856547513.png@400w_400h
             * mobile :
             * influence : 0
             * certificate : 0
             * id : 595
             * ranking : 1
             */

            private String user_id;
            private String user_name;
            private String real_name;
            private String honor;
            private String headimg;
            private String mobile;
            private String influence;
            private String certificate;
            private String id;
            private int ranking;

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public void setUser_name(String user_name) {
                this.user_name = user_name;
            }

            public void setReal_name(String real_name) {
                this.real_name = real_name;
            }

            public void setHonor(String honor) {
                this.honor = honor;
            }

            public void setHeadimg(String headimg) {
                this.headimg = headimg;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public void setInfluence(String influence) {
                this.influence = influence;
            }

            public void setCertificate(String certificate) {
                this.certificate = certificate;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setRanking(int ranking) {
                this.ranking = ranking;
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

            public String getHonor() {
                return honor;
            }

            public String getHeadimg() {
                return headimg;
            }

            public String getMobile() {
                return mobile;
            }

            public String getInfluence() {
                return influence;
            }

            public String getCertificate() {
                return certificate;
            }

            public String getId() {
                return id;
            }

            public int getRanking() {
                return ranking;
            }
        }
    }
}
