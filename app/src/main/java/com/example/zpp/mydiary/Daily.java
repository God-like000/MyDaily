package com.example.zpp.mydiary;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.security.PublicKey;

public class Daily {
    private Integer id;
    private String title;
    private String createtime;
    private String content;
    private String man;
    private byte[] photos;
    public Daily(){

    }

    public Daily(Integer id,String title,String createtime,String content,byte[] photos){
        this.id=id;
        this.title=title;
        this.createtime=createtime;
        this.content=content;
        this.man=man;
        this.photos=photos;
    }

    public Integer getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getCreatetime() {
        return createtime;
    }

    public String getTitle() {
        return title;
    }

    public String getMan(){ return man; }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMan(String man) { this.man = man; }

    public byte[] getPhotos() {
        return photos;
    }

    public void setPhotos(byte[] photos) {
        this.photos = photos;
    }
}
