package com.top1shvetsvadim1.gallery.presentation.fragments.detail_screen_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.top1shvetsvadim1.gallery.R
import com.top1shvetsvadim1.gallery.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private val args by navArgs<DetailFragmentArgs>()

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding ?: throw RuntimeException("FragmentDetailBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext()).load(args.photoItem.mediaUrl).into(binding.ivImage)
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonShare.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, args.photoItem.mediaUrl)
                type = "image/jpeg"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}