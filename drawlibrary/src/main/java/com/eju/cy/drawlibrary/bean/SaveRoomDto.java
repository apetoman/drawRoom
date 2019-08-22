package com.eju.cy.drawlibrary.bean;

public class SaveRoomDto {


    /**
     * msg : 成功
     * data : {"no":"0ac3ab083ca311e8aaa5fa163edde52c"}
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
         * no : 0ac3ab083ca311e8aaa5fa163edde52c
         */

        private String no;

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }
    }
}
