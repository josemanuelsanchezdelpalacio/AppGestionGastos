package com.dam2jms.appgestiongastos.auxiliar

import androidx.lifecycle.ViewModel
import com.dam2jms.appgestiongastos.states.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class AuthViewModel: ViewModel(){

    private val _uiState = MutableStateFlow(UiState())
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
}