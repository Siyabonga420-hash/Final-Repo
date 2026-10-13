package com.example.rentmate

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

// ----------------------------------------------------
// RENTMATE COLORS
// ----------------------------------------------------

private val BgLight = Color(0xFFF4F5F7)
private val CardBackground = Color(0xFFFFFFFF)
private val Orange = Color(0xFFF56038)
private val Pink = Color(0xFFEC4899)
private val TextMain = Color(0xFF2D3142)
private val TextSub = Color(0xFF8D99AE)
private val DarkBorder = Color(0xFF2D3142)

// ----------------------------------------------------
// FEATURE DATA
// ----------------------------------------------------

data class WelcomeFeature(
    val name: String,
    val icon: String
)

private val welcomeFeatures = listOf(
    WelcomeFeature("Calendar", "📅"),
    WelcomeFeature("Utilities", "💡"),
    WelcomeFeature("Moments", "❤️"),
    WelcomeFeature("Chores", "✓"),
    WelcomeFeature("Payments", "💳"),
    WelcomeFeature("Waste", "🗑"),
    WelcomeFeature("Shopping", "🛒"),
    WelcomeFeature("Maintenance", "🔧")
)

// ----------------------------------------------------
// WELCOME SCREEN
// ----------------------------------------------------

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {

    var selectedFeature by remember {
        mutableStateOf<WelcomeFeature?>(null)
    }

    val currentDate = SimpleDateFormat(
        "EEE, dd MMM",
        Locale.getDefault()
    ).format(Date())

    val currentTime = SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(Date())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BgLight,
                    shape = RoundedCornerShape(40.dp)
                )
                .border(
                    width = 3.dp,
                    color = DarkBorder,
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ------------------------------------------------
            // DATE AND TIME
            // ------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = currentDate,
                        color = TextMain,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = currentTime,
                        color = TextSub,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "⚡",
                    fontSize = 24.sp
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ------------------------------------------------
            // RENTMATE BRAND
            // ------------------------------------------------

            Text(
                text = "RentMate",
                color = TextMain,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Live together,",
                color = TextSub,
                fontSize = 18.sp
            )

            Text(
                text = "Do BETTER.",
                color = Orange,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ------------------------------------------------
            // ANIMATED FEATURE CIRCLE
            // ------------------------------------------------

            FeatureOrbit(
                features = welcomeFeatures,
                onFeatureClick = { feature ->
                    selectedFeature = feature
                }
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // ------------------------------------------------
            // GET STARTED
            // ------------------------------------------------

            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange
                )
            ) {

                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // ------------------------------------------------
            // LOGIN
            // ------------------------------------------------

            TextButton(
                onClick = onLoginClick
            ) {

                Text(
                    text = "Already have an account? Log In",
                    color = Pink,
                    fontSize = 14.sp
                )
            }
        }
    }

    // ----------------------------------------------------
    // FEATURE DIALOG
    // ----------------------------------------------------

    selectedFeature?.let { feature ->

        AlertDialog(
            onDismissRequest = {
                selectedFeature = null
            },

            title = {

                Text(
                    text = "${feature.icon}  ${feature.name}",
                    color = TextMain,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {

                Text(
                    text = "The ${feature.name} module helps you automate household organization, share costs, and manage tasks seamlessly in RentMate.",
                    color = TextSub
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        selectedFeature = null

                        onGetStartedClick()
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange
                    )
                ) {

                    Text(
                        text = "Sign Up to Access"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        selectedFeature = null
                    }
                ) {

                    Text(
                        text = "Close"
                    )
                }
            }
        )
    }
}

// ----------------------------------------------------
// FEATURE ORBIT
// ----------------------------------------------------

@Composable
fun FeatureOrbit(
    features: List<WelcomeFeature>,
    onFeatureClick: (WelcomeFeature) -> Unit
) {

    // ------------------------------------------------
    // CONTINUOUS ROTATION
    // ------------------------------------------------

    val infiniteTransition = rememberInfiniteTransition(
        label = "RentMateFeatureOrbit"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 18000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitRotation"
    )

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {

        // ------------------------------------------------
        // CENTER CIRCLE
        // ------------------------------------------------

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(CardBackground)
                .border(
                    width = 3.dp,
                    color = Orange,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "🏠",
                    fontSize = 38.sp
                )

                Text(
                    text = "RentMate",
                    color = TextMain,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ------------------------------------------------
        // ROTATING FEATURE CARDS
        // ------------------------------------------------

        features.forEachIndexed { index, feature ->

            /*
             * Each card has its own starting position.
             *
             * The animated rotation is added to that
             * starting position so every card moves
             * around the center circle.
             */

            val startingAngle =
                (index * 360f / features.size) - 90f

            val angle =
                Math.toRadians(
                    (startingAngle + rotation).toDouble()
                )

            val radius = 105.0

            val x =
                cos(angle) * radius

            val y =
                sin(angle) * radius

            FeatureCard(
                feature = feature,

                modifier = Modifier.offset(
                    x = x.toFloat().dp,
                    y = y.toFloat().dp
                ),

                onClick = {
                    onFeatureClick(feature)
                }
            )
        }
    }
}

// ----------------------------------------------------
// FEATURE CARD
// ----------------------------------------------------

@Composable
fun FeatureCard(
    feature: WelcomeFeature,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .size(72.dp)
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(
                CardBackground
            )
            .border(
                width = 1.5.dp,
                color = Pink,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            },

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = feature.icon,
                fontSize = 22.sp
            )

            Text(
                text = feature.name,
                color = TextMain,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ----------------------------------------------------
// PREVIEW
// ----------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "RentMate Welcome Screen"
)
@Composable
fun WelcomeScreenPreview() {

    MaterialTheme {

        WelcomeScreen(
            onGetStartedClick = {},
            onLoginClick = {}
        )
    }
}

