package com.top1shvetsvadim1.gallery.presentation.adapter.detail_screen_adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.top1shvetsvadim1.gallery.R
import com.top1shvetsvadim1.gallery.databinding.FilterItemBinding
import com.top1shvetsvadim1.gallery.domain.FiltersItems
import com.zomato.photofilters.imageprocessors.Filter

class FilterAdapter(
    val onAction: (ActionFilterAdapter) -> Unit
) : ListAdapter<FiltersItems, FilterAdapter.FilterAdapterViewHolder>(FilterDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterAdapterViewHolder {
        val binding = FilterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterAdapterViewHolder, position: Int) {
        val filterItem = getItem(position)
        holder.bind(filterItem)
    }

    inner class FilterAdapterViewHolder(private val binding: FilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filterItem: FiltersItems) {
            with(binding) {
                constraint.startAnimation(
                    AnimationUtils.loadAnimation(
                        binding.ivFilter.context,
                        R.anim.horizontal_recycler
                    )
                )
                tvNameFilter.text = filterItem.filter.name
                Glide.with(binding.root)
                    .load(filterItem.filter.processFilter(filterItem.image))
                    .into(binding.ivFilter)
                ivFilter.setOnClickListener {
                    Log.d("BITMAP", filterItem.filter.name)
                    onAction(ActionFilterAdapter.OnFilterClicked(filterItem.filter))
                }
            }

        }
    }

    sealed interface ActionFilterAdapter {
        data class OnFilterClicked(val filter: Filter) : ActionFilterAdapter
    }
}