package com.ar.androidware.walkietalkie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class ViewAdapter(private val itemList: ArrayList<Model>) :
    RecyclerView.Adapter<ViewAdapter.ModelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAdapter.ModelViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter, parent, false)
        return ModelViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewAdapter.ModelViewHolder, position: Int) {

        holder.img.setImageBitmap(itemList[position].image)
        holder.text.text = itemList[position].description
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var cl: ConstraintLayout
        var img: ImageView
        var text: TextView


        init {
            cl = itemView.findViewById(R.id.cl) as ConstraintLayout
            img = itemView.findViewById(R.id.profile) as ImageView
            text = itemView.findViewById(R.id.contact) as TextView
        }
    }
}