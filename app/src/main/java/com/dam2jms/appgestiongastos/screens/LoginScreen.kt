package com.dam2jms.appgestiongastos.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.Components.AuthTextField
import com.dam2jms.appgestiongastos.components.Components.fondo
import com.dam2jms.appgestiongastos.models.LoginViewModel
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, mvvm: LoginViewModel){
    val uiState by mvvm.uiState.collectAsState()

    //para navegar a HomeScreen si la sesion
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenBody(paddingValues: PaddingValues, navController: NavController, mvvm: LoginViewModel, uiState: UiState) {
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            color = Blanco,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        AuthTextField(
            label = "Correo electronico",
            text = uiState.email,
            onTextChange = {mvvm.onChange(it, uiState.password)},
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Correo electronico")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        AuthTextField(
            label = "Contraseña",
            text = uiState.password,
            onTextChange = {mvvm.onChange(uiState.email, it)},
            isPasswordField = true,
            isPasswordVisible = uiState.visibilidadPassword,
            onPasswordVisibilityChange = { mvvm.visibilidadContraseña()},
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Contraseña")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (uiState.email.isNotEmpty() && uiState.password.isNotEmpty() && mvvm.validarCorreo(context, uiState.email) && mvvm.validaContraseña(context, uiState.password)) {
                    mvvm.iniciarSesion(uiState.email, uiState.password, context)
                    navController.navigate(AppScreen.HomeScreen.route)
                } else {
                    Toast.makeText(context, "Ningún campo puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blanco)
        ) {
            Text(text = "Iniciar sesión", color = NaranjaOscuro)
        }

        Spacer(modifier = Modifier.height(16.dp))


        TextButton(
            onClick = {
                navController.navigate(AppScreen.RegisterScreen.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = Blanco)
        ) {
            Text(text = "Registrarse")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = Blanco,
            modifier = Modifier.clickable {
                if (uiState.email.isNotEmpty() && mvvm.validarCorreo(context, uiState.email)) {
                    mvvm.recuperarContraseña(uiState.email, context)
                } else {
                    Toast.makeText(context, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}