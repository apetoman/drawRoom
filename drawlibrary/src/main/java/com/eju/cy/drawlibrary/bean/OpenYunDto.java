package com.eju.cy.drawlibrary.bean;

public class OpenYunDto {

    /**
     * token : d6e16e3162f803ef7b4eb6892aa737f265e1c0a2
     * user_id : 1869
     */

    private String token;
    private int user_id;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }


    @Override
    public String toString() {
        return "OpenYunDto{" +
                "token='" + token + '\'' +
                ", user_id=" + user_id +
                '}';
    }
}
