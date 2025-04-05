package com.example.energieausweis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.energieausweis.databinding.FragmentFormular2Binding

class Formular2Fragment : Fragment() {
    private var _binding: FragmentFormular2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormular2Binding.inflate(inflater, container, false)

        binding.btnSpeichern2.setOnClickListener {
            val verbrauch = binding.etVerbrauch.text.toString()
            Toast.makeText(requireContext(), "Gespeichert: $verbrauch kWh", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
