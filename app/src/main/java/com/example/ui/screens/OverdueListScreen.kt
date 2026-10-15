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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LoanRecord
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
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

@Composable
fun OverdueListScreen(
    overdueLoans: List<LoanRecord>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSettleAndReturn: (loanId: String) -> Unit,
    onSendReminder: (loanId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalFines = overdueLoans.sumOf { it.fineAmount() }
    val maxOverdueDays = overdueLoans.maxOfOrNull { it.daysOverdue() } ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Overdue Loans & Penalties",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OverdueRed
            )
            Text(
                text = "Track past-due circulations and manage institutional penalty fines ($0.50/day standard tariff)",
                fontSize = 13.sp,
                color = SlateTextSecondary
            )
        }

        // Summary Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Overdue",
                value = overdueLoans.size.toString(),
                subtitle = "Active delinquent loans",
                icon = Icons.Default.Warning,
                iconTint = OverdueRed,
                iconBgColor = OverdueRedContainer,
                accentBorder = OverdueRed,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Accrued Penalties",
                value = String.format("$%.2f", totalFines),
                subtitle = "Uncollected fine revenue",
                icon = Icons.Default.Payment,
                iconTint = AccentGold,
                iconBgColor = AccentGoldContainer,
                accentBorder = AccentGold,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Max Delay",
                value = "$maxOverdueDays days",
                subtitle = "Longest overdue record",
                icon = Icons.Default.Warning,
                iconTint = NavyPrimary,
                iconBgColor = Color(0xFFEFF6FF),
                modifier = Modifier.weight(1f)
            )
        }

        // Search Bar Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Filter overdue items by student, title, or ID...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = OverdueRed) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = OverdueRed,
                        unfocusedIndicatorColor = SlateBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("overdue_search_input")
                )
            }
        }

        // Data Table
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWide = maxWidth >= 840.dp

                if (overdueLoans.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No overdue loans found!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                            Text(
                                text = "All borrowed books are within their active loan periods.",
                                fontSize = 13.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }
                } else if (isWide) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEF2F2))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("STUDENT PATRON", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF991B1B), modifier = Modifier.weight(2.2f))
                            Text("BOOK TITLE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF991B1B), modifier = Modifier.weight(2.4f))
                            Text("DUE DATE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF991B1B), modifier = Modifier.weight(1.2f))
                            Text("DAYS OVERDUE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF991B1B), modifier = Modifier.weight(1.3f))
                            Text("FINE ACCRUED", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF991B1B), modifier = Modifier.weight(1.2f))
                            Text("ACTIONS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF991B1B), modifier = Modifier.width(220.dp))
                        }
                        HorizontalDivider(color = Color(0xFFFECACA))

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(overdueLoans, key = { it.id }) { loan ->
                                val days = loan.daysOverdue()
                                val fine = loan.fineAmount()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(2.2f)) {
                                        Text(loan.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateTextPrimary)
                                        Text(loan.studentCode, fontSize = 11.sp, color = SlateTextMuted)
                                    }
                                    Column(modifier = Modifier.weight(2.4f)) {
                                        Text(loan.bookTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = SlateTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("ISBN: ${loan.bookIsbn}", fontSize = 11.sp, color = SlateTextMuted, fontFamily = FontFamily.Monospace)
                                    }
                                    Text(loan.dueDate, fontSize = 12.sp, color = SlateTextSecondary, modifier = Modifier.weight(1.2f))
                                    Box(modifier = Modifier.weight(1.3f)) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = OverdueRedContainer
                                        ) {
                                            Text(
                                                text = "$days days late",
                                                color = OverdueRed,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = String.format("$%.2f", fine),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = AccentGold,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    Row(
                                        modifier = Modifier.width(220.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { onSendReminder(loan.id) },
                                            modifier = Modifier.height(34.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Remind", fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = { onSettleAndReturn(loan.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = OverdueRed),
                                            modifier = Modifier.height(34.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Text("Settle & Return", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                HorizontalDivider(color = SlateBorder)
                            }
                        }
                    }
                } else {
                    // Mobile Cards
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(overdueLoans, key = { it.id }) { loan ->
                            val days = loan.daysOverdue()
                            val fine = loan.fineAmount()
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFECDD3))),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(loan.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = OverdueRed
                                        ) {
                                            Text(
                                                text = "$days days late",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(loan.bookTitle, fontSize = 13.sp, color = SlateTextPrimary, fontWeight = FontWeight.Medium)
                                    Text("Due: ${loan.dueDate} • Accrued Fine: ${String.format("$%.2f", fine)}", fontSize = 12.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { onSendReminder(loan.id) },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Send Notice", fontSize = 12.sp)
                                        }
                                        Button(
                                            onClick = { onSettleAndReturn(loan.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = OverdueRed),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Settle & Return", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
