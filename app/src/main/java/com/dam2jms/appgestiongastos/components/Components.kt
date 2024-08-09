package com.dam2jms.appgestiongastos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

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
    
}