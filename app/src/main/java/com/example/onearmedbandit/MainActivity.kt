package com.example.onearmedbandit

import android.os.Bundle
import android.text.style.BackgroundColorSpan
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onearmedbandit.ui.theme.OneArmedBanditTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OneArmedBanditTheme {
                OneArmBanditApp()
            }
        }
    }
}
@Preview
@Composable
fun OneArmBanditApp() {

    val images = remember { mutableStateListOf(R.drawable.leaf, R.drawable.diamond, R.drawable.question_mark, R.drawable.grapes) }

    var spinResult by remember { mutableStateOf("") }

    var creditsToPlay by remember { mutableStateOf(200) }

    var spinnedImages = remember { mutableStateListOf(R.drawable.diamond, R.drawable.question_mark, R.drawable.grapes) }

    var blurredImages = remember { mutableStateListOf(R.drawable.diamond_blured, R.drawable.leaf_blured, R.drawable.grapes_blured) }

    var snackbarVisible by remember { mutableStateOf(false) }

    var isSpinning by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Credits Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("One-Arm Bandit", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Credits: $creditsToPlay", fontSize = 18.sp)
        }

        // Slot Machine Images with border, shadow, and blur effect
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 3) {
                SlotImageWithBorderAndShadow(imageResource = if (isSpinning) blurredImages[i] else spinnedImages[i], isSpinning = isSpinning)
            }
        }

    // Spin Result
        Text(
            text = spinResult,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (spinResult == "Winner") Color.Green else Color.Red,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        // Spin Button
        Button(
            onClick = {
                if (!isSpinning && creditsToPlay >= 20) {
                    isSpinning = true
                    creditsToPlay -= 20
                    spinResult = ""

                    coroutineScope.launch {
                        // Simulate a delay of 2 seconds
                        delay(2000)

                        // Stop spinning
                        isSpinning = false

                        // Randomly choose new images
                        val randomImages = List(3) { images.random() }
                        spinnedImages.clear()
                        spinnedImages.addAll(randomImages)

                        // Check for a win
                        if (randomImages.distinct().size == 1) {
                            spinResult = "Winner"
                            creditsToPlay += 60
                        } else {
                            spinResult = "Bad Luck"
                        }
                    }
                }
                if (creditsToPlay < 20) {
                    snackbarVisible = true
                }
            },
            enabled = !isSpinning && creditsToPlay >= 20,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text("Spin: 20 Credits")
        }
    }

    if (snackbarVisible) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Gray)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Not enough credits to spin.",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { snackbarVisible = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Dismiss", color = Color.White)
                }
            }
        }
    }
}


@Composable
fun SlotImageWithBorderAndShadow(imageResource: Int, isSpinning: Boolean) {
    val contentDescription = null

    val modifier = Modifier
        .size(120.dp)
        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
        .shadow(4.dp, shape = RoundedCornerShape(4.dp))
        .then(
            if (isSpinning) Modifier.graphicsLayer(alpha = 0.3f) else Modifier
        )

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}



@Composable
fun SlotImage(imageResource: Int) {
    Image(
        painter = painterResource(imageResource),
        contentDescription = null,
        modifier = Modifier.size(100.dp)
    )
}

