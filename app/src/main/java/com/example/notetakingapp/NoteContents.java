package com.example.notetakingapp;

import java.io.Serializable;
import java.util.Date;

public class NoteContents implements Serializable {
    private String note;
    private Date dateCreated;

    public String getNote() {
        return note;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
