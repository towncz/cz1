package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.SceneryImages
import com.example.myapplication.model.Scenery

class SceneryAdapter(
    private val sceneries: List<Scenery>,
    private val onSceneryClick: (Scenery) -> Unit
) : RecyclerView.Adapter<SceneryAdapter.SceneryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SceneryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scenery, parent, false)
        return SceneryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SceneryViewHolder, position: Int) {
        holder.bind(sceneries[position])
    }

    override fun getItemCount(): Int = sceneries.size

    inner class SceneryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.sceneryImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.sceneryNameTextView)
        private val typeTextView: TextView = itemView.findViewById(R.id.sceneryTypeTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.sceneryPriceTextView)
        private val openStateTextView: TextView = itemView.findViewById(R.id.sceneryOpenStateTextView)

        fun bind(scenery: Scenery) {
            imageView.setImageResource(SceneryImages.imageForName(scenery.name))
            imageView.contentDescription = "${scenery.name}图片"
            nameTextView.text = scenery.name
            typeTextView.text = "类型：${scenery.type}"
            priceTextView.text = "票价：${scenery.price}"
            openStateTextView.text = if (scenery.isOpen) "开放状态：开放" else "开放状态：暂不开放"
            itemView.setOnClickListener { onSceneryClick(scenery) }
        }
    }
}
