package com.example.MadWeek2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

public class Image_Download_Info implements Serializable{
    private String _id;
    private String salt;
    private String originalName;
    private String base64;

    public String get_id(){
        return _id;
    }

    public void set_id(String _id){
        this._id = _id;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getBase64(){
        return base64;
    }

    public void setBase64(String base64){
        this.base64 = base64;
    }
}
