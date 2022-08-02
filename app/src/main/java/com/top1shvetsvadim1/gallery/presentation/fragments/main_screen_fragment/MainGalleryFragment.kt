package com.top1shvetsvadim1.gallery.presentation.fragments.main_screen_fragment

import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.top1shvetsvadim1.gallery.databinding.FragmentMainGalleryBinding
import com.top1shvetsvadim1.gallery.presentation.adapter.main_screen_adapter.GalleryAdapter
import com.top1shvetsvadim1.gallery.presentation.adapter.main_screen_adapter.GalleryAdapter.Companion.ITEM_HEADER
import com.top1shvetsvadim1.gallery.presentation.adapter.main_screen_adapter.GalleryAdapter.Companion.ITEM_PHOTO
import com.top1shvetsvadim1.gallery.presentation.utils.MediaContentObserver
import com.top1shvetsvadim1.gallery.presentation.utils.SpanGridLayoutManager
import com.top1shvetsvadim1.gallery.presentation.utils.State


class MainGalleryFragment : Fragment() {

    private var _binding: FragmentMainGalleryBinding? = null
    private val binding: FragmentMainGalleryBinding
        get() = _binding ?: throw RuntimeException("FragmentMainGalleryBinding == null")

    private val observerFile by MediaContentObserver.lazyInit(this::onMediaChanged)
    //TODO: for future: use lazy initialization only if it is necessary (you need context or it is very heavy to init)
    //TODO: instead of span manager, create a whole configurable spanGridLayout
    private val spanManager by SpanGridLayoutManager.lazyInit(this::onSpanManger)

    private val viewModel by lazy {
        ViewModelProvider(this)[PhotoViewModel::class.java]
    }

    //TODO: never use java M letter
    private val mProductAdapter by lazy {
        GalleryAdapter(::onItemClicked)
    }

    private fun onItemClicked(action: GalleryAdapter.Action) {
        when (action) {
            is GalleryAdapter.Action.OnPhotoClicked -> {
                //TODO: use apply or when
                binding.floatingButtonCamera.isVisible = false
                binding.linearButtons.isVisible = true
                binding.buttonCancel.setOnClickListener {
                    binding.linearButtons.isVisible = false
                    binding.floatingButtonCamera.isVisible = true
                    mProductAdapter.resetChoice()
                }
                binding.buttonEdit.setOnClickListener {
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

    override fun onStart() {
        super.onStart()
        requireContext().contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observerFile
        )
    }

    private fun onMediaChanged() {
        viewModelMethods()
        //TODO: remove
        setupRecyclerView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModelObserves()
        mProductAdapter.resetChoice()
        //TODO: remove
        ViewCompat.setTransitionName(binding.rvList, "item_image")
    }

    private fun viewModelObserves() {
        viewModel.listPhoto.observe(viewLifecycleOwner) {
            mProductAdapter.submitList(it)
        }
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.progressBar.isVisible = it.isLoading
                }
            }
        }
    }

    private fun viewModelMethods() {
        viewModel.getListPhoto(requireContext())
    }

    //TODO: rename properly
    private fun onSpanManger(position: Int): Int {
        return when (binding.rvList.adapter?.getItemViewType(position)) {
            ITEM_HEADER -> 4
            ITEM_PHOTO -> 1
            else -> 1
        }
    }

    private fun setupRecyclerView() {
        with(binding.rvList) {
            val layoutManagers = GridLayoutManager(requireContext(), 4).apply {
                spanSizeLookup = spanManager
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

    //TODO: check, maybe it is possible to move to onPause
    override fun onDetach() {
        super.onDetach()
        requireContext().contentResolver.unregisterContentObserver(observerFile)
    }
}