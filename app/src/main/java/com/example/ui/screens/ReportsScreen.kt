package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BookCoverBox
import com.example.ui.theme.AccentGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavyPrimaryDark
import com.example.ui.theme.NavyPrimaryLight
import com.example.ui.theme.OverdueRed
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.BookBorrowStats
import com.example.ui.viewmodel.DashboardStats
import com.example.ui.viewmodel.MonthlyTrend

@Composable
fun ReportsScreen(
    stats: DashboardStats,
    topBooks: List<BookBorrowStats>,
    monthlyTrends: List<MonthlyTrend>,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Bar & Export Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Analytics & Circulation Reports",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimaryDark
                    )
                    Text(
                        text = "Audit metrics, circulation throughput, and patron trends",
                        fontSize = 13.sp,
                        color = SlateTextSecondary
                    )
                }

                Button(
                    onClick = onExportClick,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("reports_export_button")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Report (CSV)", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Summary KPI Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "ON-TIME RETURN RATE",
                    value = "${stats.returnRatePercent}%",
                    sub = "Target: ≥90%",
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "ACTIVE CIRCULATIONS",
                    value = stats.booksBorrowed.toString(),
                    sub = "In student hands",
                    color = NavyPrimary,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "AVG LOAN DURATION",
                    value = "12.4 Days",
                    sub = "Undergraduate norm",
                    color = AccentGold,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "TOTAL INVENTORY",
                    value = stats.totalBooks.toString(),
                    sub = "${stats.totalUniqueTitles} catalog items",
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section 1: Borrowing Trends Over Time (Canvas Chart)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = NavyPrimary)
                            Text(
                                text = "Monthly Circulation Trends (2026)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimaryDark
                            )
                        }

                        // Chart legend
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(NavyPrimary))
                                Text("Checkouts", fontSize = 11.sp, color = SlateTextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(EmeraldGreen))
                                Text("Returns", fontSize = 11.sp, color = SlateTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom Canvas Bar Graph
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(SlateSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxVal = 260f
                            val count = monthlyTrends.size
                            val groupWidth = size.width / count
                            val barWidth = (groupWidth * 0.28f).coerceAtMost(32f)

                            monthlyTrends.forEachIndexed { index, item ->
                                val groupCenter = (index + 0.5f) * groupWidth

                                // Checkout bar (Navy)
                                val checkoutHeight = (item.borrowCount / maxVal) * (size.height - 24f)
                                drawRoundRect(
                                    color = NavyPrimary,
                                    topLeft = Offset(groupCenter - barWidth - 2f, size.height - checkoutHeight - 16f),
                                    size = Size(barWidth, checkoutHeight),
                                    cornerRadius = CornerRadius(4f, 4f)
                                )

                                // Return bar (Green)
                                val returnHeight = (item.returnCount / maxVal) * (size.height - 24f)
                                drawRoundRect(
                                    color = EmeraldGreen,
                                    topLeft = Offset(groupCenter + 2f, size.height - returnHeight - 16f),
                                    size = Size(barWidth, returnHeight),
                                    cornerRadius = CornerRadius(4f, 4f)
                                )
                            }
                        }

                        // X-axis labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            monthlyTrends.forEach {
                                Text(
                                    text = it.month,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SlateTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Most Borrowed Books Ranking & Category Distribution
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 760.dp
                if (isWide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MostBorrowedSection(
                            topBooks = topBooks,
                            modifier = Modifier.weight(1.3f)
                        )
                        GenreDistributionSection(
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        MostBorrowedSection(
                            topBooks = topBooks,
                            modifier = Modifier.fillMaxWidth()
                        )
                        GenreDistributionSection(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MostBorrowedSection(
    topBooks: List<BookBorrowStats>,
    modifier: Modifier = Modifier
) {
    val maxBorrows = topBooks.maxOfOrNull { it.borrowCount }?.coerceAtLeast(1) ?: 1

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Leaderboard, contentDescription = null, tint = AccentGold)
                Text(
                    text = "Most Borrowed Books",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimaryDark
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                topBooks.forEachIndexed { index, item ->
                    val rankColor = when (index) {
                        0 -> AccentGold
                        1 -> Color(0xFF94A3B8)
                        2 -> Color(0xFFB45309)
                        else -> SlateTextMuted
                    }
                    val ratio = (item.borrowCount.toFloat() / maxBorrows.toFloat()).coerceIn(0f, 1f)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Rank Badge
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(rankColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = rankColor
                            )
                        }

                        // Book Title & Bar
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.book.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = SlateTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${item.borrowCount} loans",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(SlateBorder)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(ratio)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(NavyPrimary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenreDistributionSection(
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Computer Science" to 38,
        "Literature & Arts" to 22,
        "Economics & Law" to 18,
        "Science & Medicine" to 14,
        "History & Philosophy" to 8
    )
    val colors = listOf(NavyPrimary, AccentGold, EmeraldGreen, Color(0xFF7C3AED), Color(0xFF0284C7))

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = EmeraldGreen)
                Text(
                    text = "Circulation by Classification",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimaryDark
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment proportional bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
            ) {
                categories.forEachIndexed { index, (_, pct) ->
                    Box(
                        modifier = Modifier
                            .weight(pct.toFloat())
                            .height(14.dp)
                            .background(colors[index % colors.size])
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                categories.forEachIndexed { index, (name, pct) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(colors[index % colors.size])
                            )
                            Text(name, fontSize = 12.sp, color = SlateTextPrimary)
                        }
                        Text("$pct%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SlateSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted, letterSpacing = 0.5.sp)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(sub, fontSize = 11.sp, color = SlateTextSecondary)
        }
    }
}
