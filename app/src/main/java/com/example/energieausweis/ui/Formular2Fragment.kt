package com.example.energieausweis.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.energieausweis.databinding.FragmentFormular2Binding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Formular2Fragment : Fragment() {

    private var _binding: FragmentFormular2Binding? = null
    private val binding get() = _binding!!

    private lateinit var pdfSaveLauncher: ActivityResultLauncher<Intent>
    private var tempPdfFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormular2Binding.inflate(inflater, container, false)

        pdfSaveLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    try {
                        val inputStream = tempPdfFile?.inputStream()
                        val outputStream = requireContext().contentResolver.openOutputStream(uri)

                        if (inputStream != null && outputStream != null) {
                            inputStream.copyTo(outputStream)
                            inputStream.close()
                            outputStream.close()
                            Toast.makeText(requireContext(), "PDF gespeichert", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Fehler beim Speichern: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        tempPdfFile?.delete()
                        tempPdfFile = null
                    }
                }
            }
        }

        binding.btnSpeichern.setOnClickListener {
            createPdf()
        }

        binding.btnZuruecksetzen.setOnClickListener {
            resetFields()
        }

        return binding.root
    }

    private fun createPdf() {
        val document = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        var y = 40

        fun drawSection(title: String, vararg choices: Pair<String, Boolean>, note: String?) {
            paint.textSize = 16f
            canvas.drawText(title, 40f, y.toFloat(), paint)
            y += 24
            paint.textSize = 12f
            for ((label, checked) in choices) {
                if (checked) {
                    canvas.drawText("$label", 40f, y.toFloat(), paint)
                    y += 18
                }
            }
            if (!note.isNullOrEmpty()) {
                canvas.drawText("Notiz: $note", 40f, y.toFloat(), paint)
                y += 18
            }
            y += 20
        }

        drawSection("Fensterplakette fotografiert?",
            "Ja" to binding.cbFensterJa.isChecked,
            "Nicht vorhanden" to binding.cbFensterNichtVorhanden.isChecked,
            note = binding.etFensterNotiz.text.toString()
        )

        drawSection("Dachboden",
            "Isoliert" to binding.cbDachIsoliert.isChecked,
            "Nicht isoliert" to binding.cbDachNichtIsoliert.isChecked,
            "Nicht vorhanden" to binding.cbDachNichtVorhanden.isChecked,
            note = binding.etDachNotiz.text.toString()
        )

        drawSection("Heizungsplakette fotografiert?",
            "Ja" to binding.cbHeizungsPlaketteJa.isChecked,
            "Nein" to binding.cbHeizungsPlaketteNein.isChecked,
            note = binding.etHeizungsPlaketteNotiz.text.toString()
        )

        drawSection("Heizungsrohre isoliert?",
            "Ja" to binding.cbHeizungsrohreJa.isChecked,
            "Nein" to binding.cbHeizungsrohreNein.isChecked,
            note = binding.etHeizungsrohreNotiz.text.toString()
        )

        drawSection("Kellerdecke gedämmt?",
            "Ja" to binding.cbKellerdeckeJa.isChecked,
            "Nein" to binding.cbKellerdeckeNein.isChecked,
            note = binding.etKellerdeckeNotiz.text.toString()
        )

        drawSection("Solaranlage?",
            "Ja" to binding.cbSolaranlageJa.isChecked,
            "Nein" to binding.cbSolaranlageNein.isChecked,
            note = binding.etSolaranlageNotiz.text.toString()
        )

        drawSection("Außenfassade gedämmt?",
            "Ja" to binding.cbAussenfassadeJa.isChecked,
            "Nein" to binding.cbAussenfassadeNein.isChecked,
            note = binding.etAussenfassadeNotiz.text.toString()
        )

        document.finishPage(page)

        val fileName = "energieausweis_formular2_${System.currentTimeMillis()}.pdf"
        val file = File(requireContext().cacheDir, fileName)

        try {
            val outputStream = FileOutputStream(file)
            document.writeTo(outputStream)
            outputStream.close()
            tempPdfFile = file
            Toast.makeText(requireContext(), "Temporäre PDF erstellt", Toast.LENGTH_SHORT).show()

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(Intent.EXTRA_TITLE, "Energieausweis_Formular2.pdf")
            }
            pdfSaveLauncher.launch(intent)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Fehler beim Schreiben: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            document.close()
        }
    }

    private fun resetFields() {
        binding.rgFenster.clearCheck()
        binding.etFensterNotiz.text.clear()
        binding.rgDach.clearCheck()
        binding.etDachNotiz.text.clear()
        binding.rgHeizungsPlakette.clearCheck()
        binding.etHeizungsPlaketteNotiz.text.clear()
        binding.rgHeizungsrohre.clearCheck()
        binding.etHeizungsrohreNotiz.text.clear()
        binding.rgKellerdecke.clearCheck()
        binding.etKellerdeckeNotiz.text.clear()
        binding.rgSolaranlage.clearCheck()
        binding.etSolaranlageNotiz.text.clear()
        binding.rgAussenfassade.clearCheck()
        binding.etAussenfassadeNotiz.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
