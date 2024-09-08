package com.dam2jms.appgestiongastos.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.Negro


object FormComponents {

    /**
     * metodo para reutilizar un campo de texto
     *
     * @param label texto que se mostrara como etiqueta del campo
     * @param text el texto actual que sera mostrado dentro del textfield
     * @param onTextChange lambda que sera llamada cuando el texto cambie
     * @param isPasswordField si el campo es para contraseña se muestra el icono de visibilidad
     * @param isPasswordVisible si la contraseña es visible o no
     * @param onPasswordVisibilityChange lambda que sera llamada cuando se cambie la visibilidad de la contraseña
     * @param leadingIcon icono de visibilidad de la contraseña
     * @param modifier modificador de diseño para el textfield
     * */
    @Composable
    fun AuthTextField(
        label: String,
        text: String,
        onTextChange: (String) -> Unit,
        isPasswordField: Boolean = false,
        isPasswordVisible: Boolean = false,
        onPasswordVisibilityChange: (() -> Unit)? = null,
        leadingIcon: @Composable (() -> Unit)? = null,
        modifier: Modifier
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
                        Icon(imageVector = icon, contentDescription = if (passwordVisible) "Contraseña visible" else "Contraseña oculta")
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isPasswordField) KeyboardType.Password else KeyboardType.Email
            )
        )
    }

    /**
     * metodo para reutilizar radiobuttton
     *
     * @param seleccion si el boton de radio esta seleccionado
     * @param onClick lambda que sera invocada cuando se pulse el radiobutton
     * @param label el texto que se muestra al lado del radiobutton
     * **/
    @Composable
    fun AuthRadioButton(seleccion: Boolean, onClick: () -> Unit, label: String){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onClick)
        ){
            RadioButton(
                selected = seleccion,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if(seleccion) NaranjaClaro else Negro,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

