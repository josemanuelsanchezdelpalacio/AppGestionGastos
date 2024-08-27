package com.dam2jms.appgestiongastos.models

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.dam2jms.appgestiongastos.states.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class AuthViewModel: ViewModel(){

    protected val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    protected val auth: FirebaseAuth = FirebaseAuth.getInstance()


    /**Actualiza los outtextfield con la informacion que ponga el usuario*/
    fun onChange(email: String, password: String){
        _uiState.update { it.copy(email = email, password = password) }
    }


    /**Permite cambiar la visibilidad de la contraseña pulsando en el icono */
    fun visibilidadContraseña(){
        _uiState.value = _uiState.value.copy(visibilidadPassword = !uiState.value.visibilidadPassword)
    }

    /**Valida el correo electronico para que tenga la estructura de un email valido*/
    fun validarCorreo(context: Context, email: String): Boolean {
        val patronEmail = Patterns.EMAIL_ADDRESS
        return if(email.isNotEmpty() && patronEmail.matcher(email).matches()) {
            true
        }else{
            Toast.makeText(context, "Ingrese un correo electronico valido", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**Valida la contraseña para que tenga como minimo 6 caracteres (el minimo para firebase)*/
    fun validaContraseña(context: Context, password: String): Boolean {
        return if(password.length >= 6){
            true
        }else{
            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            false
        }
    }

}