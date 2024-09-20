package com.dam2jms.appgestiongastos.models

import android.content.Context
import android.widget.Toast
import com.dam2jms.appgestiongastos.auxiliar.AuthViewModel
import com.dam2jms.appgestiongastos.utils.Validaciones.validaContraseña
import com.dam2jms.appgestiongastos.utils.Validaciones.validarCorreo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterViewModel : AuthViewModel() {

    /**
     * Permite crear un nuevo usuario en Firebase y crear un documento en Firestore
     * @param email correo electronico del nuevo usuario
     * @param password contraseña del nuevo usuario
     * @param context contexto necesario para los avisos dentro del Toast
     */
    fun registrarUsuario(email: String, password: String, context: Context){

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

        //verifico si el correo ya esta registrado en firebase
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        Toast.makeText(context, "El correo no esta registrado. Utilize un correo existente", Toast.LENGTH_SHORT).show()
                    } else {
                        //creo el nuevo usuario en Firebase si el correo es valido
                        crearUsuarioFirebase(email, password, context)
                    }
                }else {
                    Toast.makeText(context, "Error verificando el correo", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * metodo para crear un nuevo usuario en firebase y guardo sus datos en firestore
     * @param email correo electronico del nuevo usuario
     * @param password contraseña del nuevo usuario
     * @param context contexto necesario para los avisos **/
    private fun crearUsuarioFirebase(email: String, password: String, context: Context){

        //creo el usuario en Firebase Authetication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){

                    //obtengo el ID del usuario autenticado
                    val userId = auth.currentUser?.uid?: return@addOnCompleteListener

                    //creo el documento en firestore
                    val db = Firebase.firestore
                    val userMap = hashMapOf(
                        "email" to email,
                        "userId" to userId,
                        "createdAt" to System.currentTimeMillis() //fecha de creacion
                    )

                    db.collection("users")
                        .document()
                        .set(userMap) //creo el documento con los datos del usuario
                        .addOnSuccessListener {
                            Toast.makeText(context, "Usuario registrado y guardado en FireStore", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al guardar el usuario en FireStore", Toast.LENGTH_SHORT).show()
                        }
                }else{
                    Toast.makeText(context, "Error al crear el usuario", Toast.LENGTH_LONG).show()
                }
            }
    }
}
