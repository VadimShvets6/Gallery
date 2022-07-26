package com.top1shvetsvadim1.gallery.presentation.fragments.MainScreenFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.top1shvetsvadim1.gallery.databinding.FragmentMainGalleryBinding
import com.top1shvetsvadim1.gallery.domain.Item
import com.top1shvetsvadim1.gallery.presentation.adapter.GalleryAdapter
import com.top1shvetsvadim1.gallery.presentation.adapter.GalleryAdapter.Companion.ITEM_HEADER
import com.top1shvetsvadim1.gallery.presentation.adapter.GalleryAdapter.Companion.ITEM_PHOTO

class MainGalleryFragment : Fragment() {

    private var _binding: FragmentMainGalleryBinding? = null
    private val binding: FragmentMainGalleryBinding
        get() = _binding ?: throw RuntimeException("FragmentMainGalleryBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this)[PhotoViewModel::class.java]
    }

    private lateinit var mProductAdapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelMethods()
        setupRecyclerView()
        viewModelObserves()
        mProductAdapter.onProductItemClickListeners = {
            when (it) {
                is Item.PhotoItem -> {
                    viewModel.changeEnableState(it)
                    binding.floatingButtonCamera.visibility = View.GONE
                    binding.linearButtons.visibility = View.VISIBLE
                    binding.buttonEdit.setOnClickListener { view ->
                        findNavController().navigate(
                            MainGalleryFragmentDirections.actionMainGalleryFragmentToDetailFragment2(
                                it.photo
                            )
                        )
                    }
                    binding.buttonCancel.setOnClickListener { view ->
                        binding.linearButtons.visibility = View.GONE
                        binding.floatingButtonCamera.visibility = View.VISIBLE
                    }
                    Log.d("ITEM", "Item: " + it.photo.mediaUrl + ", " + it.photo.isChecked)
                }
                else -> {
                }
            }
        }
    }

    private fun viewModelObserves() {
        viewModel.listPhoto.observe(viewLifecycleOwner) {
            Log.d("LISTTEST", it.toString())
            mProductAdapter.submitList(it)
        }
    }

    private fun viewModelMethods() {
        viewModel.getListPhoto(requireContext())
    }

    private fun setupRecyclerView() {
        with(binding.rvList) {
            mProductAdapter = GalleryAdapter()
            val layoutManagers = GridLayoutManager(requireContext(), 4).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter?.getItemViewType(position)) {
                            ITEM_HEADER -> 4
                            ITEM_PHOTO -> 1
                            else -> 1
                        }
                    }
                }
            }
            layoutManager = layoutManagers //GridLayoutManager(requireContext(), 4)
            setHasFixedSize(true)
            adapter = mProductAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}