package com.top1shvetsvadim1.gallery.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.top1shvetsvadim1.gallery.R
import com.top1shvetsvadim1.gallery.databinding.DataImageBinding
import com.top1shvetsvadim1.gallery.databinding.PhotoItemBinding
import com.top1shvetsvadim1.gallery.domain.Item

class GalleryAdapter : ListAdapter<Item, PhotoItemViewHolder>(PhotoDiffCallback) {

    //TODO: you should pass this lambda in adapter's constructor
    var onProductItemClickListeners: ((Item) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val layout = when (viewType) {
            ITEM_PHOTO -> R.layout.photo_item
            ITEM_HEADER -> R.layout.data_image
            //TODO: typo
            else -> throw RuntimeException("Unknow viewType: $viewType")
        }

        //TODO: you should create separate viewHolders for different layouts and create them here using native binding classes.
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return PhotoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        val photoItem = getItem(position)
        val binding = holder.binding

        //TODO: you should encapsulate UI login into ViewHolder classes. onBindViewHolder should only call corresponding functions in holder.
        when (photoItem) {
            is Item.PhotoItem -> {
                when (binding) {
                    is PhotoItemBinding -> {
                        //TODO: you should arrange code in another way if you use buildes
                        /*Glide.with(binding.root)
                            .load(photoItem.photo.mediaUrl)
                            .into(binding.ivImage)*/

                        Glide.with(binding.root).load(photoItem.photo.mediaUrl)
                            .into(binding.ivImage)

                        binding.ivImage.setOnClickListener {
                            onProductItemClickListeners?.invoke(photoItem)

                            //TODO: extract values from boolean check
                            /*val drawableRes = if (photoItem.photo.isChecked) R.drawable.check else R.drawable.circle
                            binding.ivChecked.setImageResource(drawableRes)*/


                            if (photoItem.photo.isChecked) {
                                binding.ivChecked.setImageResource(R.drawable.check)
                            } else {
                                binding.ivChecked.setImageResource(R.drawable.circle)
                            }
                        }
                    }
                }
            }
            is Item.HeaderItem -> {
                when (binding) {
                    is DataImageBinding -> {
                        binding.tvData.text = photoItem.title
                        binding.tvSize.text = photoItem.size.toString()
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Item.HeaderItem -> ITEM_HEADER
            is Item.PhotoItem -> ITEM_PHOTO
        }

    companion object {
        const val ITEM_HEADER = 1
        const val ITEM_PHOTO = 2
    }

}