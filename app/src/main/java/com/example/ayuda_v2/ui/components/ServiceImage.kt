package com.example.ayuda_v2.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

/**
 * Composable that displays an image using Coil for efficient loading and caching.
 * Coil handles memory management, caching, and prevents memory leaks automatically.
 *
 * Benefits of using Coil:
 * - Native Kotlin and Coroutines support
 * - Automatic memory and disk caching
 * - Efficient bitmap recycling (prevents OOM errors)
 * - Automatic lifecycle-aware loading (prevents leaks)
 * - Native Jetpack Compose integration
 * - Support for placeholders and error images
 *
 * @param imageUrl URL of the image to load
 * @param contentDescription Accessibility description
 * @param modifier Modifier for the image
 * @param size Size of the image container
 */
@Composable
fun ServiceImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    if (imageUrl.isNullOrEmpty()) {
        // Show placeholder when no image URL
        DefaultServicePlaceholder(modifier = modifier.size(size))
    } else {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier.size(size),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                DefaultServicePlaceholder(modifier = Modifier.size(size))
            }
        )
    }
}

/**
 * Default placeholder shown when no image is available or loading fails.
 */
@Composable
private fun DefaultServicePlaceholder(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        // Empty surface as placeholder
    }
}
