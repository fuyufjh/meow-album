package me.ericfu.atchannel.model;

import java.io.Serializable;
import java.util.Date;

public class Photo implements Serializable {

    private static final long serialVersionUID = -8553034150470792610L;

    private int id;
    private int ownerId;
    private String title;
    private String text;
    private String previewUrl;
    private String rawUrl;
    private Date photoTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getRawUrl() {
        return rawUrl;
    }

    public void setRawUrl(String rawUrl) {
        this.rawUrl = rawUrl;
    }

    public Date getPhotoTime() {
        return photoTime;
    }

    public void setPhotoTime(Date photoTime) {
        this.photoTime = photoTime;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", rawUrl='" + rawUrl + '\'' +
                ", photoTime=" + photoTime +
                '}';
    }
}
