package com.example.notetakingapp;

import android.widget.EditText;

public interface EditTextListener {
    void onTextEdited(String text, int position, EditText note);
}
