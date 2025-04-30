package com.example.energieausweis.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.energieausweis.R

data class Termin(
    val datum: Long,
    val titel: String,
    val uhrzeit: String,
    val ort: String
)

class TerminAdapter(
    private var termine: MutableList<Termin>,
    private val onDeleteClicked: (Termin) -> Unit
) : RecyclerView.Adapter<TerminAdapter.TerminViewHolder>() {

    inner class TerminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titel: TextView = view.findViewById(R.id.tvTitel)
        val beschreibung: TextView = view.findViewById(R.id.tvBeschreibung)
        val deleteBtn: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TerminViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_termin, parent, false)
        return TerminViewHolder(view)
    }

    override fun onBindViewHolder(holder: TerminViewHolder, position: Int) {
        val termin = termine[position]
        holder.titel.text = "${termin.titel} - ${termin.uhrzeit}"
        holder.beschreibung.text = termin.ort
        holder.deleteBtn.setOnClickListener {
            onDeleteClicked(termin)
        }
    }

    override fun getItemCount(): Int = termine.size

    fun updateTermine(neueListe: List<Termin>) {
        termine = neueListe.toMutableList()
        notifyDataSetChanged()
    }

    fun removeTermin(termin: Termin) {
        termine.remove(termin)
        notifyDataSetChanged()
    }
}
