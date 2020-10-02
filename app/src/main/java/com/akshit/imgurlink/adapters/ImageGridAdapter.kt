package com.akshit.imgurlink.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akshit.imgurlink.R
import com.akshit.imgurlink.apiHelpers.models.Image
import com.squareup.picasso.Picasso

class ImageGridAdapter(val context: Context, private val data: List<Image>): RecyclerView.Adapter<ImageGridAdapter.MyViewHolder>() {

    class MyViewHolder(val mainView: RelativeLayout) : RecyclerView.ViewHolder(mainView) {
        val imageView: ImageView = mainView.findViewById(R.id.itemImage)
        val textView: TextView = mainView.findViewById(R.id.itemText)
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int): Unit
    }

    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false) as RelativeLayout
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        Picasso.get().load(item.thumbLink ?: item.link).fit().into(holder.imageView)

        val text = item.title ?: item.description ?: "-"

        holder.textView.text = text

        holder.mainView.setOnClickListener {
            itemClickListener?.onClick(holder.mainView, position)
        }
    }

    override fun getItemCount(): Int = data.size
}