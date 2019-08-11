package de.flapdoodle.einkauf

/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ItemListAdapter internal constructor(
        context: Context,
        private val onAmountChanged: (Item, Int) -> Unit,
        private val onActiveChanged: (Item, Boolean) -> Unit,
        private val onDelete: (Item) -> Unit,
        private val onEdit: (Item) -> Unit
) : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items = emptyList<Item>() // Cached copy of items

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.textView)
        val priceView: TextView = itemView.findViewById(R.id.price)
        val amountView: Spinner = itemView.findViewById(R.id.amount)
        val checkboxView: CheckBox = itemView.findViewById(R.id.active)
        val imageButton: ImageButton = itemView.findViewById(R.id.delete)
        var current: Item? = null

        init {
            amountView.onItemSelectedListener = FixedOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //onAmountChanged(current, holder.amountView.selectedItem.toString().toInt())
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    onAmountChanged(current!!, amountView.selectedItem.toString().toInt())
                }
            })
            checkboxView.setOnCheckedChangeListener { compoundButton, checked ->
                onActiveChanged(current!!, checked)
            }
            imageButton.setOnClickListener {
                onDelete(current!!)
            }
            nameView.setOnClickListener {
                onEdit(current!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = items[position]

        holder.current = current
        holder.nameView.text = current.name
        holder.priceView.text = "${current.unitPriceCent.toDouble() / 100.0} €"

        @Suppress("UNCHECKED_CAST") val ad = holder.amountView.adapter as ArrayAdapter<String>
        val pos = ad.getPosition(current.amount.toString())
        holder.amountView.setSelection(pos)
    }

    internal fun setItems(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}


