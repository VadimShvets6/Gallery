package com.top1shvetsvadim1.gallery.presentation.adapter.main_screen_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.top1shvetsvadim1.gallery.R
import com.top1shvetsvadim1.gallery.databinding.DataImageBinding
import com.top1shvetsvadim1.gallery.databinding.PhotoItemBinding
import com.top1shvetsvadim1.gallery.domain.HeaderItem
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
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when (val photoItem = getItem(position)) {
            is PhotoItem -> {
                when (holder) {
                    is PhotoItemViewHolder -> {
                        if (payloads.isEmpty()) {
                            onBindViewHolder(holder, position)
                        } else {
                            if (payloads[0] == true) {
                                holder.setChecked(photoItem.isFavorite)
                            }
                        }
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

        if (singleItem != -1) {
            val oldItem = getItem(singleItem) as PhotoItem
            oldItem.isFavorite = false
            notifyItemChanged(singleItem, true)
        }

        singleItem = position

        val newItem = getItem(singleItem) as PhotoItem
        newItem.isFavorite = true
        notifyItemChanged(singleItem, true)
    }


    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is HeaderItem -> ITEM_HEADER
            is PhotoItem -> ITEM_PHOTO
            else -> throw RuntimeException("Unknown view")
        }

    fun resetChoice() {
        if (singleItem == -1) {
            return
        }
        val newItem = getItem(singleItem) as PhotoItem
        newItem.isFavorite = false
        notifyItemChanged(singleItem, true)
    }

    inner class PhotoItemViewHolder(private val binding: PhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhotoItem, position: Int) {
            Glide.with(binding.root).load(item.mediaUrl).into(binding.ivImage)
            binding.constraint.startAnimation(
                AnimationUtils.loadAnimation(
                    binding.ivImage.context,
                    R.anim.anim_recycler
                )
            )
            binding.ivImage.setOnClickListener {
                onAction(Action.OnPhotoClicked(item))
                singleItemCheck(position)
            }
            setChecked(item.isFavorite)
        }

        fun setChecked(isChecked: Boolean) {
            binding.ivCheckedTrue.isVisible = isChecked
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
        data class OnPhotoClicked(val photo: PhotoItem) : Action
    }

}