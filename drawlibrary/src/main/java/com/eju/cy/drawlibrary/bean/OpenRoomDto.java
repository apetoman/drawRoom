package com.eju.cy.drawlibrary.bean;


public class OpenRoomDto {
    private  String no;
    private  String data;

    public Boolean getAddOrUpdata() {
        return addOrUpdata;
    }

    public void setAddOrUpdata(Boolean addOrUpdata) {
        this.addOrUpdata = addOrUpdata;
    }

    private  Boolean addOrUpdata = false;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OpenRoomDto{" +
                "no='" + no + '\'' +
                ", data='" + data + '\'' +
                ", addOrUpdata=" + addOrUpdata +
                '}';
    }
}
