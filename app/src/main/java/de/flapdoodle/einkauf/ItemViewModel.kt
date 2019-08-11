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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * View Model to keep a reference to the word repository and
 * an up-to-date list of all words.
 */

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ItemRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allItems: LiveData<List<Item>>

    init {
        val itemsDao = ItemDatabase.getDatabase(application, viewModelScope).itemDao()
        repository = ItemRepository(itemsDao)
        allItems = repository.allItems
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(item: Item) = viewModelScope.launch {
        repository.insert(item)
    }

    fun updateAmount(item: Item, amount: Int) {
        Executors.newSingleThreadExecutor().execute {
            repository.updatedAmount(item.id!!, amount)
        }
    }

    fun updateActive(item: Item, active: Boolean) {
        Executors.newSingleThreadExecutor().execute {
            repository.updateActive(item.id!!, active)
        }
    }

    fun delete(item: Item) {
        Executors.newSingleThreadExecutor().execute {
            repository.delete(item.id!!)
        }
    }

    fun updateNameAndPrice(id: Int, name: String, price: Int) = viewModelScope.launch {
        Executors.newSingleThreadExecutor().execute {
            repository.updateNameAndPrice(id, name, price)
        }
    }
}
