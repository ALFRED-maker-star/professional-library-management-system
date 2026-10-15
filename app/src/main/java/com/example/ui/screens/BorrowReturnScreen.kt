package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.Book
import com.example.data.model.LoanRecord
import com.example.data.model.Student
import com.example.ui.components.AvailabilityStatusBadge
import com.example.ui.components.BookCoverBox
import com.example.ui.components.LoanStatusBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.AccentGold
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowReturnScreen(
    books: List<Book>,
    students: List<Student>,
    activeLoans: List<LoanRecord>,
    initialTab: BorrowReturnTab,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onIssueLoan: (studentId: String, bookId: String, durationDays: Int, notes: String) -> Unit,
    onMarkReturned: (loanId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Bar
        Column {
            Text(
                text = "Circulation Desk",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimaryDark
            )
            Text(
                text = "Issue loan records and process returned volumes",
                fontSize = 13.sp,
                color = SlateTextSecondary
            )
        }

        // Tab Selector
        SecondaryTabRow(
            selectedTabIndex = if (selectedTab == BorrowReturnTab.BORROW) 0 else 1,
            containerColor = SlateSurface,
            contentColor = NavyPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = selectedTab == BorrowReturnTab.BORROW,
                onClick = { selectedTab = BorrowReturnTab.BORROW },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Borrow / Checkout", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_borrow")
            )
            Tab(
                selected = selectedTab == BorrowReturnTab.RETURN,
                onClick = { selectedTab = BorrowReturnTab.RETURN },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Return Desk (${activeLoans.size} Active)", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_return")
            )
        }

        if (selectedTab == BorrowReturnTab.BORROW) {
            BorrowFormContent(
                books = books,
                students = students,
                onIssueLoan = onIssueLoan,
                modifier = Modifier.weight(1f)
            )
        } else {
            ReturnTableContent(
                activeLoans = activeLoans,
                searchQuery = searchQuery,
                onSearchChange = onSearchChange,
                onMarkReturned = onMarkReturned,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowFormContent(
    books: List<Book>,
    students: List<Student>,
    onIssueLoan: (studentId: String, bookId: String, durationDays: Int, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var studentDropdownExpanded by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(students.firstOrNull()) }

    var bookDropdownExpanded by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<Book?>(books.firstOrNull { it.availableCopies > 0 }) }

    var durationDays by remember { mutableIntStateOf(14) }
    var loanNotes by remember { mutableStateOf("") }

    val calculatedDueDate = remember(durationDays) {
        val today = LocalDate.parse("2026-10-15")
        val due = today.plusDays(durationDays.toLong())
        due.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "STEP 1: SELECT STUDENT PATRON",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = studentDropdownExpanded,
                    onExpandedChange = { studentDropdownExpanded = !studentDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedStudent?.let { "${it.fullName} (${it.studentId}) - ${it.department}" } ?: "Select student...",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NavyPrimary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("borrow_student_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = studentDropdownExpanded,
                        onDismissRequest = { studentDropdownExpanded = false }
                    ) {
                        students.forEach { s ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(s.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${s.studentId} • ${s.department}", fontSize = 11.sp, color = SlateTextSecondary)
                                    }
                                },
                                onClick = {
                                    selectedStudent = s
                                    studentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "STEP 2: SELECT BOOK TO BORROW",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = bookDropdownExpanded,
                    onExpandedChange = { bookDropdownExpanded = !bookDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedBook?.let { "${it.title} [${it.availableCopies} available]" } ?: "Select book...",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Book, contentDescription = null, tint = NavyPrimary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bookDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("borrow_book_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = bookDropdownExpanded,
                        onDismissRequest = { bookDropdownExpanded = false }
                    ) {
                        books.forEach { b ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(b.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text("${b.author} • ${b.isbn}", fontSize = 11.sp, color = SlateTextSecondary)
                                        }
                                        AvailabilityStatusBadge(availableCopies = b.availableCopies, totalCopies = b.totalCopies)
                                    }
                                },
                                onClick = {
                                    selectedBook = b
                                    bookDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                selectedBook?.let { book ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (book.availableCopies > 0) Color(0xFFF0FDF4) else OverdueRedContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BookCoverBox(
                                title = book.title,
                                author = book.author,
                                genre = book.genre,
                                gradientIndex = book.coverGradientIndex,
                                width = 40.dp,
                                height = 56.dp
                            )
                            Column {
                                Text(
                                    text = if (book.availableCopies > 0) "In Stock: ${book.availableCopies} copies available on shelf ${book.shelfLocation}" else "Warning: All ${book.totalCopies} copies currently checked out!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (book.availableCopies > 0) EmeraldGreen else OverdueRed
                                )
                                Text(
                                    text = "ISBN: ${book.isbn} • Category: ${book.genre}",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "STEP 3: LOAN DURATION & AUTO-CALCULATED DUE DATE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(7 to "1 Week (Short)", 14 to "14 Days (Standard)", 28 to "28 Days (Semester)").forEach { (days, label) ->
                        FilterChip(
                            selected = durationDays == days,
                            onClick = { durationDays = days },
                            label = { Text(label) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = NavyPrimary)
                        Column {
                            Text(
                                text = "Auto-Calculated Due Date:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyPrimaryDark
                            )
                            Text(
                                text = calculatedDueDate,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = loanNotes,
                    onValueChange = { loanNotes = it },
                    label = { Text("Librarian Circulation Notes (Optional)") },
                    placeholder = { Text("e.g. Special reference permission, midterm project") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                val canIssue = selectedStudent != null && selectedBook != null && (selectedBook?.availableCopies ?: 0) > 0

                Button(
                    onClick = {
                        val stu = selectedStudent ?: return@Button
                        val bk = selectedBook ?: return@Button
                        onIssueLoan(stu.id, bk.id, durationDays, loanNotes)
                    },
                    enabled = canIssue,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("issue_loan_button")
                ) {
                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if ((selectedBook?.availableCopies ?: 0) > 0) "Confirm & Issue Loan Record" else "Book Currently Unavailable",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ReturnTableContent(
    activeLoans: List<LoanRecord>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onMarkReturned: (loanId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Filter active loans by student, title, or ID...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = NavyPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("return_search_input")
            )

            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val isWide = maxWidth >= 840.dp

                if (activeLoans.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No active loans currently checked out matching your query.",
                            fontSize = 14.sp,
                            color = SlateTextSecondary
                        )
                    }
                } else if (isWide) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateSurfaceVariant)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("BOOK TITLE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.weight(2.4f))
                            Text("PATRON", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.weight(2f))
                            Text("BORROW DATE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.weight(1.2f))
                            Text("DUE DATE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.weight(1.2f))
                            Text("STATUS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.weight(1.3f))
                            Text("ACTION", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, modifier = Modifier.width(130.dp))
                        }
                        HorizontalDivider(color = SlateBorder)

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(activeLoans, key = { it.id }) { loan ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(2.4f)) {
                                        Text(loan.bookTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("ISBN: ${loan.bookIsbn}", fontSize = 11.sp, color = SlateTextMuted, fontFamily = FontFamily.Monospace)
                                    }
                                    Column(modifier = Modifier.weight(2f)) {
                                        Text(loan.studentName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = SlateTextPrimary)
                                        Text(loan.studentCode, fontSize = 11.sp, color = SlateTextMuted)
                                    }
                                    Text(loan.borrowDate, fontSize = 12.sp, color = SlateTextSecondary, modifier = Modifier.weight(1.2f))
                                    Text(
                                        loan.dueDate,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (loan.daysOverdue() > 0) OverdueRed else SlateTextPrimary,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    Box(modifier = Modifier.weight(1.3f)) {
                                        LoanStatusBadge(loan)
                                    }
                                    Button(
                                        onClick = { onMarkReturned(loan.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .width(130.dp)
                                            .height(34.dp)
                                            .testTag("mark_returned_${loan.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mark Returned", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                HorizontalDivider(color = SlateBorder)
                            }
                        }
                    }
                } else {
                    // Mobile Card List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(activeLoans, key = { it.id }) { loan ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(loan.bookTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                        LoanStatusBadge(loan)
                                    }
                                    Text("${loan.studentName} (${loan.studentCode})", fontSize = 12.sp, color = SlateTextSecondary)
                                    Text("Due: ${loan.dueDate} (Borrowed: ${loan.borrowDate})", fontSize = 11.sp, color = SlateTextMuted)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { onMarkReturned(loan.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        modifier = Modifier.fillMaxWidth().height(36.dp)
                                    ) {
                                        Text("Mark Returned & Restock")
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
