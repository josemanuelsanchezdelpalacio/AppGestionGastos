package com.dam2jms.appgestiongastos.models

import android.content.Context
import android.credentials.CredentialManager
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.states.UiState
import com.google.android.gms.common.api.Response
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : AuthViewModel() {

    /**Permite iniciar sesion con un usuario que ya exista en firebase*/
    fun iniciarSesion(email: String, password: String, context: Context) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al iniciar sesion", Toast.LENGTH_SHORT).show()
                }
            }
    }


    /**Permite recuperar la contraseña de un usuario a traves de un correo*/
    fun recuperarContraseña(email: String, context: Context) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "correo de recuperacion enviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "error al enviar correo de recuperacion", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
