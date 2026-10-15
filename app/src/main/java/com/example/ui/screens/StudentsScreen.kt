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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.ui.components.StudentAvatar
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
fun StudentsScreen(
    students: List<Student>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    getStudentLoanCount: (String) -> Int,
    onStudentClick: (Student) -> Unit,
    onOpenAddStudent: () -> Unit,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & Add Student Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Student Directory",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimaryDark
                )
                Text(
                    text = "${students.size} registered patrons with borrowing privileges",
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )
            }

            Button(
                onClick = onOpenAddStudent,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_student_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ Add Student", fontWeight = FontWeight.Bold)
            }
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
                    .padding(14.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search student by name, student ID, or department...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = NavyPrimary
                        )
                    },
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
                        focusedIndicatorColor = NavyPrimary,
                        unfocusedIndicatorColor = SlateBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_search_input")
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

                if (students.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No students match your query.",
                            fontSize = 14.sp,
                            color = SlateTextSecondary
                        )
                    }
                } else if (isWide) {
                    // Full Table for Tablet/Desktop
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateSurfaceVariant)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("STUDENT NAME", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, letterSpacing = 0.5.sp, modifier = Modifier.weight(2.2f))
                            Text("STUDENT ID", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, letterSpacing = 0.5.sp, modifier = Modifier.weight(1.3f))
                            Text("EMAIL ADDRESS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, letterSpacing = 0.5.sp, modifier = Modifier.weight(1.8f))
                            Text("DEPARTMENT", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, letterSpacing = 0.5.sp, modifier = Modifier.weight(1.5f))
                            Text("BORROWED BOOKS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, letterSpacing = 0.5.sp, modifier = Modifier.weight(1.4f))
                            Text("ACTIONS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SlateTextMuted, letterSpacing = 0.5.sp, modifier = Modifier.width(110.dp))
                        }
                        HorizontalDivider(color = SlateBorder)

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(students, key = { it.id }) { student ->
                                val loanCount = getStudentLoanCount(student.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onStudentClick(student) }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(2.2f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        StudentAvatar(name = student.fullName, colorIndex = student.avatarColorIndex, size = 36.dp)
                                        Column {
                                            Text(
                                                text = student.fullName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = SlateTextPrimary
                                            )
                                            Text(
                                                text = student.gradeYear,
                                                fontSize = 11.sp,
                                                color = SlateTextMuted
                                            )
                                        }
                                    }

                                    Text(
                                        text = student.studentId,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NavyPrimary,
                                        modifier = Modifier.weight(1.3f)
                                    )

                                    Text(
                                        text = student.email,
                                        fontSize = 12.sp,
                                        color = SlateTextSecondary,
                                        modifier = Modifier.weight(1.8f)
                                    )

                                    Text(
                                        text = student.department,
                                        fontSize = 12.sp,
                                        color = SlateTextPrimary,
                                        modifier = Modifier.weight(1.5f)
                                    )

                                    Box(modifier = Modifier.weight(1.4f)) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (loanCount > 0) Color(0xFFEFF6FF) else SlateSurfaceVariant
                                        ) {
                                            Text(
                                                text = if (loanCount == 1) "1 book checked out" else "$loanCount books checked out",
                                                color = if (loanCount > 0) NavyPrimary else SlateTextMuted,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.width(110.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        IconButton(
                                            onClick = { onStudentClick(student) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = "View Profile",
                                                tint = NavyPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { onNavigate(Screen.BorrowReturn(BorrowReturnTab.BORROW)) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SwapHoriz,
                                                contentDescription = "New Loan",
                                                tint = AccentGold,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = SlateBorder)
                            }
                        }
                    }
                } else {
                    // Mobile / Compact List View
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(students, key = { it.id }) { student ->
                            val loanCount = getStudentLoanCount(student.id)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onStudentClick(student) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        StudentAvatar(name = student.fullName, colorIndex = student.avatarColorIndex, size = 38.dp)
                                        Column {
                                            Text(
                                                text = student.fullName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = SlateTextPrimary
                                            )
                                            Text(
                                                text = "${student.studentId} • ${student.department}",
                                                fontSize = 11.sp,
                                                color = SlateTextSecondary
                                            )
                                            Text(
                                                text = "$loanCount active loans",
                                                fontSize = 11.sp,
                                                color = if (loanCount > 0) NavyPrimary else SlateTextMuted,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { onStudentClick(student) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("View", fontSize = 11.sp)
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
