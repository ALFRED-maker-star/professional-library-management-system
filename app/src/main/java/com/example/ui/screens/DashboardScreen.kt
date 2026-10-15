package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityItem
import com.example.data.model.ActivityType
import com.example.data.model.Book
import com.example.data.model.Librarian
import com.example.ui.components.BookCoverBox
import com.example.ui.components.StatCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldGreenContainer
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavyPrimaryDark
import com.example.ui.theme.OverdueRed
import com.example.ui.theme.OverdueRedContainer
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.BorrowReturnTab
import com.example.ui.viewmodel.DashboardStats
import com.example.ui.viewmodel.Screen

@Composable
fun DashboardScreen(
    stats: DashboardStats,
    activities: List<ActivityItem>,
    recentBooks: List<Book>,
    currentLibrarian: Librarian?,
    onNavigate: (Screen) -> Unit,
    onOpenExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Header bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good morning, ${currentLibrarian?.name?.split(" ")?.firstOrNull() ?: "Librarian"}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimaryDark
                    )
                    Text(
                        text = "System active • Thursday, October 15, 2026",
                        fontSize = 13.sp,
                        color = SlateTextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onOpenExport,
                        modifier = Modifier.testTag("dashboard_export_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Audit")
                    }
                    Button(
                        onClick = { onNavigate(Screen.BorrowReturn(BorrowReturnTab.BORROW)) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier.testTag("dashboard_new_loan_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ New Loan")
                    }
                }
            }
        }

        // Overdue Alert Banner if overdues exist
        if (stats.overdueLoansCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(Screen.OverdueList) }
                        .testTag("dashboard_overdue_banner"),
                    colors = CardDefaults.cardColors(containerColor = OverdueRedContainer),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OverdueRed))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(OverdueRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Attention Needed: ${stats.overdueLoansCount} Overdue Books",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF7F1D1D)
                                )
                                Text(
                                    text = "Total outstanding penalties: ${String.format("$%.2f", stats.totalOverdueFines)} • Action required",
                                    fontSize = 12.sp,
                                    color = Color(0xFF991B1B)
                                )
                            }
                        }

                        Button(
                            onClick = { onNavigate(Screen.OverdueList) },
                            colors = ButtonDefaults.buttonColors(containerColor = OverdueRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Review Overdues")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4 Stat Cards
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 680.dp
                if (isWide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = "Total Books",
                            value = stats.totalBooks.toString(),
                            subtitle = "${stats.totalUniqueTitles} unique titles in catalog",
                            icon = Icons.Default.MenuBook,
                            iconTint = NavyPrimary,
                            iconBgColor = Color(0xFFEFF6FF),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.BookCatalog) }
                        )
                        StatCard(
                            title = "Books Borrowed",
                            value = stats.booksBorrowed.toString(),
                            subtitle = "${stats.returnRatePercent}% on-time circulation rate",
                            icon = Icons.Default.SwapHoriz,
                            iconTint = EmeraldGreen,
                            iconBgColor = EmeraldGreenContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.BorrowReturn(BorrowReturnTab.RETURN)) }
                        )
                        StatCard(
                            title = "Overdue",
                            value = stats.overdueLoansCount.toString(),
                            subtitle = "${String.format("$%.2f", stats.totalOverdueFines)} in pending fines",
                            icon = Icons.Default.Warning,
                            iconTint = OverdueRed,
                            iconBgColor = OverdueRedContainer,
                            accentBorder = if (stats.overdueLoansCount > 0) OverdueRed else null,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.OverdueList) }
                        )
                        StatCard(
                            title = "Total Students",
                            value = stats.totalStudents.toString(),
                            subtitle = "Registered library patrons",
                            icon = Icons.Default.People,
                            iconTint = AccentGold,
                            iconBgColor = AccentGoldContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Students) }
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Total Books",
                                value = stats.totalBooks.toString(),
                                subtitle = "${stats.totalUniqueTitles} titles",
                                icon = Icons.Default.MenuBook,
                                iconTint = NavyPrimary,
                                iconBgColor = Color(0xFFEFF6FF),
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(Screen.BookCatalog) }
                            )
                            StatCard(
                                title = "Borrowed",
                                value = stats.booksBorrowed.toString(),
                                subtitle = "Active circulation",
                                icon = Icons.Default.SwapHoriz,
                                iconTint = EmeraldGreen,
                                iconBgColor = EmeraldGreenContainer,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(Screen.BorrowReturn(BorrowReturnTab.RETURN)) }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Overdue",
                                value = stats.overdueLoansCount.toString(),
                                subtitle = "${String.format("$%.2f", stats.totalOverdueFines)} fines",
                                icon = Icons.Default.Warning,
                                iconTint = OverdueRed,
                                iconBgColor = OverdueRedContainer,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(Screen.OverdueList) }
                            )
                            StatCard(
                                title = "Students",
                                value = stats.totalStudents.toString(),
                                subtitle = "Patrons registered",
                                icon = Icons.Default.People,
                                iconTint = AccentGold,
                                iconBgColor = AccentGoldContainer,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(Screen.Students) }
                            )
                        }
                    }
                }
            }
        }

        // Quick Navigation & Action Shortcuts
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "QUICK CIRCULATION ACTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionShortcutButton(
                            title = "Issue Loan",
                            desc = "Checkout to student",
                            icon = Icons.Default.SwapHoriz,
                            tint = NavyPrimary,
                            bgColor = Color(0xFFEFF6FF),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.BorrowReturn(BorrowReturnTab.BORROW)) }
                        )
                        ActionShortcutButton(
                            title = "Mark Return",
                            desc = "Restock book",
                            icon = Icons.Default.CheckCircle,
                            tint = EmeraldGreen,
                            bgColor = EmeraldGreenContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.BorrowReturn(BorrowReturnTab.RETURN)) }
                        )
                        ActionShortcutButton(
                            title = "Add Book",
                            desc = "New catalog item",
                            icon = Icons.Default.Book,
                            tint = AccentGold,
                            bgColor = AccentGoldContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.AddEditBook()) }
                        )
                        ActionShortcutButton(
                            title = "Add Student",
                            desc = "Register patron",
                            icon = Icons.Default.PersonAdd,
                            tint = Color(0xFF7C3AED),
                            bgColor = Color(0xFFF3E8FF),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(Screen.Students) }
                        )
                    }
                }
            }
        }

        // Two-Column Grid: Left: Recent Activity Feed, Right: Featured Catalog Preview
        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth >= 760.dp
                if (isWide) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActivityFeedSection(
                            activities = activities,
                            modifier = Modifier.weight(1.3f)
                        )
                        CatalogPreviewSection(
                            books = recentBooks.take(4),
                            onViewAll = { onNavigate(Screen.BookCatalog) },
                            onBookClick = { onNavigate(Screen.BookDetail(it.id)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActivityFeedSection(
                            activities = activities,
                            modifier = Modifier.fillMaxWidth()
                        )
                        CatalogPreviewSection(
                            books = recentBooks.take(4),
                            onViewAll = { onNavigate(Screen.BookCatalog) },
                            onBookClick = { onNavigate(Screen.BookDetail(it.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionShortcutButton(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SlateSurfaceVariant,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SlateTextPrimary
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = SlateTextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ActivityFeedSection(
    activities: List<ActivityItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity Feed",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyPrimaryDark
                )
                Text(
                    text = "Live Log",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldGreen
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                activities.forEach { act ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        val (icon, tint, bg) = when (act.type) {
                            ActivityType.LOAN_ISSUED -> Triple(Icons.Default.SwapHoriz, NavyPrimary, Color(0xFFEFF6FF))
                            ActivityType.BOOK_RETURNED -> Triple(Icons.Default.CheckCircle, EmeraldGreen, EmeraldGreenContainer)
                            ActivityType.OVERDUE_ALERT -> Triple(Icons.Default.Warning, OverdueRed, OverdueRedContainer)
                            ActivityType.BOOK_ADDED -> Triple(Icons.Default.Book, AccentGold, AccentGoldContainer)
                            ActivityType.STUDENT_REGISTERED -> Triple(Icons.Default.People, Color(0xFF7C3AED), Color(0xFFF3E8FF))
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = tint,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = act.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = SlateTextPrimary
                                )
                                Text(
                                    text = act.timeAgo,
                                    fontSize = 11.sp,
                                    color = SlateTextMuted
                                )
                            }
                            Text(
                                text = act.description,
                                fontSize = 12.sp,
                                color = SlateTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogPreviewSection(
    books: List<Book>,
    onViewAll: () -> Unit,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catalog Highlights",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyPrimaryDark
                )
                Text(
                    text = "View All →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    modifier = Modifier.clickable { onViewAll() }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                books.forEach { book ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onBookClick(book) }
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BookCoverBox(
                            title = book.title,
                            author = book.author,
                            genre = book.genre,
                            gradientIndex = book.coverGradientIndex,
                            width = 44.dp,
                            height = 62.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = SlateTextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "${book.author} • ${book.genre}",
                                fontSize = 11.sp,
                                color = SlateTextSecondary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${book.availableCopies} of ${book.totalCopies} available",
                                fontSize = 11.sp,
                                color = if (book.availableCopies > 0) EmeraldGreen else OverdueRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
