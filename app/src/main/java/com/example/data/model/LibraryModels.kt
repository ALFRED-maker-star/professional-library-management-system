package com.example.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val isbn: String,
    val genre: String,
    val publisher: String,
    val publishYear: Int,
    val totalCopies: Int,
    val availableCopies: Int,
    val shelfLocation: String,
    val description: String,
    val coverGradientIndex: Int = 0
) {
    val isAvailable: Boolean get() = availableCopies > 0
    val isLowStock: Boolean get() = availableCopies in 1..2
    val borrowedCopies: Int get() = (totalCopies - availableCopies).coerceAtLeast(0)
}

data class Student(
    val id: String,
    val studentId: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val department: String,
    val gradeYear: String,
    val avatarColorIndex: Int = 0
)

enum class LoanStatus {
    ACTIVE,
    RETURNED,
    OVERDUE
}

data class LoanRecord(
    val id: String,
    val bookId: String,
    val bookTitle: String,
    val bookIsbn: String,
    val studentId: String,
    val studentName: String,
    val studentCode: String,
    val borrowDate: String, // YYYY-MM-DD
    val dueDate: String,    // YYYY-MM-DD
    val returnDate: String? = null,
    val status: LoanStatus = LoanStatus.ACTIVE,
    val finePerDay: Double = 0.50,
    val notes: String = ""
) {
    fun daysOverdue(currentDateStr: String = "2026-10-15"): Long {
        if (status == LoanStatus.RETURNED) return 0
        return try {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val due = LocalDate.parse(dueDate, formatter)
            val current = LocalDate.parse(currentDateStr, formatter)
            val diff = ChronoUnit.DAYS.between(due, current)
            if (diff > 0) diff else 0
        } catch (_: Exception) {
            0
        }
    }

    fun fineAmount(currentDateStr: String = "2026-10-15"): Double {
        val days = daysOverdue(currentDateStr)
        return days * finePerDay
    }

    fun isDueSoon(currentDateStr: String = "2026-10-15"): Boolean {
        if (status == LoanStatus.RETURNED) return false
        return try {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val due = LocalDate.parse(dueDate, formatter)
            val current = LocalDate.parse(currentDateStr, formatter)
            val diff = ChronoUnit.DAYS.between(current, due)
            diff in 0..2
        } catch (_: Exception) {
            false
        }
    }
}

enum class ActivityType {
    LOAN_ISSUED,
    BOOK_RETURNED,
    BOOK_ADDED,
    STUDENT_REGISTERED,
    OVERDUE_ALERT
}

data class ActivityItem(
    val id: String,
    val type: ActivityType,
    val title: String,
    val description: String,
    val timeAgo: String
)

data class Librarian(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val department: String = "Central University Library"
)
