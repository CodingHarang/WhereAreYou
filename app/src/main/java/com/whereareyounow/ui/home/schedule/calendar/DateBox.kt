package com.whereareyounow.ui.home.schedule.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.whereareyounow.ui.theme.lato

@Composable
fun DateBox(
    date: Int,
    scheduleCount: Int,
    isSelected: Boolean = false,
    textColor: Color
) {
    var componentWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                componentWidth = (it.size.width / density.density).toInt()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width((componentWidth * 0.5).dp)
                .height((componentWidth * 0.5).dp)
                .background(
                    color = when (isSelected) {
                        true -> Color(0xFF1D1A7D)
                        false -> Color(0x00000000)
                    },
                    shape = RoundedCornerShape(25)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.toString(),
                fontWeight = FontWeight.Bold,
                color = when (isSelected) {
                    true -> Color.White
                    false -> textColor
                },
                fontFamily = lato
            )
        }
        Spacer(Modifier.size(2.dp))
        Column(
            modifier = Modifier
                .width((componentWidth * 0.3).dp)
                .height((componentWidth * 0.2).dp)
        ) {
            for (i in 0..1) {
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..2) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (i * 3 + j < scheduleCount) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(
                                            shape = CircleShape,
                                            color = Color(0xFF8C9EFF)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DateBoxPreview() {
    Column() {
        Row() {
            Box(
                modifier = Modifier
                    .size(50.dp)
            ) {
                DateBox(
                    date = 2,
                    scheduleCount = 1,
                    isSelected = false,
                    textColor = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
            ) {
                DateBox(
                    date = 20,
                    scheduleCount = 3,
                    isSelected = false,
                    textColor = Color.Black
                )
            }
        }
        Row() {
            Box(
                modifier = Modifier
                    .size(50.dp)
            ) {
                DateBox(
                    date = 20,
                    scheduleCount = 4,
                    isSelected = false,
                    textColor = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
            ) {
                DateBox(
                    date = 20,
                    scheduleCount = 6,
                    isSelected = true,
                    textColor = Color.White
                )
            }
        }
    }
}