package com.example.notetakingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notetakingapp.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements TabListener, RemoveNoteListener, RemoveTabListener, EditTextListener {

    private ActivityMainBinding binding;

    private TabsAdapter tabsAdapter;

    private NotesAdapter notesAdapter;

    private List<TabContents> tabs;

    private HashMap<String, List<NoteContents>> noteArrays;

    private List<NoteContents> currentNotes;

    private String currentTabName;

    private boolean alreadyEditing = false;

    private int editCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        tabs = new ArrayList<>();
        noteArrays = new HashMap<>();
        loadTabs();
        loadNotes();
        tabsAdapter = new TabsAdapter(tabs, this, this);
        if (tabs.size() > 0) {
            currentTabName = String.valueOf(tabs.get(0));
            currentNotes = noteArrays.get(currentTabName);
        }
        else {
            currentNotes = null;
            currentTabName = null;
        }
        List<NoteContents> emptyNotesArray = new ArrayList<>();
        notesAdapter = new NotesAdapter(emptyNotesArray, this, this);
        binding.notesRecyclerView.setAdapter(notesAdapter);
        binding.tabsRecyclerView.setAdapter(tabsAdapter);
        binding.tabsProgressBar.setVisibility(View.GONE);
        binding.notesProgressBar.setVisibility(View.GONE);
        Collections.sort(tabs, (obj1, obj2) -> obj1.getDateCreated().compareTo(obj2.getDateCreated()));
        tabsAdapter.notifyDataSetChanged();
        notesAdapter.notifyDataSetChanged();
        binding.tabsRecyclerView.smoothScrollToPosition(0);
        binding.notesRecyclerView.smoothScrollToPosition(0);
        binding.tabsRecyclerView.setVisibility(View.VISIBLE);
        binding.notesRecyclerView.setVisibility(View.VISIBLE);

        setListeners();

        setContentView(binding.getRoot());
    }

    @Override
    public void onTabClicked(TabContents tabContents) {
        currentTabName = tabContents.getTabName();
        currentNotes = noteArrays.get(currentTabName);
        notesAdapter = new NotesAdapter(currentNotes, this, this);
        binding.notesRecyclerView.setAdapter(notesAdapter);
        //Collections.sort(currentNotes, (obj1, obj2) -> obj2.getDateCreated().compareTo(obj1.getDateCreated()));
        notesAdapter.notifyDataSetChanged();
        binding.notesRecyclerView.smoothScrollToPosition(0);
        binding.notesTitle.setText( "Notes for " + currentTabName);
    }

    @Override
    public void onDeleteNoteClicked(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("OK", (dialog, listenerInterface) -> {
                            currentNotes.remove(position);
                            notesAdapter.notifyDataSetChanged();})
                .setNegativeButton("Cancel", (dialog, listenerInterface) -> dialog.dismiss())
                .create()
                .show();
        saveNotes();
    }

    private void setListeners() {
        binding.addNoteButton.setOnClickListener(v -> addNote());
        binding.addTabButton.setOnClickListener(v -> addTab());
        binding.hideTabsButton.setOnClickListener(v -> toggleTabsView());
    }


    private void addNote() {
        if (currentNotes == null) {
            showToast("Please selected a notes group before making a note");
            return;
        }

        if (binding.noteInputField.getText().toString().equals("")) {
            showToast("Please type a message");
            return;
        }

        //binding.noteInputField.requestFocus();
        NoteContents newNote = new NoteContents();
        newNote.setNote(binding.noteInputField.getText().toString());
        newNote.setDateCreated(new Date());
        currentNotes.add(newNote);
        notesAdapter.notifyDataSetChanged();
        binding.notesRecyclerView.smoothScrollToPosition(0);
        binding.noteInputField.setText("");
        saveNotes();
        //for (int i = 0; i < currentNotes.size(); i++) {
            //System.out.println("Note " + i + " is " + currentNotes.get(i).getNote());
        //}
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void addTab() {
        AlertDialog.Builder addTabDialog = new AlertDialog.Builder(MainActivity.this);
        addTabDialog.setTitle("Add Note Group");

        EditText tabNameInput = new EditText(MainActivity.this);
        tabNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        addTabDialog.setView(tabNameInput);

        addTabDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newTabName = tabNameInput.getText().toString();

                for (int j = 0; j < tabs.size(); j++) {
                    if (tabs.get(j).getTabName().equals(newTabName)) {
                        showToast("There is already a notes group with that name");
                        showToast("Please use a different name for each notes group");
                        return;
                    }
                }
                if (newTabName.equals("")) {
                    showToast("Please enter a note group name");
                }
                else {
                    TabContents newTab = new TabContents();
                    newTab.setTabName(newTabName);
                    newTab.setDateCreated(new Date());
                    List<NoteContents> newTabNotes = new ArrayList<>();
                    tabs.add(newTab);
                    noteArrays.put(newTabName, newTabNotes);
                    onTabClicked(newTab);
                    saveTabs();
                    saveNotes();
                }
            }
        });
        addTabDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        addTabDialog.show();
    }

    @Override
    public void onTabRemoveClicked(TabContents tabContents) {

        new AlertDialog.Builder(this)
                .setTitle("Delete "+ tabContents.getTabName() +" Group of Notes")
                .setMessage("Are you sure you want to delete this group of notes?")
                .setPositiveButton("OK", (dialog, listenerInterface) -> {
                    noteArrays.remove(tabContents.getTabName());
                    for (int i = 0; i < tabs.size(); i++) {
                        if (tabs.get(i).getTabName().equals(tabContents.getTabName())) {
                            tabs.remove(i);
                            tabsAdapter.notifyDataSetChanged();
                            binding.tabsRecyclerView.smoothScrollToPosition(0);
                            currentTabName = null;
                            currentNotes = null;
                            List<NoteContents> emptyNotesArray = new ArrayList<>();
                            notesAdapter = new NotesAdapter(emptyNotesArray, this, this);
                            binding.notesRecyclerView.setAdapter(notesAdapter);
                            notesAdapter.notifyDataSetChanged();
                            binding.notesRecyclerView.smoothScrollToPosition(0);
                            binding.notesTitle.setText("");
                            saveTabs();
                            saveNotes();
                            break;
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, listenerInterface) -> dialog.dismiss())
                .create()
                .show();
    }


    private void saveTabs() {
        try {
            File path = getApplicationContext().getFilesDir();
            File tabArrayFile = new File(path, "tabs.txt");
            FileOutputStream tabsArrayFileOutputStream = new FileOutputStream(tabArrayFile);
            ObjectOutputStream tabsArrayObjectOutputStream = new ObjectOutputStream(tabsArrayFileOutputStream);
            tabsArrayObjectOutputStream.writeObject(tabs);
            tabsArrayFileOutputStream.close();
            tabsArrayObjectOutputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNotes() {
        try {
            File path = getApplicationContext().getFilesDir();
            File notesHashMapFile = new File(path, "notes.txt");
            FileOutputStream hashMapFileOutputStream = new FileOutputStream(notesHashMapFile);
            ObjectOutputStream hashMapObjectOutputStream = new ObjectOutputStream(hashMapFileOutputStream);
            hashMapObjectOutputStream.writeObject(noteArrays);
            hashMapFileOutputStream.close();
            hashMapObjectOutputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadTabs() {
        //System.out.println("original tabs array " + tabs);
        try {
            File path = getApplicationContext().getFilesDir();
            File tabsArrayFile = new File(path, "tabs.txt");
            FileInputStream tabsArrayFileInputStream = new FileInputStream(tabsArrayFile);
            ObjectInputStream tabsArrayObjectInputStream = new ObjectInputStream(tabsArrayFileInputStream);
            Object tabsArrayObject = tabsArrayObjectInputStream.readObject();
            tabs = (List<TabContents>) tabsArrayObject;
            tabsArrayFileInputStream.close();
            tabsArrayObjectInputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadNotes() {
        try {
            File path = getApplicationContext().getFilesDir();
            File notesHashMapFile = new File(path, "notes.txt");
            FileInputStream notesHashMapFileInputStream = new FileInputStream(notesHashMapFile);
            ObjectInputStream notesHashMapObjectInputStream = new ObjectInputStream(notesHashMapFileInputStream);
            Object notesHashMapObject = notesHashMapObjectInputStream.readObject();
            noteArrays = (HashMap<String, List<NoteContents>>) notesHashMapObject;
            notesHashMapFileInputStream.close();
            notesHashMapObjectInputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTextEdited(String text, int position, EditText note) {
        for (int i = 0; i < binding.notesRecyclerView.getAdapter().getItemCount(); i++) {
            NotesAdapter.NotesViewHolder notesViewHolder = (NotesAdapter.NotesViewHolder) binding.notesRecyclerView.findViewHolderForAdapterPosition(i);
            NoteContents currentNote = currentNotes.get(i);
            //System.out.println(" is the thingy null " + (notesViewHolder == null) + " " + " current " + i + " total " + binding.notesRecyclerView.getAdapter().getItemCount());
            if (notesViewHolder == null) {
                continue;
            }
            EditText notes = notesViewHolder.itemView.findViewById(R.id.notes);
            if (currentNote.getNote().equals(notes.getText().toString())) {
                continue;
            }
            currentNote.setNote(notes.getText().toString());
            saveNotes();
        }
        //notesAdapter = new NotesAdapter(currentNotes, this, this, this);
        //binding.notesRecyclerView.setAdapter(notesAdapter);
    }

    private void toggleTabsView() {
        if (binding.tabFrameLayout.getVisibility() == View.VISIBLE) {
            binding.tabFrameLayout.setVisibility(View.GONE);
            binding.addTabButton.setVisibility(View.GONE);
            binding.notesFrameLayout.setPadding(120, 0,0,0);
            binding.hideTabsButton.setRotation(180);
        }
        else {
            binding.tabFrameLayout.setVisibility(View.VISIBLE);
            binding.addTabButton.setVisibility(View.VISIBLE);
            binding.notesFrameLayout.setPadding(0, 0,0,0);
            binding.hideTabsButton.setRotation(0);
        }
    }
}