package com.smile.thirdpartylogin;

/**
 * author: smile .
 * date: On 2018/9/8
 */
public class Login {

    private String name;
    private int loginImage;

    public Login(String name, int loginImage) {
        this.name = name;
        this.loginImage = loginImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoginImage() {
        return loginImage;
    }

    public void setLoginImage(int loginImage) {
        this.loginImage = loginImage;
    }
}
