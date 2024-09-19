package com.dam2jms.appgestiongastos.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.dam2jms.appgestiongastos.R
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import com.dam2jms.appgestiongastos.ui.theme.Negro

@RequiresApi(Build.VERSION_CODES.O)
object ScreenComponents {

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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NaranjaOscuro)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Blanco),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.imagen_logo),
                            contentDescription = "icono app",
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }

                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = NaranjaOscuro,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Divider(color = NaranjaOscuro)
                Spacer(modifier = Modifier.height(24.dp))

                NavigationDrawerItem(
                    label = { Text(text = "Volver al inicio", color = Negro) },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Inicio", tint = Negro)},
                    selected = false,
                    onClick = { navController.navigate(AppScreen.HomeScreen.route) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(NaranjaClaro)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = NaranjaOscuro)
                Spacer(modifier = Modifier.height(16.dp))


                NavigationDrawerItem(
                    label = { Text(text = "Añadir transacciones", color = Negro) },
                    icon = { Icon(imageVector = Icons.Default.Money, contentDescription = "Gastos", tint = Negro)},
                    selected = false,
                    onClick = {
                        val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                        navController.navigate(AppScreen.TransactionScreen.createRoute(currentDate))
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(NaranjaClaro)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = NaranjaOscuro)
                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text(text = "Historial de transacciones", color = Negro) },
                    icon = { Icon(imageVector = Icons.Default.History, contentDescription = "Gastos", tint = Negro)},
                    selected = false,
                    onClick = { navController.navigate(AppScreen.HistoryScreen.route) },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(NaranjaClaro)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = NaranjaOscuro)
            }
        }
    }
