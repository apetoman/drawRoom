package com.eju.cy.drawlibrary.bean;

import java.util.List;

public class MyRoomData {


    /**
     * msg : 成功
     * data : {"count":3,"total_count":71,"start_index":0,"records":[{"state":0,"intro":"","layout":3,"name":"梧桐城邦（公寓）3室2厅","modify_time":1535514151,"no":"638456aad5b211e79d6e0800276e1046","area":70.57,"id":27765,"preview_url":"https://img-test.jiandanhome.com/deco/1808/29/8ec998dcab3d11e8b41dfa163edde52c.jpg"},{"state":0,"intro":"","layout":1,"name":"田林七村1室1厅123","modify_time":1535513922,"no":"311da8402b5e11e8acc50800276e1046","area":32.86,"id":27988,"preview_url":"https://img-test.jiandanhome.com/deco/1808/29/067ad8e2ab3d11e8b41dfa163edde52c.jpg"},{"state":0,"intro":"","layout":3,"name":"怡田小区3室2厅","modify_time":1534501588,"no":"4257ab6e8f1411e8b4bf0800276e1046","area":84.69,"id":28147,"preview_url":"https://img-test.jiandanhome.com/deco/1808/14/53276c4c9fa211e88ffb0800276e1046.jpg"}]}
     * code : 10000
     * encrypt : 0
     */

    private String msg;
    private DataBean data;
    private String code;
    private int encrypt;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    public static class DataBean {
        /**
         * count : 3
         * total_count : 71
         * start_index : 0
         * records : [{"state":0,"intro":"","layout":3,"name":"梧桐城邦（公寓）3室2厅","modify_time":1535514151,"no":"638456aad5b211e79d6e0800276e1046","area":70.57,"id":27765,"preview_url":"https://img-test.jiandanhome.com/deco/1808/29/8ec998dcab3d11e8b41dfa163edde52c.jpg"},{"state":0,"intro":"","layout":1,"name":"田林七村1室1厅123","modify_time":1535513922,"no":"311da8402b5e11e8acc50800276e1046","area":32.86,"id":27988,"preview_url":"https://img-test.jiandanhome.com/deco/1808/29/067ad8e2ab3d11e8b41dfa163edde52c.jpg"},{"state":0,"intro":"","layout":3,"name":"怡田小区3室2厅","modify_time":1534501588,"no":"4257ab6e8f1411e8b4bf0800276e1046","area":84.69,"id":28147,"preview_url":"https://img-test.jiandanhome.com/deco/1808/14/53276c4c9fa211e88ffb0800276e1046.jpg"}]
         */

        private int count;
        private int total_count;
        private int start_index;
        private List<RecordsBean> records;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

        public int getStart_index() {
            return start_index;
        }

        public void setStart_index(int start_index) {
            this.start_index = start_index;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public static class RecordsBean {
            /**
             * state : 0
             * intro :
             * layout : 3
             * name : 梧桐城邦（公寓）3室2厅
             * modify_time : 1535514151
             * no : 638456aad5b211e79d6e0800276e1046
             * area : 70.57
             * id : 27765
             * preview_url : https://img-test.jiandanhome.com/deco/1808/29/8ec998dcab3d11e8b41dfa163edde52c.jpg
             */

            private int state;
            private String intro;
            private int layout;
            private String name;
            private Long modify_time;
            private String no;
            private double area;
            private int id;
            private String preview_url;

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }

            public String getIntro() {
                return intro;
            }

            public void setIntro(String intro) {
                this.intro = intro;
            }

            public int getLayout() {
                return layout;
            }

            public void setLayout(int layout) {
                this.layout = layout;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Long getModify_time() {
                return modify_time;
            }

            public void setModify_time(Long modify_time) {
                this.modify_time = modify_time;
            }

            public String getNo() {
                return no;
            }

            public void setNo(String no) {
                this.no = no;
            }

            public double getArea() {
                return area;
            }

            public void setArea(double area) {
                this.area = area;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getPreview_url() {
                return preview_url;
            }

            public void setPreview_url(String preview_url) {
                this.preview_url = preview_url;
            }
        }
    }
}
