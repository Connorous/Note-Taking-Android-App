package com.example.notetakingapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetakingapp.databinding.ItemContainerTabBinding;

import java.util.List;

public class TabsAdapter extends RecyclerView.Adapter<TabsAdapter.TabViewHolder> {

    private final List<TabContents> tabs;

    private TabListener tabListener;

    private RemoveTabListener removeTabListener;

    public TabsAdapter(List<TabContents> tabs, TabListener tabListener, RemoveTabListener removeTabListener) {
        this.tabs = tabs;
        this.tabListener = tabListener;
        this.removeTabListener = removeTabListener;
    }

    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerTabBinding itemContainerTabBinding = ItemContainerTabBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TabViewHolder(itemContainerTabBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
        holder.setData(tabs.get(position));
    }

    @Override
    public int getItemCount() {
        return tabs.size();
    }

    class TabViewHolder extends RecyclerView.ViewHolder {

        private ItemContainerTabBinding itemContainerTabBinding;
        public TabViewHolder(@NonNull ItemContainerTabBinding itemContainerTabBinding) {
            super(itemContainerTabBinding.getRoot());
            this.itemContainerTabBinding = itemContainerTabBinding;
        }

        public void setData(TabContents tabContents) {
            itemContainerTabBinding.tabName.setText(tabContents.getTabName());
            //itemContainerTabBinding.dateCreated.setText(new SimpleDateFormat("MMMM dd, yyyy - hh : mm a", Locale.getDefault()).format(tabContents.getDateCreated()));
            itemContainerTabBinding.tabName.setOnClickListener(v -> tabListener.onTabClicked(tabContents));
            itemContainerTabBinding.deleteButton.setOnClickListener(v -> removeTabListener.onTabRemoveClicked(tabContents));
        }
    }
}
