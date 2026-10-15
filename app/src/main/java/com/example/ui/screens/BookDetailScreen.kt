package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Book
import com.example.data.model.LoanRecord
import com.example.ui.components.AvailabilityStatusBadge
import com.example.ui.components.BookCoverBox
import com.example.ui.components.LoanStatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavyPrimaryDark
import com.example.ui.theme.OverdueRed
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.BorrowReturnTab
import com.example.ui.viewmodel.Screen

@Composable
fun BookDetailScreen(
    book: Book?,
    borrowHistory: List<LoanRecord>,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    if (book == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Book not found in catalog", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) { Text("Back to Catalog") }
            }
        }
        return
    }

    val availableRatio = if (book.totalCopies > 0) book.availableCopies.toFloat() / book.totalCopies.toFloat() else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Navigation Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("book_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NavyPrimary
                        )
                    }
                    Text(
                        text = "Catalog / ${book.title}",
                        fontSize = 14.sp,
                        color = SlateTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onNavigate(Screen.AddEditBook(book.id)) },
                        modifier = Modifier.testTag("book_detail_edit_button")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Book")
                    }
                    if (book.availableCopies > 0) {
                        Button(
                            onClick = { onNavigate(Screen.BorrowReturn(BorrowReturnTab.BORROW)) },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            modifier = Modifier.testTag("book_detail_loan_button")
                        ) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Issue Loan")
                        }
                    }
                }
            }
        }

        // Hero Card with Cover & Core Specs
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        BookCoverBox(
                            title = book.title,
                            author = book.author,
                            genre = book.genre,
                            gradientIndex = book.coverGradientIndex,
                            width = 110.dp,
                            height = 160.dp
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = book.genre.uppercase(),
                                        color = NavyPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                AvailabilityStatusBadge(
                                    availableCopies = book.availableCopies,
                                    totalCopies = book.totalCopies
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = book.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyPrimaryDark,
                                lineHeight = 28.sp
                            )
                            Text(
                                text = "By ${book.author}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Inventory Stock Progress Bar
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Shelf Availability: ${book.availableCopies} of ${book.totalCopies} copies in stock",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (book.availableCopies > 0) EmeraldGreen else OverdueRed
                                    )
                                    Text(
                                        text = "${(availableRatio * 100).toInt()}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateTextPrimary
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { availableRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (book.availableCopies > 0) EmeraldGreen else OverdueRed,
                                    trackColor = SlateBorder
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = SlateBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Metadata Spec Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecItem(label = "ISBN", value = book.isbn, isCode = true)
                        SpecItem(label = "PUBLISHER", value = book.publisher)
                        SpecItem(label = "YEAR", value = book.publishYear.toString())
                        SpecItem(label = "SHELF LOCATION", value = book.shelfLocation)
                        SpecItem(label = "BORROWED COPIES", value = "${book.borrowedCopies} active")
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "DESCRIPTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.description,
                        fontSize = 13.sp,
                        color = SlateTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Borrow History Section
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
                            Icon(imageVector = Icons.Default.History, contentDescription = null, tint = NavyPrimary)
                            Text(
                                text = "Borrowing History for this Title",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimaryDark
                            )
                        }
                        Text(
                            text = "${borrowHistory.size} records",
                            fontSize = 12.sp,
                            color = SlateTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (borrowHistory.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateSurfaceVariant)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No loans have been recorded for this book yet.",
                                fontSize = 13.sp,
                                color = SlateTextSecondary
                            )
                        }
                    } else {
                        // History Data Table
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SlateSurfaceVariant)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("STUDENT PATRON", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.weight(2f))
                                Text("BORROW DATE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.weight(1.2f))
                                Text("DUE DATE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.weight(1.2f))
                                Text("STATUS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.weight(1.2f))
                            }
                            HorizontalDivider(color = SlateBorder)

                            borrowHistory.forEach { loan ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(2f)) {
                                        Text(loan.studentName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = SlateTextPrimary)
                                        Text(loan.studentCode, fontSize = 11.sp, color = SlateTextMuted)
                                    }
                                    Text(loan.borrowDate, fontSize = 12.sp, color = SlateTextSecondary, modifier = Modifier.weight(1.2f))
                                    Text(loan.dueDate, fontSize = 12.sp, color = SlateTextSecondary, modifier = Modifier.weight(1.2f))
                                    Box(modifier = Modifier.weight(1.2f)) {
                                        LoanStatusBadge(loan)
                                    }
                                }
                                HorizontalDivider(color = SlateBorder)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecItem(label: String, value: String, isCode: Boolean = false) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = SlateTextMuted,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SlateTextPrimary,
            fontFamily = if (isCode) FontFamily.Monospace else FontFamily.Default
        )
    }
}
