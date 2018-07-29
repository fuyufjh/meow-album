package me.ericfu.album.model;

import java.io.Serializable;

public class Album implements Serializable {

    private static final long serialVersionUID = -4895316054056220016L;

    private int id;
    private int ownerId;
    private String title;
    private String alias;
    private boolean public_;

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isPublic() {
        return public_;
    }

    public void setPublic(boolean public_) {
        this.public_ = public_;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", title='" + title + '\'' +
                ", alias='" + alias + '\'' +
                ", public_=" + public_ +
                '}';
    }
}
