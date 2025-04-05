package com.example.energieausweis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.energieausweis.databinding.FragmentFormular1Binding

class Formular1Fragment : Fragment() {
    private var _binding: FragmentFormular1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormular1Binding.inflate(inflater, container, false)

        binding.btnSpeichern.setOnClickListener {
            val adresse = binding.etAdresse.text.toString()
            Toast.makeText(requireContext(), "Gespeichert: $adresse", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
