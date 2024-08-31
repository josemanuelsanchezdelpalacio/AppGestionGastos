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
import com.dam2jms.appgestiongastos.ui.theme.Negro


object Components {

    @Composable
    fun fondo(content: @Composable() (BoxScope.() -> Unit)){

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFA726), Color(0xFFD35400)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                ),
            content = content
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                    onClick = {
                        navController.navigate(AppScreen.HomeScreen.route)
                    },
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
                    onClick = {
                        navController.navigate(AppScreen.HistoryScreen.route)
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Blanco)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun horizontalCalendar(fechaSeleccionada: LocalDate, onDateSelected: (LocalDate) -> Unit) {

        val fechas = remember{
            (0..30).map { LocalDate.now().minusDays(it.toLong()) }
        }

        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fechas) { fecha ->
                val seleccionada = fecha == fechaSeleccionada
                val background = if(seleccionada) MaterialTheme.colorScheme.primary else Color.Transparent
                val textColor = if(seleccionada) Blanco else MaterialTheme.colorScheme.onBackground

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(background, shape = CircleShape)
                        .border(1.dp, if (seleccionada) Blanco else Gris, shape = CircleShape)
                        .clickable { onDateSelected(fecha) },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = fecha.dayOfMonth.toString(),
                        color = textColor,
                        fontWeight = if(seleccionada) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }

    @Composable
    fun AuthTextField(
        label: String,
        text: String,
        onTextChange: (String) -> Unit,
        isPasswordField: Boolean = false,
        isPasswordVisible: Boolean = false,
        onPasswordVisibilityChange: (() -> Unit)? = null,
        leadingIcon: @Composable (() -> Unit)? = null,
        modifier: Modifier = Modifier
    ) {

        var passwordVisible by remember { mutableStateOf(isPasswordVisible) }

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text(text = label) },
            singleLine = true,
            modifier = modifier,
            visualTransformation = if (isPasswordField && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = leadingIcon,
            trailingIcon = if (isPasswordField) {
                {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                        onPasswordVisibilityChange?.invoke()
                    }) {
                        Icon(imageVector = icon, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isPasswordField) KeyboardType.Password else KeyboardType.Email
            )
        )
    }

    @Composable
    fun RadioButtonWithLabel(selected: Boolean, onClick: () -> Unit, label: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) NaranjaClaro else Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

}