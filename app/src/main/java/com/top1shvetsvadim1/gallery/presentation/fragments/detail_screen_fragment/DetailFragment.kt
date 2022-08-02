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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class DetailFragment : Fragment() {

    private val args by navArgs<DetailFragmentArgs>()

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding ?: throw RuntimeException("FragmentDetailBinding == null")

    private lateinit var filterAdapter: FilterAdapter

    //TODO: you can lazy init it
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
        //TODO: builder pattern
        Glide.with(requireContext()).load(args.photoItem.mediaUrl).into(binding.ivImage)

        val path = args.photoItem.mediaUrl.toString()
        //TODO: shorten these row with, f.e. split
        val filename: String = path.substring(path.lastIndexOf("/") + 1)
        //TODO: check mime-type of file instead of force put jpg
        binding.tvName.text = String.format(
            Locale.getDefault(),
            getString(R.string.name_image_jpg),
            filename
        )
    }

    private fun viewModelObserves() {
        viewModel.listBitmaps.observe(viewLifecycleOwner) {
            //TODO: never reinitialize adapters, use list adapter instead
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
        //TODO: simplify comparison. There block differs only with extra blocks
        binding.buttonShare.setOnClickListener {
            //TODO: do it in kotlin way. Try apply or create a function
            val shareIntent = Intent()
            //TODO: never check mutable variable for null with it. Use let instead
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
                //TODO: add title
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
            //TODO: change to callbacks
            lifecycleScope.launch(Dispatchers.IO) {
                val action = async(Dispatchers.IO) {
                    filterBitmap?.let {
                        viewModel.saveImageToGallery(
                            requireContext(),
                            it,
                            args.photoItem.tag
                        )
                    }
                }
                (0..70).forEach {
                    delay(25)
                    binding.buttonBack.isClickable = false
                    binding.progressBarSave.progress = it
                }
                action.await()
                (70..100).forEach {
                    delay(10)
                    binding.buttonBack.isClickable = false
                    binding.progressBarSave.progress = it
                    if (it == 100) {
                        binding.ivSaveSuccess.isVisible = true
                        binding.buttonBack.isClickable = true
                    }
                }
            }
        }
    }

    private fun onItemClicked(action: FilterAdapter.ActionFilterAdapter) {
        when (action) {
            //TODO: pass a filter instance and manage it separately
            is FilterAdapter.ActionFilterAdapter.OnFilterClicked -> {
                binding.buttonChecked.isVisible = true
                Log.d("BITMAP", "filterename :" + action.filter.name)
                Log.d("BITMAP", "bitmap: " + mBitmap?.width)
                try {
                    filterBitmap = action.filter.processFilter(
                        mBitmap?.let {
                            Bitmap.createScaledBitmap(
                                it,
                                mBitmap?.width ?: 1024, //TODO: create constants
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

    //TODO: move to separate file, use lazy
    companion object {
        init {
            System.loadLibrary("NativeImageProcessor")
        }
    }

}