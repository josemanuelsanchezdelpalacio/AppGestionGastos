package com.dam2jms.appgestiongastos.models

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.dam2jms.appgestiongastos.states.UiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel: AuthViewModel(){

    /**Permite crear un nuevo usuario en Firebase */
    fun registrarUsuario(email: String, password: String, context: Context){
       auth.createUserWithEmailAndPassword(email, password)
           .addOnCompleteListener { task ->
               if(task.isSuccessful){
                   Toast.makeText(context, "Registro creado", Toast.LENGTH_SHORT).show()
               }else {
                   Toast.makeText(context, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
               }
           }
    }
}

