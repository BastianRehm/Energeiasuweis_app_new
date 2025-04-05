package com.example.energieausweis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.energieausweis.databinding.FragmentTermineBinding
import java.text.SimpleDateFormat
import java.util.*

class TermineFragment : Fragment() {
    private var _binding: FragmentTermineBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTermineBinding.inflate(inflater, container, false)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(GregorianCalendar(year, month, dayOfMonth).time)
            binding.tvAuswahl.text = "Ausgewählt: $date"
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
