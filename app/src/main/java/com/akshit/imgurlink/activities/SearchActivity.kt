package com.akshit.imgurlink.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.akshit.imgurlink.R
import com.akshit.imgurlink.adapters.ImageGridAdapter
import com.akshit.imgurlink.apiHelpers.ClientException
import com.akshit.imgurlink.apiHelpers.ServerException
import com.akshit.imgurlink.apiHelpers.apis.GalleryApi
import com.akshit.imgurlink.apiHelpers.models.Image
import com.akshit.imgurlink.helpers.GridSpacingItemDecoration
import com.akshit.imgurlink.helpers.displayMessage
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val COLUMNS = 3
        const val IMAGE_SIZE = 's'
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private val images = mutableListOf<Image>()
    private var page = 1

    val watcher = object: TextWatcher {
        private var searchFor = ""

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val searchText = s.toString().trim()
            if (searchText == searchFor)
                return

            searchFor = searchText

            GlobalScope.launch {
                delay(300)  //debounce timeOut
                if (searchText != searchFor)
                    return@launch

                images.clear()
                page = 1
                getResults(searchFor, page)
            }
        }

        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        imagesGrid.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, COLUMNS)
            addItemDecoration(GridSpacingItemDecoration(COLUMNS, 10, true))
            adapter = ImageGridAdapter(this@SearchActivity, images)
        }

        searchBox.addTextChangedListener(watcher)
    }

    private fun getResults(text: String, page: Int) {
        GlobalScope.launch {
            try {
                runOnUiThread {
                    progressBar.visibility = View.VISIBLE
                }
                val resp = GalleryApi().searchGallery(text, page)
                val data = resp.data?.data

                if (data == null) {
                    runOnUiThread {
                        displayMessage("No Results Found!!!")
                    }
                    return@launch
                }

                data.forEach { item ->
                    item.images?.forEach { image ->
                        if (!image.animated && image.link != null) {
                            // Modifying link here
                            val idx = image.link.lastIndexOf(".")
                            image.thumbLink = StringBuilder(image.link).insert(idx, IMAGE_SIZE).toString()
                            images.add(image)
                        }
                    }
                }

                runOnUiThread {
                    if (images.isEmpty()) {
                        displayMessage("No Results Found!!!")
                    }

                    imagesGrid.adapter?.notifyDataSetChanged()
                    progressBar.visibility = View.INVISIBLE
                }

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

        }
    }

}
