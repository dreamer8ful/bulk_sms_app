package com.introsoft.bulksms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val onSelectionChanged: (Int) -> Unit,
) : ListAdapter<ContactAdapter.SelectableContact, ContactAdapter.ViewHolder>(ContactDiffCallback()) {

    data class SelectableContact(
        val name: String,
        val phone: String,
        var isSelected: Boolean = false,
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.contactCheckbox)
        val name: TextView = view.findViewById(R.id.contactName)
        val phone: TextView = view.findViewById(R.id.contactPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = getItem(position)
        holder.name.text = contact.name
        holder.phone.text = contact.phone
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = contact.isSelected

        holder.itemView.setOnClickListener {
            contact.isSelected = !contact.isSelected
            holder.checkbox.isChecked = contact.isSelected
            onSelectionChanged(getSelectedCount())
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            contact.isSelected = isChecked
            onSelectionChanged(getSelectedCount())
        }
    }

    fun getSelectedContacts(): List<SelectableContact> = currentList.filter { it.isSelected }

    fun getSelectedCount(): Int = currentList.count { it.isSelected }

    private class ContactDiffCallback : DiffUtil.ItemCallback<SelectableContact>() {
        override fun areItemsTheSame(oldItem: SelectableContact, newItem: SelectableContact): Boolean {
            return oldItem.phone == newItem.phone
        }

        override fun areContentsTheSame(oldItem: SelectableContact, newItem: SelectableContact): Boolean {
            return oldItem == newItem
        }
    }
}
