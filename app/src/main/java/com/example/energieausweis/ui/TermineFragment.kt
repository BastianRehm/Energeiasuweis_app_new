package com.example.energieausweis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.energieausweis.databinding.FragmentTermineBinding

class TermineFragment : Fragment() {

    private var _binding: FragmentTermineBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TerminAdapter
    private val terminListe = mutableListOf<Termin>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TerminAdapter(terminListe) { termin ->
            adapter.removeTermin(termin)
        }

        binding.rvTermine.adapter = adapter
        binding.rvTermine.layoutManager = LinearLayoutManager(requireContext())

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            binding.formLayout.visibility = View.VISIBLE

            binding.btnTerminSpeichern.setOnClickListener {
                val titel = binding.etTitel.text.toString()
                val uhrzeit = binding.etUhrzeit.text.toString()
                val ort = binding.etOrt.text.toString()
                val calendar = java.util.Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                val termin = Termin(
                    datum = calendar.timeInMillis,
                    titel = titel,
                    uhrzeit = uhrzeit,
                    ort = ort
                )

                terminListe.add(termin)
                adapter.updateTermine(terminListe)

                // Zurücksetzen
                binding.etTitel.text.clear()
                binding.etUhrzeit.text.clear()
                binding.etOrt.text.clear()
                binding.formLayout.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
