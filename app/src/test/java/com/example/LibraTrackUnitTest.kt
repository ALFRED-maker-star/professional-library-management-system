package com.example

import com.example.data.model.LoanRecord
import com.example.data.model.LoanStatus
import com.example.data.repository.LibraryRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LibraTrackUnitTest {

    private lateinit var repository: LibraryRepository

    @Before
    fun setup() {
        repository = LibraryRepository()
    }

    @Test
    fun testLoanRecord_daysOverdueAndFineCalculation() {
        val overdueLoan = LoanRecord(
            id = "LN-TEST1",
            bookId = "BK-101",
            bookTitle = "Clean Architecture",
            bookIsbn = "978-0134494166",
            studentId = "STU-001",
            studentName = "Sophia Martinez",
            studentCode = "CS-2024-041",
            borrowDate = "2026-09-20",
            dueDate = "2026-10-05", // 10 days before 2026-10-15
            status = LoanStatus.OVERDUE,
            finePerDay = 0.50
        )

        assertEquals(10L, overdueLoan.daysOverdue("2026-10-15"))
        assertEquals(5.00, overdueLoan.fineAmount("2026-10-15"), 0.001)
    }

    @Test
    fun testIssueLoan_decrementsCopiesAndRecordsLoan() {
        val initialBook = repository.books.value.first { it.id == "BK-101" }
        val initialCopies = initialBook.availableCopies
        val student = repository.students.value.first()

        val result = repository.issueLoan(
            studentId = student.id,
            bookId = initialBook.id,
            durationDays = 14,
            notes = "Test loan"
        )

        assertTrue(result.isSuccess)
        val loan = result.getOrNull()
        assertNotNull(loan)
        assertEquals("2026-10-29", loan?.dueDate)

        val updatedBook = repository.books.value.first { it.id == "BK-101" }
        assertEquals(initialCopies - 1, updatedBook.availableCopies)
    }

    @Test
    fun testReturnLoan_restocksInventoryAndMarksReturned() {
        // Use an active loan from the repository
        val activeLoan = repository.loans.value.first { it.status == LoanStatus.ACTIVE }
        val bookBefore = repository.books.value.first { it.id == activeLoan.bookId }

        val result = repository.returnLoan(activeLoan.id, "Returned on time")
        assertTrue(result.isSuccess)

        val updatedLoan = repository.loans.value.first { it.id == activeLoan.id }
        assertEquals(LoanStatus.RETURNED, updatedLoan.status)

        val bookAfter = repository.books.value.first { it.id == activeLoan.bookId }
        assertEquals(bookBefore.availableCopies + 1, bookAfter.availableCopies)
    }
}
