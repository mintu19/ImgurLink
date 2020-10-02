package com.akshit.imgurlink.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.akshit.imgurlink.R
import com.akshit.imgurlink.adapters.ImageGridAdapter
import com.akshit.imgurlink.apiHelpers.ClientException
import com.akshit.imgurlink.apiHelpers.ServerException
import com.akshit.imgurlink.apiHelpers.apis.GalleryApi
import com.akshit.imgurlink.apiHelpers.models.Image
import com.akshit.imgurlink.helpers.EndlessGridLayoutScrollListener
import com.akshit.imgurlink.helpers.autoFitColumns
import com.akshit.imgurlink.helpers.displayMessage
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(), ImageGridAdapter.ItemClickListener {

    companion object {
        const val IMAGE_SIZE = 't'
    }

    private val images = mutableListOf<Image>()
    private var searchTerm = ""
    private var nextPage = 1

    private val watcher = object: TextWatcher {
        private var searchFor = ""

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val searchText = s.toString().trim()
            if (searchText == searchFor || searchText.isBlank())
                return

            searchFor = searchText

            GlobalScope.launch(Dispatchers.Main) {
                delay(350)  //debounce timeOut
                if (searchText != searchFor)
                    return@launch

                images.clear()
                nextPage = 1
                searchTerm = searchFor
                // Todo: Cancel previous
                getResults(searchTerm, nextPage++)
            }
        }

        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        imagesGrid.apply {
            // if using decoration
//            val cols = resources.getInteger(R.integer.no_cols)
//            layoutManager = GridLayoutManager(this@SearchActivity, cols)
//            addItemDecoration(GridSpacingItemDecoration(cols, 10, true))
            // if using autofit (also generate layout)
            autoFitColumns(120)
            val myAdapter = ImageGridAdapter(images)
            myAdapter.itemClickListener = this@SearchActivity
            adapter = myAdapter
            addOnScrollListener(object : EndlessGridLayoutScrollListener() {
                override fun loadMoreItems() {
                    getResults(searchTerm, nextPage++)
                }

            })
        }

        commentBox.addTextChangedListener(watcher)
    }

    private fun getResults(text: String, page: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                progressBar.visibility = View.VISIBLE
                val resp = GalleryApi().searchGallery(text, page)
                val data = resp.data?.data

                if (data == null) {
                    displayMessage("No Results Found!!!")
                    return@launch
                }

                // Todo: Replace with cancelTask
                if (text != searchTerm) {
                    return@launch
                }

                // extracting images from gallery result and filtering out animations
                // animations and/or video thumbnails can used here when rebuild thumb link
                data.forEach { item ->
                    item.images?.forEach { image ->
                        if (image.link != null) {
                            // Modifying link here for thumbnail
                            val idx = image.link.lastIndexOf(".")
                            image.thumbLink = StringBuilder(image.link).insert(idx, IMAGE_SIZE).toString()
                            // Todo: Decide here if you want pass gallery title as title if image title is empty
                            images.add(image)
                        }
                    }
                }

                if (images.isEmpty()) {
                    displayMessage("No Results Found!!!")
                }

                imagesGrid.adapter?.notifyDataSetChanged()

            } catch (e: ClientException) {
                Log.e("SearchActivity", "Client Error: ${e.message}")
                displayMessage("Unable to fetch results")
            } catch (e: ServerException) {
                Log.e("SearchActivity", "Server Error: ${e.message}")
                displayMessage("Unable to fetch results")
            } catch (e: Throwable) {
                Log.e("SearchActivity", "Other Error: ", e)
                displayMessage("Unable to fetch results")
            }
            progressBar.visibility = View.GONE
        }
    }

    override fun onItemClick(view: View, position: Int) {
        try {
            val item = images[position]

            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra(ImageViewActivity.EXTRA_DATA, item)

            startActivity(intent)
        } catch (e: Throwable) {
            Log.e("SearchActivity", "Error: ", e)
        }
    }

    fun onSearchIconClick(view: View) {
        val text = commentBox.text.toString()

        if (text == searchTerm) return

        images.clear()
        nextPage = 1
        searchTerm = text
        getResults(searchTerm, nextPage++)
    }
}
