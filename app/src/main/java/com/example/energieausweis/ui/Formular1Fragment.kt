package com.example.energieausweis.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.energieausweis.databinding.FragmentFormular1Binding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Formular1Fragment : Fragment() {

    private var _binding: FragmentFormular1Binding? = null
    private val binding get() = _binding!!

    private lateinit var currentImageTarget: ViewGroup
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var pdfSaveLauncher: ActivityResultLauncher<Intent>
    private var cameraImageUri: Uri? = null
    private var tempPdfFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormular1Binding.inflate(inflater, container, false)

        setupImageUpload(binding.layoutHausfassadeBilder, binding.btnHausfassade)
        setupImageUpload(binding.layoutHeizungBilder, binding.btnHeizung)
        setupImageUpload(binding.layoutHeizrohreBilder, binding.btnHeizrohre)
        setupImageUpload(binding.layoutFensterBilder, binding.btnFenster)
        setupImageUpload(binding.layoutDaemmungBilder, binding.btnDaemmung)
        setupImageUpload(binding.layoutSolaranlageBilder, binding.btnSolaranlage)

        setupActivityLaunchers()

        binding.btnSpeichern.setOnClickListener {
            createPdf()
        }

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

        pdfSaveLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    try {
                        val inputStream = tempPdfFile?.inputStream()
                        val outputStream = requireContext().contentResolver.openOutputStream(uri)

                        if (inputStream != null && outputStream != null) {
                            inputStream.copyTo(outputStream)
                            inputStream.close()
                            outputStream.close()
                            Toast.makeText(requireContext(), "PDF gespeichert", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(requireContext(), "Fehler beim Zugriff auf Streams", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Fehler beim Kopieren: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        tempPdfFile?.delete()
                        tempPdfFile = null
                    }
                }
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

    private fun createPdf() {
        val document = PdfDocument()
        val paint = Paint()
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1

        var page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas
        var y = 40

        fun newPage() {
            document.finishPage(page)
            pageNumber++
            page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page.canvas
            y = 40
        }

        fun drawSection(title: String, imageLayout: LinearLayout, notizFeld: EditText) {
            paint.textSize = 16f
            if (y > pageHeight - 100) newPage()
            canvas.drawText(title, 40f, y.toFloat(), paint)
            y += 30

            paint.textSize = 12f
            val lines = notizFeld.text.toString().split("\n")
            for (line in lines) {
                if (y > pageHeight - 40) newPage()
                canvas.drawText(line, 40f, y.toFloat(), paint)
                y += 18
            }
            y += 20

            if (imageLayout.childCount == 0) {
                if (y > pageHeight - 40) newPage()
                canvas.drawText("(Kein Bild vorhanden)", 40f, y.toFloat(), paint)
                y += 20
            } else {
                for (i in 0 until imageLayout.childCount) {
                    val view = imageLayout.getChildAt(i)
                    if (view.width > 0 && view.height > 0) {
                        val bmp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                        val viewCanvas = Canvas(bmp)
                        view.draw(viewCanvas)
                        val scaled = Bitmap.createScaledBitmap(bmp, 200, 200, true)
                        canvas.drawBitmap(scaled, 40f, y.toFloat(), paint)
                        y += 210
                    } else {
                        canvas.drawText("(Bild konnte nicht verarbeitet werden)", 40f, y.toFloat(), paint)
                        y += 20
                    }
                }
            }

            y += 40
        }



        drawSection("Hausfassade", binding.layoutHausfassadeBilder, binding.notizHausfassade)
        drawSection("Heizung", binding.layoutHeizungBilder, binding.notizHeizung)
        drawSection("Heizrohre", binding.layoutHeizrohreBilder, binding.notizHeizrohre)
        drawSection("Fenster", binding.layoutFensterBilder, binding.notizFenster)
        drawSection("Dämmung", binding.layoutDaemmungBilder, binding.notizDaemmung)
        drawSection("Solaranlage", binding.layoutSolaranlageBilder, binding.notizSolaranlage)

        document.finishPage(page)

        val fileName = "energieausweis_temp_${System.currentTimeMillis()}.pdf"
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
                putExtra(Intent.EXTRA_TITLE, "Energieausweis_Formular1.pdf")
            }
            pdfSaveLauncher.launch(intent)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Fehler beim Schreiben: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            document.close()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
