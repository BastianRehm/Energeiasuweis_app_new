package com.example.energieausweis.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.energieausweis.databinding.FragmentFormular1Binding

class Formular1Fragment : Fragment() {
    private var _binding: FragmentFormular1Binding? = null
    private val binding get() = _binding!!

    private val imagePickers = mutableMapOf<Int, (Uri?) -> Unit>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormular1Binding.inflate(inflater, container, false)

        setupImagePicker(binding.btnHausfassade, binding.imageHausfassade, 1)
        setupImagePicker(binding.btnHeizung, binding.imageHeizung, 2)
        setupImagePicker(binding.btnHeizrohre, binding.imageHeizrohre, 3)
        setupImagePicker(binding.btnFenster, binding.imageFenster, 4)
        setupImagePicker(binding.btnDaemmung, binding.imageDaemmung, 5)
        setupImagePicker(binding.btnSolaranlage, binding.imageSolaranlage, 6)

        return binding.root
    }

    private fun setupImagePicker(button: View, imageView: View, key: Int) {
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                (imageView as? android.widget.ImageView)?.setImageURI(uri)
                imagePickers[key]?.invoke(uri)
            }
        }

        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launcher.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}