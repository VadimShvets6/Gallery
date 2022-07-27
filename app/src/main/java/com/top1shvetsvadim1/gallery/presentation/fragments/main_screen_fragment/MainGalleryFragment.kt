package com.top1shvetsvadim1.gallery.presentation.fragments.main_screen_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.top1shvetsvadim1.gallery.databinding.FragmentMainGalleryBinding
import com.top1shvetsvadim1.gallery.presentation.adapter.GalleryAdapter
import com.top1shvetsvadim1.gallery.presentation.adapter.GalleryAdapter.Companion.ITEM_HEADER
import com.top1shvetsvadim1.gallery.presentation.adapter.GalleryAdapter.Companion.ITEM_PHOTO

class MainGalleryFragment : Fragment() {

    private var _binding: FragmentMainGalleryBinding? = null
    private val binding: FragmentMainGalleryBinding
        get() = _binding ?: throw RuntimeException("FragmentMainGalleryBinding == null")

    //TODO: integrate Dagger Hilt
    private val viewModel by lazy {
        ViewModelProvider(this)[PhotoViewModel::class.java]
    }

    private val mProductAdapter by lazy {
        GalleryAdapter(::onItemClicked)
    }

    private fun onItemClicked(action: GalleryAdapter.Action) {
        when (action) {
            is GalleryAdapter.Action.OnPhotoClicked -> {
                binding.floatingButtonCamera.isVisible = false
                binding.linearButtons.isVisible = true
                binding.buttonCancel.setOnClickListener {
                    binding.linearButtons.isVisible = false
                    binding.floatingButtonCamera.isVisible = true
                }
                binding.buttonEdit.setOnClickListener { view ->
                    findNavController().navigate(
                        MainGalleryFragmentDirections.actionMainGalleryFragmentToDetailFragment2(
                            action.photo
                        )
                    )
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelMethods()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModelObserves()
    }

    private fun viewModelObserves() {
        viewModel.listPhoto.observe(viewLifecycleOwner) {
            Log.d("LISTTEST", it.toString())
            mProductAdapter.submitList(it)
        }
    }

    private fun viewModelMethods() {
        //TODO: you should avoid to pass context in viewModel if it is not necessary
        viewModel.getListPhoto(requireContext())
    }

    private fun setupRecyclerView() {
        with(binding.rvList) {
            //TODO: you can extract this class into separate class, not the anonymous one
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
            layoutManager = layoutManagers
            setHasFixedSize(true)
            adapter = mProductAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}