package com.dam2jms.appgestiongastos.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.data.CategoriaAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel() : ViewModel() {

    private val _categories = MutableStateFlow<List<Categoria>>(emptyList())
    val categories: StateFlow<List<Categoria>> = _categories


    fun obtenerCategorias(tipo: String) {

        viewModelScope.launch {
            val categorias = CategoriaAPI.obtenerCategorias(tipo)
            _categories.value = categorias
        }
    }

}





