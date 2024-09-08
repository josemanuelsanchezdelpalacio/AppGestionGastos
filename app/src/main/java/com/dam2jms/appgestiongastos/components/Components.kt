package com.dam2jms.appgestiongastos.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.screens.HistoryScreen
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.Gris
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.dam2jms.appgestiongastos.R
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import com.dam2jms.appgestiongastos.ui.theme.Negro

@RequiresApi(Build.VERSION_CODES.O)
object Components {

    /**metodo con el fondo para las pantallas LoginScreen y HomeScreen**/
    @Composable
    fun fondoPantalla(contenido: @Composable() (BoxScope.() -> Unit)){

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    //creo un gradiente naranja claro y oscuro
                    brush = Brush.verticalGradient(
                        colors = listOf(NaranjaClaro, NaranjaOscuro),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                ),
            content = contenido
        )
    }

    /**metodo para reutilizar el menu*/
    @Composable
    fun menu(navController: NavController) {
        ModalDrawerSheet {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NaranjaClaro)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Image(
                        painter = painterResource(id = R.drawable.imagen_logo),
                        contentDescription = "icono app",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }

                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Blanco,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Divider(color = Blanco)
                Spacer(modifier = Modifier.height(24.dp))

                NavigationDrawerItem(
                    label = { Text(text = "Volver al inicio", color = Negro) },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Inicio", tint = Negro)},
                    selected = false,
                    onClick = { navController.navigate(AppScreen.HomeScreen.route) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Blanco)
                Spacer(modifier = Modifier.height(16.dp))


                NavigationDrawerItem(
                    label = { Text(text = "Añadir transacciones", color = Negro) },
                    icon = { Icon(imageVector = Icons.Default.Money, contentDescription = "Gastos", tint = Negro)},
                    selected = false,
                    onClick = {
                        val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                        navController.navigate(AppScreen.TransactionScreen.createRoute(currentDate))
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Blanco)
                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text(text = "Historial de transacciones", color = Color.Black) },
                    icon = { Icon(imageVector = Icons.Default.History, contentDescription = "Gastos", tint = Negro)},
                    selected = false,
                    onClick = { navController.navigate(AppScreen.HistoryScreen.route) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Blanco)
            }
        }
    }

    /** metodo para reutilizar Textfield
     * Usado para las clases LoginScreen, HomeScreen
     * @param isPasswordVisible indica si la contraseña es visible
     * */
    @Composable
    fun AuthTextField(label: String, text: String, onTextChange: (String) -> Unit, isPasswordField: Boolean = false, isPasswordVisible: Boolean = false, onPasswordVisibilityChange: (() -> Unit)? = null, leadingIcon: @Composable (() -> Unit)? = null, modifier: Modifier) {

        var passwordVisible by remember { mutableStateOf(isPasswordVisible) }

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text(text = label) },
            //para que solo pueda escribir en una linea
            singleLine = true,
            modifier = modifier,
            //para que la contraseña salga tapado con simbolos *
            visualTransformation = if (isPasswordField && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = leadingIcon,
            trailingIcon =
            if (isPasswordField) {
                {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                        onPasswordVisibilityChange?.invoke()
                    }) {
                        Icon(imageVector = icon, contentDescription = if (passwordVisible) "contraseña escondida" else "contraseña mostrada")
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isPasswordField) KeyboardType.Password else KeyboardType.Email
            )
        )
    }


    /**metodo para reutilizar el componente de radioButton
     * Utilizado en las clases TransactionScreen, AddTransactionScreen e HistoryScreen*/
    @Composable
    fun AuthRadioButton(seleccion: Boolean, onClick: () -> Unit, label: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            RadioButton(
                selected = seleccion,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (seleccion) NaranjaClaro else Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
