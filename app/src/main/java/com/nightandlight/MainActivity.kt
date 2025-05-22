package com.nightandlight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nightandlight.ui.theme.NightAndLightTheme
import com.nightandlight.ui.theme.lightColor
import com.nightandlight.ui.theme.nightColor
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // State to track current theme
            var isDarkMode by remember { mutableStateOf(false) }

            // Applies dynamic theme
            NightAndLightTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        ThemeToggleSwitch(
                            isDarkMode = isDarkMode,
                            onThemeChanged = { isDarkMode = it }
                        )
                    }

                }
            }
        }
    }
}


@Composable
fun ThemeToggleSwitch(
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    // Controls animation state
    var isAnimating by remember { mutableStateOf(false) }

    // Image resources
    val sunImageResId = R.drawable.ic_sun
    val moonImageResId = R.drawable.ic_moon
    val cloudImageResId = R.drawable.ic_cloud
    val starImageResId = R.drawable.ic_stars

    val durationMillis = 800

    // Offset values for sliding effect
    val rightPosition = (-18).dp
    val leftPosition = 18.dp


    // Main icon position animation
    val mainIconOffset by animateDpAsState(
        targetValue = if (isDarkMode) leftPosition else rightPosition,
        animationSpec = tween(durationMillis)
    )

    // Background icon position animation
    val backgroundIconOffset by animateDpAsState(
        targetValue = if (isDarkMode) rightPosition else leftPosition,
        animationSpec = tween(durationMillis)
    )

    // Rotation animation for icon
    val animatedRotation by animateFloatAsState(
        targetValue = if (isDarkMode) 0f else 360f,
        animationSpec = tween(durationMillis)
    )

    // Background color animation
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isDarkMode) nightColor else lightColor,
        animationSpec = tween(durationMillis, easing = LinearOutSlowInEasing)
    )

    // Automatically reset animation state after the duration
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(durationMillis.toLong())
            isAnimating = false
        }
    }

    // Button with animated content
    Button(
        onClick = {
            if (!isAnimating) {
                isAnimating = true
                onThemeChanged(!isDarkMode)
            }
        },
        modifier = Modifier
            .width(80.dp)
            .height(40.dp),
        contentPadding = PaddingValues(
            horizontal = 5.dp,
            vertical = 6.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedBackgroundColor,
            disabledContainerColor = animatedBackgroundColor
        ),
        enabled = !isAnimating,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            // Fading background icon (e.g., stars or clouds)
            Image(
                painter = painterResource(
                    if (isDarkMode) starImageResId
                    else cloudImageResId
                ),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = backgroundIconOffset)
                    .alpha(if (isAnimating) 0f else 1f)

            )
            // Rotating foreground icon (e.g., sun or moon)
            Image(
                painter = painterResource(
                    if (isDarkMode) moonImageResId
                    else sunImageResId),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = mainIconOffset)
                    .rotate(animatedRotation)
            )


        }
    }
}


@Preview(showBackground = true)
@Composable
fun ThemeTogglePreview() {
    var isDarkMode by remember { mutableStateOf(false) }

    ThemeToggleSwitch(
        isDarkMode = isDarkMode, onThemeChanged = { isDarkMode = it }
    )
}
