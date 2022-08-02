package com.top1shvetsvadim1.gallery.presentation.adapter.detail_screen_adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.top1shvetsvadim1.gallery.R
import com.top1shvetsvadim1.gallery.databinding.ItemFilteerBinding
import com.top1shvetsvadim1.gallery.domain.FiltersItems
import com.zomato.photofilters.imageprocessors.Filter

//TODO: use list adapter
class FilterAdapter(
    private val data: List<FiltersItems>,
    val onAction: (ActionFilterAdapter) -> Unit
) :
    RecyclerView.Adapter<FilterAdapter.FilterAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterAdapterViewHolder {
        val binding = ItemFilteerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterAdapterViewHolder, position: Int) {
        val filterItem = data[position]
        holder.bind(filterItem)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class FilterAdapterViewHolder(private val binding: ItemFilteerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filterItem: FiltersItems) {
            //TODO: use binding.apply or with(binding) for better code formatting
            binding.constraint.startAnimation(
                AnimationUtils.loadAnimation(
                    binding.ivFilter.context,
                    R.anim.horizontal_recycler
                )
            )
            binding.tvNameFilter.text = filterItem.filter.name
            //TODO: while you use code in builder pattern, you should add newline before every build step
            /*Glide.with(binding.root)
                .load(filterItem.filter.processFilter(filterItem.image))
                .into(binding.ivFilter)*/


            Glide.with(binding.root).load(filterItem.filter.processFilter(filterItem.image))
                .into(binding.ivFilter)
            binding.ivFilter.setOnClickListener {
                Log.d("BITMAP", filterItem.filter.name)
                onAction(ActionFilterAdapter.OnFilterClicked(filterItem.filter))
            }
        }

    }

    sealed interface ActionFilterAdapter {
        data class OnFilterClicked(val filter: Filter) : ActionFilterAdapter
    }
}