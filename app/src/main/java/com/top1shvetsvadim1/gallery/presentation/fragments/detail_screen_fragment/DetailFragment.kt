package com.top1shvetsvadim1.gallery.presentation.fragments.detail_screen_fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import com.top1shvetsvadim1.gallery.presentation.utils.Loading
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class DetailFragment : Fragment() {

    private val args by navArgs<DetailFragmentArgs>()

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding ?: throw RuntimeException("FragmentDetailBinding == null")

    private lateinit var filterAdapter: FilterAdapter
    private var mBitmap: Bitmap? = null
    private var filterBitmap: Bitmap? = null

    private val viewModel by lazy {
        ViewModelProvider(this)[DetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getListBitmaps(args.photoItem.mediaUrl, requireContext())
        mBitmap = BitmapUtils.getBitmap(
            args.photoItem.mediaUrl,
            requireContext()
        )
        mBitmap = mBitmap?.copy(Bitmap.Config.ARGB_8888, true);
        Log.d("BITMAP", mBitmap.toString())
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
        viewModelObserves()
        setupUserInterface()
        setupOnClickListeners()
    }

    private fun setupUserInterface() {
        Log.d("BITMAP", args.photoItem.mediaUrl.toString())
        Glide.with(requireContext()).load(args.photoItem.mediaUrl).into(binding.ivImage)
        val path = args.photoItem.mediaUrl.toString()
        val filename: String = path.substring(path.lastIndexOf("/") + 1)
        binding.tvName.text = String.format(
            Locale.getDefault(),
            getString(R.string.name_image_jpg),
            filename
        )
    }

    private fun viewModelObserves() {
        viewModel.listBitmaps.observe(viewLifecycleOwner) {
            filterAdapter = FilterAdapter(it, ::onItemClicked)
            binding.rvListFilters.adapter = filterAdapter
        }
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is Loading -> {
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
            val shareIntent = Intent()
            if (filterBitmap != null) {
                shareIntent.apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_STREAM,
                        filterBitmap?.let { filterBitmap ->
                            viewModel.shareImage(
                                filterBitmap,
                                requireContext()
                            )
                        })
                    type = "image/jpeg"
                }
                startActivity(Intent.createChooser(shareIntent, null))
            } else {
                shareIntent.apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, args.photoItem.mediaUrl)
                    type = "image/jpeg"
                }
                startActivity(Intent.createChooser(shareIntent, null))
            }
        }
        binding.buttonChecked.setOnClickListener {
            binding.progressBarSave.isVisible = true
            lifecycleScope.launch {
                (0..100).forEach {
                    delay(25)
                    binding.buttonBack.isClickable = false
                    binding.progressBarSave.progress = it
                    if (it == 100) {
                        binding.ivSaveSuccess.isVisible = true
                        binding.buttonBack.isClickable = true
                    }
                }
                filterBitmap?.let {
                    viewModel.saveImageToGallery(
                        requireContext(),
                        it,
                        args.photoItem.tag
                    )
                }
            }
        }
    }

    private fun onItemClicked(action: FilterAdapter.ActionFilterAdapter) {
        when (action) {
            is FilterAdapter.ActionFilterAdapter.OnFilterClicked -> {
                binding.buttonChecked.isVisible = true
                Log.d("BITMAP", "filterename :" + action.filter.name)
                Log.d("BITMAP", "bitmap: " + mBitmap?.width)
                try {
                    filterBitmap = action.filter.processFilter(
                        mBitmap?.let {
                            Bitmap.createScaledBitmap(
                                it,
                                mBitmap?.width ?: 1024,
                                mBitmap?.height ?: 1024,
                                false
                            )
                        }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.d("BITMAP", "FilterBitmap:" + filterBitmap.toString())
                Glide.with(requireContext()).load(filterBitmap).into(binding.ivImage)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        init {
            System.loadLibrary("NativeImageProcessor")
        }
    }

}