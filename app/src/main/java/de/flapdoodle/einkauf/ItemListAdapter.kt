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
import android.widget.Spinner
import android.widget.TextView

class ItemListAdapter internal constructor(
        context: Context
) : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items = emptyList<Item>() // Cached copy of items

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.textView)
        val priceView: TextView = itemView.findViewById(R.id.price)
        val amountView: Spinner = itemView.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = items[position]
        holder.nameView.text = current.name
        holder.priceView.text = "${current.unitPriceCent.toDouble() / 100.0} €"
        holder.amountView.setSelection(0)
    }

    internal fun setItems(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}


