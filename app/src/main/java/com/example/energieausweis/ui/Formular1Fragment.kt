// Formular1Fragment.kt
package com.example.energieausweis.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.energieausweis.databinding.FragmentFormular1Binding
import java.text.SimpleDateFormat
import java.util.*

class Formular1Fragment : Fragment() {
    private var _binding: FragmentFormular1Binding? = null
    private val binding get() = _binding!!

    private lateinit var currentImageTarget: ViewGroup
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var cameraImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormular1Binding.inflate(inflater, container, false)

        setupImageUpload(binding.layoutHausfassadeBilder, binding.btnHausfassade)
        // Weitere Buttons & Layouts folgen wie z. B.:
        setupImageUpload(binding.layoutHeizungBilder, binding.btnHeizung)
        setupImageUpload(binding.layoutFensterBilder, binding.btnFenster)
        setupImageUpload(binding.layoutHeizrohreBilder, binding.btnHeizrohre)
        setupImageUpload(binding.layoutDaemmungBilder, binding.btnDaemmung)
        setupImageUpload(binding.layoutSolaranlageBilder, binding.btnSolaranlage)

        setupActivityLaunchers()
        return binding.root
    }

    private fun setupActivityLaunchers() {
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val clipData = result.data?.clipData
                val uri = result.data?.data

                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        addImageToLayout(imageUri, currentImageTarget)
                    }
                } else if (uri != null) {
                    addImageToLayout(uri, currentImageTarget)
                }
            }
        }


        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && cameraImageUri != null) {
                addImageToLayout(cameraImageUri!!, currentImageTarget)
            }
        }
    }

    private fun setupImageUpload(imageContainer: ViewGroup, button: View) {
        button.setOnClickListener {
            currentImageTarget = imageContainer

            val options = arrayOf("Kamera", "Galerie")
            AlertDialog.Builder(requireContext())
                .setTitle("Bild auswählen:")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> openCamera()
                        1 -> openGallery()
                    }
                }
                .show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        galleryLauncher.launch(Intent.createChooser(intent, "Bilder auswählen"))
    }


    private fun openCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "IMG_$timeStamp")
            put(MediaStore.Images.Media.DESCRIPTION, "Foto aus Kamera")
        }
        cameraImageUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        cameraLauncher.launch(intent)
    }

    private fun addImageToLayout(imageUri: Uri, container: ViewGroup) {
        val imageView = ImageView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(300, 300)
            setImageURI(imageUri)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(8, 8, 8, 8)
            background = ContextCompat.getDrawable(requireContext(), android.R.drawable.picture_frame)
        }
        container.addView(imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
