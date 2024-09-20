package com.dam2jms.appgestiongastos.models

import android.content.Context
import android.widget.Toast
import com.dam2jms.appgestiongastos.auxiliar.AuthViewModel
import com.dam2jms.appgestiongastos.utils.Validaciones.validaContraseña
import com.dam2jms.appgestiongastos.utils.Validaciones.validarCorreo

class LoginViewModel : AuthViewModel() {

    /**
     * Permite iniciar sesion con un usuario que ya exista en firebase
     * @param email correo electronico del usuario
     * @param password contraseña del usuario
     * @param context contexto necesario para los avisos dentro del Toast
     * */
    fun iniciarSesion(email: String, password: String, context: Context) {

        //valido el formato del correo electronico
        if(!validarCorreo(context, email)){
            Toast.makeText(context, "Correo electronico no valido", Toast.LENGTH_SHORT).show()
            return
        }

        //valido la longitud de la contraseña
        if(!validaContraseña(context, password)){
            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al iniciar sesion", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Permite recuperar la contraseña de un usuario a traves de un correo
     * @param email correo electronico del usuario para enviar enlace de recuperacion
     * @param context contexto necesario para los avisos dentro del Toast*/
    fun recuperarContraseña(email: String, context: Context) {

        //valido el formato del correo electronico
        if(!validarCorreo(context, email)){
            Toast.makeText(context, "Correo electronico no valido", Toast.LENGTH_SHORT).show()
            return
        }

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
