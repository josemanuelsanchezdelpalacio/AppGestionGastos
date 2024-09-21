package com.dam2jms.appgestiongastos.screens

import android.os.Build
import android.widget.Space
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.R
import com.dam2jms.appgestiongastos.components.ScreenComponents.fondoPantalla
import com.dam2jms.appgestiongastos.models.RegisterViewModel
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
fun RegisterScreen(navController: NavController, mvvm: RegisterViewModel){

    val uiState by mvvm.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "GESTION DE GASTOS", color = Blanco, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "atras", tint = Blanco)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NaranjaOscuro,
                    titleContentColor = NaranjaClaro
                )
            )
        }
    ) {
        paddingValues ->
        //llamo al componente para el diseño del fondo
        fondoPantalla{
            RegisterBodyScreen(paddingValues = paddingValues, navController = navController, mvvm = mvvm, uiState = uiState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterBodyScreen(paddingValues: PaddingValues, navController: NavController, mvvm: RegisterViewModel, uiState: UiState){

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
            contentDescription = "icono app",
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Blanco)
                .padding(16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Crear cuenta",
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
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { mvvm.onChange(uiState.email, it) },
            label = { Text("Contraseña") },
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "Contraseña") },
            visualTransformation = if(uiState.visibilidadPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icono = if(uiState.visibilidadPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(
                    onClick = {
                        uiState.visibilidadPassword != uiState.visibilidadPassword
                        mvvm.visibilidadContraseña()
                    }
                ) {
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
                    mvvm.registrarUsuario(uiState.email, uiState.password, context)
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
            Text(text = "Registrarse", color = NaranjaOscuro, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate(AppScreen.LoginScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "¿Ya tienes una cuenta? Inicia sesion", color = Blanco)
        }
    }

}




