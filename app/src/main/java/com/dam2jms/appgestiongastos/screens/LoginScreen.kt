package com.dam2jms.appgestiongastos.screens

import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.R
import com.dam2jms.appgestiongastos.components.ScreenComponents.fondoPantalla
import com.dam2jms.appgestiongastos.models.LoginViewModel
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import com.dam2jms.appgestiongastos.utils.Validaciones.validaContraseña
import com.dam2jms.appgestiongastos.utils.Validaciones.validarCorreo

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, mvvm: LoginViewModel){

    val uiState by mvvm.uiState.collectAsState()
    val context = LocalContext.current

    //para navegar a HomeScreen si la sesion
    LaunchedEffect(uiState.sesionIniciada) {
        if(uiState.sesionIniciada){
            navController.navigate(AppScreen.HomeScreen.route)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "GESTION DE GASTOS", color = Blanco, fontWeight = FontWeight.Bold)},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NaranjaOscuro,
                    titleContentColor = Blanco
                )
            )
        }
    ) { paddingValues ->
        //llamo al componente para el diseño del fondo
        fondoPantalla {
            LoginScreenBody(paddingValues = paddingValues, navController = navController, mvvm = mvvm, uiState = uiState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreenBody(paddingValues: PaddingValues, navController: NavController, mvvm: LoginViewModel, uiState: UiState) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.imagen_logo),
            contentDescription = "logo app",
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Blanco)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            color = Blanco,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { mvvm.onChange(it, uiState.password) },
            label = { Text("Correo electronico") },
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Correo electronico") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Blanco,
                unfocusedBorderColor = Blanco.copy(0.7f),
                focusedLabelColor = Blanco,
                unfocusedLabelColor = Blanco.copy(0.7f),
                containerColor = Blanco
            )
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { mvvm.onChange(uiState.email, it) },
            label = { Text("Contraseña") },
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "Contraseña") },
            visualTransformation = if(uiState.visibilidadPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icono = if(uiState.visibilidadPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { mvvm.visibilidadContraseña() }) {
                    Icon(imageVector = icono, contentDescription = if(uiState.visibilidadPassword) "Ocultar contraseña" else "Mostrar contraseña")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Blanco,
                unfocusedBorderColor = Blanco.copy(0.7f),
                focusedLabelColor = Blanco,
                unfocusedLabelColor = Blanco.copy(0.7f),
                containerColor = Blanco
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (uiState.email.isNotEmpty() && uiState.password.isNotEmpty() && validarCorreo(context, uiState.email) && validaContraseña(context, uiState.password)) {
                    mvvm.iniciarSesion(uiState.email, uiState.password, context)
                    navController.navigate(AppScreen.HomeScreen.route)
                } else {
                    Toast.makeText(context, "Ningún campo puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blanco)
        ) {
            Text(text = "Iniciar sesión", color = NaranjaOscuro, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate(AppScreen.RegisterScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "¿No tienes cuenta? Registrate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = Blanco,
            modifier = Modifier.clickable {
                if (uiState.email.isNotEmpty() && validarCorreo(context, uiState.email)) {
                    mvvm.recuperarContraseña(uiState.email, context)
                } else {
                    Toast.makeText(context, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}




