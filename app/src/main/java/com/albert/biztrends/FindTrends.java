package com.albert.biztrends;

public class FindTrends {
    //same id as in database
    public String img, fullname, status;

    public FindTrends(){

    }

    public FindTrends(String img, String fullname, String status) {
        this.img = img;
        this.fullname = fullname;
        this.status = status;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
