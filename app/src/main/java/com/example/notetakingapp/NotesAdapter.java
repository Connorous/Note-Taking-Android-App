package com.example.notetakingapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetakingapp.databinding.ItemContainerNoteBinding;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<NoteContents> notes ;

    private RemoveNoteListener removeNoteListener;

    private EditTextListener editTextListener;

    public NotesAdapter(List<NoteContents> notes, RemoveNoteListener removeNoteListener, EditTextListener editTextListener) {
        this.notes = notes;
        this.removeNoteListener = removeNoteListener;
        this.editTextListener = editTextListener;
    }

    @NonNull
    @Override
    public NotesAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerNoteBinding itemContainerNoteBinding = ItemContainerNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotesAdapter.NotesViewHolder(itemContainerNoteBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesViewHolder holder, int position) {
        holder.setData(notes.get(position), position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder {

        private ItemContainerNoteBinding itemContainerNoteBinding;
        public NotesViewHolder(@NonNull ItemContainerNoteBinding itemContainerNoteBinding) {
            super(itemContainerNoteBinding.getRoot());
            this.itemContainerNoteBinding = itemContainerNoteBinding;
        }

        public void setData(NoteContents noteContents, int position) {
            //Collections.sort(notes, (obj1, obj2) -> obj1.getDateCreated().compareTo(obj2.getDateCreated()));
            itemContainerNoteBinding.notes.setText(noteContents.getNote());
            itemContainerNoteBinding.dateCreated.setText(new SimpleDateFormat("MMMM dd, yyyy - hh : mm a", Locale.getDefault()).format(noteContents.getDateCreated()));
            itemContainerNoteBinding.deleteButton.setOnClickListener(v -> removeNoteListener.onDeleteNoteClicked(position));
            itemContainerNoteBinding.notes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (itemContainerNoteBinding.notes.hasFocus()) {
                        editTextListener.onTextEdited(itemContainerNoteBinding.notes.getText().toString(), position, itemContainerNoteBinding.notes);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }
    }
}
