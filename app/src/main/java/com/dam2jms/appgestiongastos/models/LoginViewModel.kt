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

class LoginViewModel: ViewModel(){

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val auth = Firebase.auth

    /**Actualiza los outtextfield con la informacion que ponga el usuario*/
    fun onChange(email: String, password: String){
        _uiState.update { it.copy(email = email, password = password) }
    }

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

    /**Permite iniciar sesion con un usuario que ya exista en firebase*/
    fun iniciarSesion(email: String, password: String, context: Context){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(context, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(context, "Error al iniciar sesion", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**Permite recuperar la contraseña de un usuario a traves de un correo*/
    fun recuperarContraseña(email: String, context: Context){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(context, "correo de recuperacion enviado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "error al enviar correo de recuperacion", Toast.LENGTH_SHORT).show()
                }
            }

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