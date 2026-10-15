package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Book
import com.example.data.model.LoanRecord
import com.example.data.model.LoanStatus
import com.example.data.model.Student
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldContainer
import com.example.ui.theme.AccentGoldOnContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldGreenContainer
import com.example.ui.theme.EmeraldGreenOnContainer
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavyPrimaryDark
import com.example.ui.theme.OverdueRed
import com.example.ui.theme.OverdueRedContainer
import com.example.ui.theme.OverdueRedOnContainer
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

// Gradient color themes for book covers
val BookCoverGradients = listOf(
    Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))), // Navy Blue
    Brush.verticalGradient(listOf(Color(0xFF065F46), Color(0xFF022C22))), // Emerald Forest
    Brush.verticalGradient(listOf(Color(0xFF7C2D12), Color(0xFF451A03))), // Crimson Leather
    Brush.verticalGradient(listOf(Color(0xFF4C1D95), Color(0xFF2E1065))), // Royal Purple
    Brush.verticalGradient(listOf(Color(0xFF854D0E), Color(0xFF451A03))), // Vintage Amber
    Brush.verticalGradient(listOf(Color(0xFF0F766E), Color(0xFF134E4A)))  // Deep Teal
)

val AvatarColors = listOf(
    Color(0xFF1E3A8A),
    Color(0xFFD97706),
    Color(0xFF059669),
    Color(0xFF7C3AED),
    Color(0xFFDC2626),
    Color(0xFF0284C7)
)

@Composable
fun BookCoverBox(
    title: String,
    author: String,
    genre: String,
    gradientIndex: Int,
    modifier: Modifier = Modifier,
    width: Dp = 80.dp,
    height: Dp = 115.dp
) {
    val gradient = BookCoverGradients.getOrElse(gradientIndex % BookCoverGradients.size) { BookCoverGradients[0] }
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .background(gradient)
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x33FFFFFF))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = genre.take(12),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 12.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = author,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatusBadge(
    statusText: String,
    containerColor: Color,
    contentColor: Color,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = statusText,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AvailabilityStatusBadge(availableCopies: Int, totalCopies: Int) {
    when {
        availableCopies == 0 -> {
            StatusBadge(
                statusText = "Checked Out",
                containerColor = OverdueRedContainer,
                contentColor = OverdueRed,
                icon = Icons.Default.Close
            )
        }
        availableCopies in 1..2 -> {
            StatusBadge(
                statusText = "Low Stock ($availableCopies left)",
                containerColor = AccentGoldContainer,
                contentColor = AccentGold,
                icon = Icons.Default.Warning
            )
        }
        else -> {
            StatusBadge(
                statusText = "Available ($availableCopies/$totalCopies)",
                containerColor = EmeraldGreenContainer,
                contentColor = EmeraldGreen,
                icon = Icons.Default.CheckCircle
            )
        }
    }
}

@Composable
fun LoanStatusBadge(loan: LoanRecord) {
    when (loan.status) {
        LoanStatus.RETURNED -> {
            StatusBadge(
                statusText = "Returned",
                containerColor = EmeraldGreenContainer,
                contentColor = EmeraldGreen,
                icon = Icons.Default.CheckCircle
            )
        }
        LoanStatus.OVERDUE -> {
            val days = loan.daysOverdue()
            StatusBadge(
                statusText = "Overdue ($days d)",
                containerColor = OverdueRedContainer,
                contentColor = OverdueRed,
                icon = Icons.Default.Warning
            )
        }
        LoanStatus.ACTIVE -> {
            if (loan.isDueSoon()) {
                StatusBadge(
                    statusText = "Due Soon",
                    containerColor = AccentGoldContainer,
                    contentColor = AccentGold,
                    icon = Icons.Default.Warning
                )
            } else {
                StatusBadge(
                    statusText = "Active",
                    containerColor = Color(0xFFEFF6FF),
                    contentColor = NavyPrimary,
                    icon = Icons.Default.Book
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    accentBorder: Color? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(accentBorder ?: SlateBorder, accentBorder ?: SlateBorder))
        )
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
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextSecondary,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SlateTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = SlateTextMuted
            )
        }
    }
}

@Composable
fun StudentAvatar(name: String, colorIndex: Int, size: Dp = 36.dp) {
    val color = AvatarColors.getOrElse(colorIndex % AvatarColors.size) { AvatarColors[0] }
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifBlank { "S" }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.38f).sp
        )
    }
}

@Composable
fun AddStudentDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSave: (code: String, name: String, email: String, phone: String, dept: String, year: String) -> Boolean
) {
    if (!isOpen) return

    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("Computer Science") }
    var year by remember { mutableStateOf("Freshman (1st Year)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Register New Student",
                fontWeight = FontWeight.Bold,
                color = NavyPrimaryDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Student ID (e.g. CS-2026-101)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_id_input")
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_name_input")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Institutional Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_email_input")
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dept,
                    onValueChange = { dept = it },
                    label = { Text("Academic Department") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Academic Level / Year") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (onSave(code, name, email, phone, dept, year)) {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("save_student_confirm_button")
            ) {
                Text("Register Student")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StudentDetailDialog(
    student: Student?,
    activeLoans: List<LoanRecord>,
    onDismiss: () -> Unit,
    onReturnBook: (loanId: String) -> Unit
) {
    if (student == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StudentAvatar(name = student.fullName, colorIndex = student.avatarColorIndex, size = 44.dp)
                Column {
                    Text(
                        text = student.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = NavyPrimaryDark
                    )
                    Text(
                        text = "${student.studentId} • ${student.department}",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Contact: ${student.email} | ${student.phone}",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
                Text(
                    text = "Level: ${student.gradeYear}",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Current Borrowed Books (${activeLoans.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SlateTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (activeLoans.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateSurfaceVariant)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No books currently checked out.",
                            fontSize = 13.sp,
                            color = SlateTextSecondary
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activeLoans.forEach { loan ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = loan.bookTitle,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Due: ${loan.dueDate}",
                                            fontSize = 11.sp,
                                            color = if (loan.daysOverdue() > 0) OverdueRed else SlateTextMuted
                                        )
                                    }
                                    Button(
                                        onClick = { onReturnBook(loan.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Return", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ExportReportDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    books: List<Book>,
    loans: List<LoanRecord>,
    students: List<Student>
) {
    if (!isOpen) return

    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    val csvContent = remember(books, loans, students) {
        buildString {
            appendLine("=== LIBRATRACK AUDIT & CIRCULATION REPORT ===")
            appendLine("Generated: 2026-10-15 | System: LibraTrack Enterprise")
            appendLine("Summary: Total Titles=${books.size}, Total Copies=${books.sumOf { it.totalCopies }}, Active Loans=${loans.count { it.status != LoanStatus.RETURNED }}, Overdue=${loans.count { it.status == LoanStatus.OVERDUE }}")
            appendLine()
            appendLine("--- ACTIVE CIRCULATION RECORDS ---")
            appendLine("LoanID,BookTitle,StudentName,StudentID,BorrowDate,DueDate,Status,FineAmount")
            loans.forEach { l ->
                appendLine("${l.id},\"${l.bookTitle}\",\"${l.studentName}\",${l.studentCode},${l.borrowDate},${l.dueDate},${l.status},${String.format("$%.2f", l.fineAmount())}")
            }
            appendLine()
            appendLine("--- INVENTORY SUMMARY ---")
            appendLine("ID,Title,Author,ISBN,Genre,Available,Total,Shelf")
            books.forEach { b ->
                appendLine("${b.id},\"${b.title}\",\"${b.author}\",${b.isbn},${b.genre},${b.availableCopies},${b.totalCopies},${b.shelfLocation}")
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Export System Report",
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimaryDark
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Report generated with ${loans.size} circulation records and ${books.size} catalog entries. Ready for export in CSV format.",
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(10.dp)
                ) {
                    Text(
                        text = csvContent.take(650) + "...\n[Full records ready in buffer]",
                        color = Color(0xFF38BDF8),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(csvContent))
                    copied = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("copy_csv_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (copied) "Copied CSV to Clipboard!" else "Copy CSV Export")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
