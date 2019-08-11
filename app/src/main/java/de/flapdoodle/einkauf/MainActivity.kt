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

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager


class MainActivity : AppCompatActivity() {

    private val newItemActivityRequestCode = 1
    private val editItemActivityRequestCode = 2

    private lateinit var itemViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ItemListAdapter(this, { item, amount ->
            itemViewModel.updateAmount(item, amount)
        }, {item, checked ->
            itemViewModel.updateActive(item, checked)
        }, { item ->
            itemViewModel.delete(item)
        }, {item ->
            val intent = Intent(this@MainActivity, EditItemActivity::class.java)
            intent.putExtra(EditItemActivity.EXTRA_REPLY_ID, item.id)
            intent.putExtra(EditItemActivity.EXTRA_REPLY_NAME, item.name)
            intent.putExtra(EditItemActivity.EXTRA_REPLY_PRICE, item.unitPriceCent)
            startActivityForResult(intent, editItemActivityRequestCode)
        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.layoutManager = GridLayoutManager(this, 2)

        val sumActual: TextView = findViewById(R.id.sum_actual)
        val sumPlaned: TextView = findViewById(R.id.sum_planed)

        // Get a new or existing ViewModel from the ViewModelProvider.

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        itemViewModel.allItems.observe(this, Observer { items ->
            // Update the cached copy of the items in the adapter.
            items?.let { adapter.setItems(it) }

            sumActual.text=actualSum(items)
            sumPlaned.text=planedSum(items)
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewItemActivity::class.java)
            startActivityForResult(intent, newItemActivityRequestCode)
        }
    }

    private fun actualSum(items: List<Item>?): String {
        val sum =if (items!=null) {
            items.sumBy { if (it.active) it.amount*it.unitPriceCent  else 0}
        } else 0

        return Numbers.amountAsEuro(sum)
    }

    private fun planedSum(items: List<Item>?): String {
        val sum =if (items!=null) {
            items.sumBy { it.amount*it.unitPriceCent }
        } else 0

        return Numbers.amountAsEuro(sum)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newItemActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val name = data.getStringExtra(NewItemActivity.EXTRA_REPLY_NAME)
                val price = data.getDoubleExtra(NewItemActivity.EXTRA_REPLY_PRICE, 0.0)
                val word = Item(
                    name = name,
                    unitPriceCent = (price * 100).toInt(),
                    amount = 1
                )
                itemViewModel.insert(word)
            }
        } else {
            if (requestCode == editItemActivityRequestCode && resultCode == Activity.RESULT_OK) {
                intentData?.let { data ->
                    val id = data.getIntExtra(EditItemActivity.EXTRA_REPLY_ID, -1)
                    val name = data.getStringExtra(EditItemActivity.EXTRA_REPLY_NAME)
                    val price = data.getDoubleExtra(EditItemActivity.EXTRA_REPLY_PRICE, 0.0)
                    if (id!=-1) {
                        itemViewModel.updateNameAndPrice(id, name, (price * 100).toInt())
                    }
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
