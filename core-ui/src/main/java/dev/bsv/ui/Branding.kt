package dev.bsv.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.bsv.coreui.R
import dev.bsv.settings.Settings

object BsvBrand {
    const val VERSION = "0.1.0"
    val Background = Color(0xFF101014)
    val Violet = Color(0xFF8B5CF6)
    val Surface = Color(0xFF191920)
}

@Composable
fun BrandLogo(modifier: Modifier = Modifier, decorative: Boolean = false) {
    Image(
        painterResource(R.drawable.bsv_logo),
        contentDescription = if (decorative) null else "BSV",
        modifier = modifier.testTag("brand-logo"),
        contentScale = ContentScale.Fit,
    )
}

@Composable
fun BrandHeader(s: Settings) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BrandLogo(Modifier.width(72.dp).height(36.dp), decorative = true)
        Column(Modifier.weight(1f)) {
            Text(
                "BSV — Brawl Stars Visuals",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "Version ${BsvBrand.VERSION} | Android",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        BoxWithConstraints {
            if (this.maxWidth > 640.dp)
                Text(
                    "● " + s.tr("LOCAL PREVIEW", "ЛОКАЛЬНО"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
        }
    }
}

@Composable
fun BrandBanner(modifier: Modifier = Modifier) {
    Image(
        painterResource(R.drawable.bsv_banner),
        contentDescription = "BSV — BRAWL STARS VISUALS. More Visuals. More Fun.",
        contentScale = ContentScale.Fit,
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(22.dp))
                .testTag("brand-banner"),
    )
}

// The caller shows this only while real initialization is pending; there is no delay timer.
@Composable
fun BsvSplash(animations: Boolean = true) {
    val alpha = remember { Animatable(if (animations) 0f else 1f) }
    LaunchedEffect(animations) {
        if (animations) alpha.animateTo(1f, tween(250)) else alpha.snapTo(1f)
    }
    BoxWithConstraints(
        Modifier.fillMaxSize()
            .background(BsvBrand.Background)
            .safeDrawingPadding()
            .testTag("bsv-splash")
    ) {
        val logoWidth = minOf(maxWidth * .45f, 280.dp)
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                Brush.radialGradient(
                    listOf(BsvBrand.Violet.copy(alpha = .23f), Color.Transparent),
                    center = center,
                    radius = size.minDimension * .6f,
                ),
                radius = size.minDimension * .6f,
            )
        }
        Column(
            Modifier.align(Alignment.Center).alpha(alpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BrandLogo(Modifier.width(logoWidth).aspectRatio(1208f / 519f))
            Text(
                "Brawl Stars Visuals",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            CircularProgressIndicator(
                Modifier.size(22.dp).testTag("loading-indicator"),
                color = BsvBrand.Violet,
                strokeWidth = 2.dp,
            )
        }
        Text(
            "Version ${BsvBrand.VERSION} | Android",
            Modifier.align(Alignment.BottomCenter).padding(16.dp),
            color = Color(0xFF9898AA),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
