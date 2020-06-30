package com.appdev.statusdownloader.FileObserver;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class RVEmptyObserver extends RecyclerView.AdapterDataObserver{

    private TextView emptyView;
    private RecyclerView recycler_business_status_image;

    /*public RVEmptyObserver(View emptyView, RecyclerView recyclerView) {
        this.emptyView = emptyView;
        this.recyclerView = recyclerView;
    }*/

    /*public RVEmptyObserver(RecyclerView recycler_business_status_image, TextView emptyView) {
    }*/

    public RVEmptyObserver(RecyclerView recycler_business_status_image, TextView emptyView) {
        this.emptyView = emptyView;
        this.recycler_business_status_image = recycler_business_status_image;
        checkIfEmpty();
    }

    private void checkIfEmpty() {

        if (emptyView != null && recycler_business_status_image.getAdapter() != null) {
            boolean emptyViewVisible = recycler_business_status_image.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            recycler_business_status_image.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }

    }

    @Override
    public void onChanged() {
        checkIfEmpty();
        super.onChanged();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        checkIfEmpty();
        super.onItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        checkIfEmpty();
        super.onItemRangeRemoved(positionStart, itemCount);
    }
}
