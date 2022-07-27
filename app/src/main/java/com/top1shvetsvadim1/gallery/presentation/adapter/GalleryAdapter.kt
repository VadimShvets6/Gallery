package com.top1shvetsvadim1.gallery.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.top1shvetsvadim1.gallery.databinding.DataImageBinding
import com.top1shvetsvadim1.gallery.databinding.PhotoItemBinding
import com.top1shvetsvadim1.gallery.domain.HeaderItem
import com.top1shvetsvadim1.gallery.domain.Photo
import com.top1shvetsvadim1.gallery.domain.PhotoItem
import com.top1shvetsvadim1.gallery.presentation.utils.ItemUIModel

class GalleryAdapter(val onAction: (Action) -> Unit) :
    ListAdapter<ItemUIModel, RecyclerView.ViewHolder>(PhotoDiffCallback) {

    private var singleItem = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ITEM_PHOTO -> PhotoItemViewHolder(
                PhotoItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            ITEM_HEADER -> HeaderViewHolder(
                DataImageBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )
            else -> throw RuntimeException("Unknown type")
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val photoItem = getItem(position)) {
            is PhotoItem -> {
                when (holder) {
                    is PhotoItemViewHolder -> {
                        holder.bind(photoItem, position)
                    }
                }
            }
            is HeaderItem -> {
                when (holder) {
                    is HeaderViewHolder -> {
                        holder.bind(photoItem)
                    }
                }
            }
        }
    }

    private fun singleItemCheck(position: Int) {
        if (position == RecyclerView.NO_POSITION) return

        notifyItemChanged(singleItem)
        singleItem = position
        notifyItemChanged(singleItem)
    }


    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is HeaderItem -> ITEM_HEADER
            is PhotoItem -> ITEM_PHOTO
            else -> throw RuntimeException("Unknown view")
        }

    inner class PhotoItemViewHolder(private val binding: PhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhotoItem, position: Int) {
            Glide.with(binding.root).load(item.photo.mediaUrl).into(binding.ivImage)

            if (singleItem == position) {
                binding.ivCheckedTrue.visibility = View.VISIBLE
            } else {
                binding.ivCheckedTrue.visibility = View.GONE
            }
            binding.ivImage.setOnClickListener {
                onAction(Action.OnPhotoClicked(item.photo))
                singleItemCheck(position)
            }
        }
    }

    inner class HeaderViewHolder(
        private val binding: DataImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HeaderItem) {
            binding.tvData.text = item.title
            binding.tvSize.text = item.size.toString()
        }
    }

    companion object {
        const val ITEM_HEADER = 1
        const val ITEM_PHOTO = 2
    }

    sealed interface Action {
        data class OnPhotoClicked(val photo: Photo) : Action
    }

}