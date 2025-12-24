package com.officialcodingconvention.weby.presentation.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.officialcodingconvention.weby.R
import com.officialcodingconvention.weby.WebyApplication
import com.officialcodingconvention.weby.core.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { WebyApplication.getInstance().preferencesManager }
    val scope = rememberCoroutineScope()

    val pages = remember {
        listOf(
            OnboardingPage(
                title = context.getString(R.string.onboarding_welcome_title),
                description = context.getString(R.string.onboarding_welcome_desc),
                icon = Icons.Outlined.PhoneAndroid,
                accentColor = WebyPrimary
            ),
            OnboardingPage(
                title = context.getString(R.string.onboarding_visual_title),
                description = context.getString(R.string.onboarding_visual_desc),
                icon = Icons.Outlined.TouchApp,
                accentColor = WebySecondary
            ),
            OnboardingPage(
                title = context.getString(R.string.onboarding_code_title),
                description = context.getString(R.string.onboarding_code_desc),
                icon = Icons.Outlined.Code,
                accentColor = WebyTertiary
            ),
            OnboardingPage(
                title = context.getString(R.string.onboarding_export_title),
                description = context.getString(R.string.onboarding_export_desc),
                icon = Icons.Outlined.CloudUpload,
                accentColor = Success
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val isLastPage = pagerState.currentPage == pages.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background decoration
        OnboardingBackground(
            currentPage = pagerState.currentPage,
            accentColor = pages[pagerState.currentPage].accentColor
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Skip button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (!isLastPage) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                preferencesManager.setOnboardingCompleted(true)
                                onComplete()
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            text = context.getString(R.string.onboarding_skip),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    isCurrentPage = page == pagerState.currentPage
                )
            }

            // Bottom section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    pages.forEachIndexed { index, page ->
                        PageIndicator(
                            isSelected = index == pagerState.currentPage,
                            color = page.accentColor
                        )
                    }
                }

                // Action button
                AnimatedContent(
                    targetState = isLastPage,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    },
                    label = "button_transition"
                ) { showGetStarted ->
                    if (showGetStarted) {
                        Button(
                            onClick = {
                                scope.launch {
                                    preferencesManager.setOnboardingCompleted(true)
                                    onComplete()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = pages[pagerState.currentPage].accentColor
                            ),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text(
                                text = context.getString(R.string.onboarding_get_started),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = pages[pagerState.currentPage].accentColor
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = SolidColor(pages[pagerState.currentPage].accentColor.copy(alpha = 0.5f))
                            )
                        ) {
                            Text(
                                text = context.getString(R.string.onboarding_next),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    isCurrentPage: Boolean
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isCurrentPage) 1f else 0.5f,
        animationSpec = tween(300),
        label = "alpha"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isCurrentPage) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .alpha(animatedAlpha)
            .scale(animatedScale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon container with animated background
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(page.accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = page.accentColor
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun PageIndicator(
    isSelected: Boolean,
    color: Color
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "indicator_width"
    )

    Box(
        modifier = Modifier
            .height(8.dp)
            .width(width)
            .clip(CircleShape)
            .background(
                if (isSelected) color
                else MaterialTheme.colorScheme.outlineVariant
            )
    )
}

@Composable
private fun OnboardingBackground(
    currentPage: Int,
    accentColor: Color
) {
    val animatedColor by animateColorAsState(
        targetValue = accentColor,
        animationSpec = tween(500),
        label = "bg_color"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Top gradient blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    animatedColor.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.8f, size.height * 0.1f),
                radius = size.width * 0.6f
            ),
            radius = size.width * 0.6f,
            center = Offset(size.width * 0.8f, size.height * 0.1f)
        )

        // Bottom gradient blob
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    animatedColor.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.2f, size.height * 0.85f),
                radius = size.width * 0.5f
            ),
            radius = size.width * 0.5f,
            center = Offset(size.width * 0.2f, size.height * 0.85f)
        )

        // Decorative circles
        drawCircle(
            color = animatedColor.copy(alpha = 0.1f),
            radius = 80f,
            center = Offset(size.width * 0.1f, size.height * 0.3f),
            style = Stroke(width = 2f)
        )

        drawCircle(
            color = animatedColor.copy(alpha = 0.08f),
            radius = 120f,
            center = Offset(size.width * 0.9f, size.height * 0.6f),
            style = Stroke(width = 2f)
        )
    }
}
