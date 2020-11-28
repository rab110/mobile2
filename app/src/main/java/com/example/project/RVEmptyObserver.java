package com.example.project;

import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;


public class RVEmptyObserver extends RecyclerView.AdapterDataObserver {
    private RelativeLayout list;
    private RecyclerView recyclerView;

    public RVEmptyObserver(RecyclerView rv, RelativeLayout ev) {
        this.recyclerView = rv;
        this.list = ev;
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (list != null && recyclerView.getAdapter() != null) {
            boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
            list.setVisibility(emptyViewVisible ? RelativeLayout.VISIBLE : RelativeLayout.GONE);
            recyclerView.setVisibility(emptyViewVisible ? RelativeLayout.GONE : RelativeLayout.VISIBLE);
        }
    }

    public void onChanged() { checkIfEmpty(); }
    public void onItemRangeInserted(int positionStart, int itemCount) { checkIfEmpty(); }
    public void onItemRangeRemoved(int positionStart, int itemCount) { checkIfEmpty(); }
}