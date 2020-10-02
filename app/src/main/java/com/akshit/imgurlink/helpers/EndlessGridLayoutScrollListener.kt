package com.akshit.imgurlink.helpers

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class EndlessGridLayoutScrollListener: RecyclerView.OnScrollListener() {

    // Total items after last load
    private var prevTotal = 0

    // last set of data is loading (after scrolled)
    private var isLoading = true

    abstract fun loadMoreItems()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (isLoading) {
            if (totalItemCount > prevTotal) {
                isLoading = false;
                prevTotal = totalItemCount;
            }
        }

        if (!isLoading) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                loadMoreItems()
            }
        }
    }
}