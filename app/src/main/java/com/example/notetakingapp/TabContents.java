package com.example.notetakingapp;

import java.io.Serializable;
import java.util.Date;

public class TabContents implements Serializable {

    private String tabName;
    private Date dateCreated;

    public String getTabName() {
        return tabName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
