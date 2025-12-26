package com.officialcodingconvention.weby.presentation.screens.onboarding

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.officialcodingconvention.weby.R
import com.officialcodingconvention.weby.WebyApplication
import com.officialcodingconvention.weby.core.theme.*
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
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

    fun completeOnboarding() {
        scope.launch {
            preferencesManager.setOnboardingCompleted(true)
            onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        // Top bar with skip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (!isLastPage) {
                TextButton(
                    onClick = ::completeOnboarding,
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
        ) { pageIndex ->
            OnboardingPageContent(page = pages[pageIndex])
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
            val currentPage = pages[pagerState.currentPage]
            if (isLastPage) {
                Button(
                    onClick = ::completeOnboarding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentPage.accentColor
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
                            pagerState.scrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = currentPage.accentColor
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

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(page.accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
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
private fun PageIndicator(isSelected: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .height(8.dp)
            .width(if (isSelected) 24.dp else 8.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) color
                else MaterialTheme.colorScheme.outlineVariant
            )
    )
}
