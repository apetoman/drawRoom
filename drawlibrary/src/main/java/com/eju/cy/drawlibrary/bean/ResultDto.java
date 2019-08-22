package com.eju.cy.drawlibrary.bean;

/**
 * Created by Cc on 2018/5/5.
 */

public class ResultDto<T> {
    /**
     * msg : 成功
     * data : {"token":"c76a2b449e1296c34af0d9a18b3e3e05a2d7c064","expires":1505463060,"user_id":328}
     * code : 0
     * encrypt : 0
     */

    private String msg;
    private T data;
    private int code;
    private int encrypt;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    public boolean isOk(){
        return getCode()==0||getCode()==10000?true:false;
    }




}
