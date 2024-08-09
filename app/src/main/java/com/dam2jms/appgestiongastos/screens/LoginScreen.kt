package com.dam2jms.appgestiongastos.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.Components.fondo
import com.dam2jms.appgestiongastos.models.LoginViewModel
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, mvvm: LoginViewModel){
    val uiState by mvvm.uiState.collectAsState()

    LaunchedEffect(uiState.sesionIniciada) {
        if(uiState.sesionIniciada){
            navController.navigate(AppScreen.HomeScreen.route)
        }
    }

    Scaffold(
       topBar = {
           CenterAlignedTopAppBar(title = { Text(text = "GESTION GASTOS", color = Blanco)},
               colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                   containerColor = NaranjaOscuro,
                   titleContentColor = NaranjaClaro
               )
           )
       }
    ) {
        paddingValues ->

        //llamo al componente para el diseño del fondo
        fondo {
            LoginScreenBody(paddingValues = paddingValues, navController = navController, mvvm = mvvm, uiState = uiState)
        }
    }
}

@Composable
fun LoginScreenBody(paddingValues: PaddingValues, navController: NavController, mvvm: LoginViewModel, uiState: UiState){

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            color = Blanco,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = {mvvm.onChange(it, uiState.password)},
            label = { Text(text = "Correo electronico", color = Blanco)},
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxSize(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "icono correo"
                )
            },
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { mvvm.onChange(uiState.email, it)},
            label = { Text(text = "Contraseña", color = Blanco)},
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxSize(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = if (uiState.visibilidadPassword) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "icono contraseña"
                )
            },
            //icono para mostrar y ocultar la contraseña
            trailingIcon = {
                val icono =
                    if (uiState.visibilidadPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { mvvm.visibilidadContraseña() }) {
                    Icon(
                        imageVector = icono,
                        contentDescription = "visibilidad password",
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        //boton para iniciar sesion si ningun campo esta vacio y si las validaciones son correctas
        Button(onClick = {
            if (uiState.email.isNotEmpty() && uiState.password.isNotEmpty() && mvvm.validarCorreo(context, uiState.email) && mvvm.validaContraseña(context, uiState.password)) {
                mvvm.iniciarSesion(uiState.email, uiState.password, context)
            } else {
                Toast.makeText(context, "Ningun campo puede estar vacio", Toast.LENGTH_SHORT).show()
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blanco)
        ) {
            Text(text = "Iniciar sesion")
        }

        Spacer(modifier = Modifier.height(16.dp))

        //texto clickable para registrar un usuario si ningun campo esta vacio y si todas las validaciones son correctas
        TextButton(onClick = {
            if (uiState.email.isNotEmpty() && uiState.password.isNotEmpty() && mvvm.validarCorreo(context, uiState.email) && mvvm.validaContraseña(context, uiState.password)) {
                mvvm.registrarUsuario(uiState.email, uiState.password, context)
            } else {
                Toast.makeText(context, "Ningun campo puede estar vacio", Toast.LENGTH_SHORT).show()
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = Blanco)
        ) {
            Text(text = "Registrarse")
        }

        //texto clickable para recuperar la contraseña
        Text(
            text = "¿Olvidaste tu contraseña?",
            color = White,
            modifier = Modifier.clickable {
                if (uiState.email.isNotEmpty() && mvvm.validarCorreo(context, uiState.email)) {
                    mvvm.recuperarContraseña(uiState.email, context)
                } else {
                    Toast.makeText(context, "Ingrese un correo electronico valido", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

}