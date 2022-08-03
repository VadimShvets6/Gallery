package com.top1shvetsvadim1.gallery.presentation.fragments.detail_screen_fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.top1shvetsvadim1.gallery.R
import com.top1shvetsvadim1.gallery.databinding.FragmentDetailBinding
import com.top1shvetsvadim1.gallery.presentation.adapter.detail_screen_adapter.FilterAdapter
import com.top1shvetsvadim1.gallery.presentation.utils.BitmapUtils
import com.top1shvetsvadim1.gallery.presentation.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DetailFragment : Fragment() {

    private val args by navArgs<DetailFragmentArgs>()

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding ?: throw RuntimeException("FragmentDetailBinding == null")

    private val filterAdapter by lazy {
        FilterAdapter(::onItemClicked)
    }

    private val copyOfOriginalPhoto by lazy {
        BitmapUtils.getBitmap(
            args.photoItem.mediaUrl,
            requireContext()
        )?.copy(Bitmap.Config.ARGB_8888, true)
    }
    private var originalPhotoWithFilters: Bitmap? = null

    private val viewModel by lazy {
        ViewModelProvider(this)[DetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getListBitmaps(args.photoItem.mediaUrl, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModelObserves()
        setupUserInterface()
        setupOnClickListeners()
    }

    private fun setupUserInterface() {
        Glide.with(requireContext())
            .load(args.photoItem.mediaUrl)
            .into(binding.ivImage)
        val path = args.photoItem.mediaUrl.toString()
        val filename: String = path.substring(path.lastIndexOf("/") + 1)
        binding.tvName.text = filename
    }

    private fun setupRecyclerView() {
        with(binding.rvListFilters) {
            adapter = filterAdapter
        }
    }

    private fun viewModelObserves() {
        viewModel.listBitmaps.observe(viewLifecycleOwner) {
            filterAdapter.submitList(it)
        }
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    binding.progressBar.isVisible = it.isLoading
                }
                else -> throw RuntimeException("error")
            }
        }
    }

    private fun setupOnClickListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/jpeg"
            }
            originalPhotoWithFilters?.let {
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    originalPhotoWithFilters?.let { filterBitmap ->
                        viewModel.shareImage(filterBitmap, requireContext())
                    }
                )
                startActivity(
                    Intent.createChooser(shareIntent, getString(R.string.share_photo_title))
                )
            } ?: shareIntent.putExtra(Intent.EXTRA_STREAM, args.photoItem.mediaUrl)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_photo_title)))
        }
        binding.buttonChecked.setOnClickListener {
            with(binding) {
                progressBarSave.isVisible = true
                progressBarSave.isSelected = true
            }
            lifecycleScope.launch(Dispatchers.Main) {
                val action = async(Dispatchers.IO) {
                    originalPhotoWithFilters?.let {
                        viewModel.saveImageToGallery(
                            requireContext(),
                            it,
                            args.photoItem.tag
                        )
                    }
                }
                (0..70).forEach {
                    delay(40)
                    with(binding) {
                        buttonBack.isClickable = false
                        progressBarSave.progress = it
                    }
                }
                action.await()
                (70..100).forEach {
                    delay(10)
                    binding.progressBarSave.progress = it
                    if (it == 100) {
                        with(binding) {
                            progressBarSave.isSelected = false
                            buttonBack.isClickable = true
                        }
                    }
                }
            }
        }
    }

    private fun onItemClicked(action: FilterAdapter.ActionFilterAdapter) {
        when (action) {
            is FilterAdapter.ActionFilterAdapter.OnFilterClicked -> {
                binding.buttonChecked.isVisible = true
                try {
                    originalPhotoWithFilters = action.filter.processFilter(
                        copyOfOriginalPhoto?.let {
                            Bitmap.createScaledBitmap(
                                it,
                                copyOfOriginalPhoto?.width ?: WIDTH_1920,
                                copyOfOriginalPhoto?.height ?: HEIGHT_1920,
                                false
                            )
                        }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Glide.with(requireContext()).load(originalPhotoWithFilters).into(binding.ivImage)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //TODO: move to separate file, use lazy
    companion object {
        init {
            System.loadLibrary("NativeImageProcessor")
        }

        private const val WIDTH_1920 = 1920
        private const val HEIGHT_1920 = 1920
    }

}