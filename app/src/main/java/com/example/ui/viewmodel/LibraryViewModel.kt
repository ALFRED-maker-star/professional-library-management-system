package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActivityItem
import com.example.data.model.Book
import com.example.data.model.Librarian
import com.example.data.model.LoanRecord
import com.example.data.model.LoanStatus
import com.example.data.model.Student
import com.example.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface Screen {
    data object Login : Screen
    data object Dashboard : Screen
    data object BookCatalog : Screen
    data class BookDetail(val bookId: String) : Screen
    data class AddEditBook(val bookId: String? = null) : Screen
    data object Students : Screen
    data class BorrowReturn(val initialTab: BorrowReturnTab = BorrowReturnTab.BORROW) : Screen
    data object OverdueList : Screen
    data object Reports : Screen
}

enum class BorrowReturnTab {
    BORROW,
    RETURN
}

data class DashboardStats(
    val totalBooks: Int = 0,
    val totalUniqueTitles: Int = 0,
    val booksBorrowed: Int = 0,
    val overdueLoansCount: Int = 0,
    val totalStudents: Int = 0,
    val totalOverdueFines: Double = 0.0,
    val returnRatePercent: Int = 94
)

data class BookBorrowStats(
    val book: Book,
    val borrowCount: Int
)

data class MonthlyTrend(
    val month: String,
    val borrowCount: Int,
    val returnCount: Int
)

class LibraryViewModel(
    private val repository: LibraryRepository = LibraryRepository()
) : ViewModel() {

    private val _backStack = MutableStateFlow<List<Screen>>(listOf(Screen.Dashboard))
    val currentScreen: StateFlow<Screen> = _backStack.combine(
        MutableStateFlow(Unit)
    ) { stack, _ -> stack.lastOrNull() ?: Screen.Dashboard }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Dashboard)

    val currentLibrarian: StateFlow<Librarian?> = repository.currentLibrarian
    val books: StateFlow<List<Book>> = repository.books
    val students: StateFlow<List<Student>> = repository.students
    val loans: StateFlow<List<LoanRecord>> = repository.loans
    val activities: StateFlow<List<ActivityItem>> = repository.activities

    // Search and Filters
    private val _bookSearchQuery = MutableStateFlow("")
    val bookSearchQuery: StateFlow<String> = _bookSearchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _bookStatusFilter = MutableStateFlow("All")
    val bookStatusFilter: StateFlow<String> = _bookStatusFilter.asStateFlow()

    private val _studentSearchQuery = MutableStateFlow("")
    val studentSearchQuery: StateFlow<String> = _studentSearchQuery.asStateFlow()

    private val _returnSearchQuery = MutableStateFlow("")
    val returnSearchQuery: StateFlow<String> = _returnSearchQuery.asStateFlow()

    private val _overdueSearchQuery = MutableStateFlow("")
    val overdueSearchQuery: StateFlow<String> = _overdueSearchQuery.asStateFlow()

    // Dialog & Feedback
    private val _snackBarMessage = MutableStateFlow<String?>("Welcome to LibraTrack Admin System")
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    private val _isAddStudentDialogOpen = MutableStateFlow(false)
    val isAddStudentDialogOpen: StateFlow<Boolean> = _isAddStudentDialogOpen.asStateFlow()

    private val _isExportDialogOpen = MutableStateFlow(false)
    val isExportDialogOpen: StateFlow<Boolean> = _isExportDialogOpen.asStateFlow()

    private val _selectedStudentDetail = MutableStateFlow<Student?>(null)
    val selectedStudentDetail: StateFlow<Student?> = _selectedStudentDetail.asStateFlow()

    // Filtered books
    val filteredBooks: StateFlow<List<Book>> = combine(
        books,
        _bookSearchQuery,
        _selectedGenre,
        _bookStatusFilter
    ) { bookList, query, genre, status ->
        bookList.filter { book ->
            val matchesQuery = query.isBlank() ||
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true) ||
                book.isbn.contains(query, ignoreCase = true)

            val matchesGenre = genre == "All" || book.genre.equals(genre, ignoreCase = true)

            val matchesStatus = when (status) {
                "Available" -> book.isAvailable
                "Low Stock" -> book.isLowStock
                "Checked Out" -> book.availableCopies == 0
                else -> true
            }

            matchesQuery && matchesGenre && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered students
    val filteredStudents: StateFlow<List<Student>> = combine(
        students,
        _studentSearchQuery
    ) { studentList, query ->
        if (query.isBlank()) studentList
        else studentList.filter {
            it.fullName.contains(query, ignoreCase = true) ||
            it.studentId.contains(query, ignoreCase = true) ||
            it.department.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active loans for Return Desk
    val activeLoans: StateFlow<List<LoanRecord>> = combine(
        loans,
        _returnSearchQuery
    ) { loanList, query ->
        val active = loanList.filter { it.status != LoanStatus.RETURNED }
        if (query.isBlank()) active
        else active.filter {
            it.bookTitle.contains(query, ignoreCase = true) ||
            it.studentName.contains(query, ignoreCase = true) ||
            it.studentCode.contains(query, ignoreCase = true) ||
            it.bookIsbn.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overdue loans
    val overdueLoans: StateFlow<List<LoanRecord>> = combine(
        loans,
        _overdueSearchQuery
    ) { loanList, query ->
        val overdue = loanList.filter { it.status == LoanStatus.OVERDUE || it.daysOverdue() > 0 }
        if (query.isBlank()) overdue
        else overdue.filter {
            it.bookTitle.contains(query, ignoreCase = true) ||
            it.studentName.contains(query, ignoreCase = true) ||
            it.studentCode.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard aggregated stats
    val dashboardStats: StateFlow<DashboardStats> = combine(
        books,
        loans,
        students
    ) { bList, lList, sList ->
        val totalBooks = bList.sumOf { it.totalCopies }
        val borrowedCount = lList.count { it.status != LoanStatus.RETURNED }
        val overdueList = lList.filter { it.status == LoanStatus.OVERDUE || it.daysOverdue() > 0 }
        val overdueCount = overdueList.size
        val totalFines = overdueList.sumOf { it.fineAmount() }

        DashboardStats(
            totalBooks = totalBooks,
            totalUniqueTitles = bList.size,
            booksBorrowed = borrowedCount,
            overdueLoansCount = overdueCount,
            totalStudents = sList.size,
            totalOverdueFines = totalFines,
            returnRatePercent = if (lList.isEmpty()) 100 else (((lList.size - overdueCount) * 100) / lList.size).coerceIn(0, 100)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Reports: Top borrowed books
    val topBorrowedBooks: StateFlow<List<BookBorrowStats>> = combine(
        books,
        loans
    ) { bList, lList ->
        bList.map { book ->
            val count = lList.count { it.bookId == book.id } + (book.totalCopies - book.availableCopies).coerceAtLeast(0)
            BookBorrowStats(book, count)
        }.sortedByDescending { it.borrowCount }.take(5)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly circulation trends for reports
    val monthlyTrends: List<MonthlyTrend> = listOf(
        MonthlyTrend("May", 145, 138),
        MonthlyTrend("Jun", 98, 92),
        MonthlyTrend("Jul", 62, 59),
        MonthlyTrend("Aug", 112, 105),
        MonthlyTrend("Sep", 230, 210),
        MonthlyTrend("Oct", 195, 172)
    )

    fun navigateTo(screen: Screen) {
        val current = _backStack.value
        if (current.lastOrNull() != screen) {
            _backStack.value = current + screen
        }
    }

    fun navigateBack(): Boolean {
        val current = _backStack.value
        return if (current.size > 1) {
            _backStack.value = current.dropLast(1)
            true
        } else {
            false
        }
    }

    fun login(email: String, role: String) {
        repository.login(email, role)
        navigateTo(Screen.Dashboard)
        showFeedback("Welcome, ${repository.currentLibrarian.value?.name ?: "Admin"}!")
    }

    fun logout() {
        repository.logout()
        _backStack.value = listOf(Screen.Login)
        showFeedback("You have been signed out.")
    }

    fun setBookSearchQuery(q: String) { _bookSearchQuery.value = q }
    fun setSelectedGenre(genre: String) { _selectedGenre.value = genre }
    fun setBookStatusFilter(status: String) { _bookStatusFilter.value = status }

    fun setStudentSearchQuery(q: String) { _studentSearchQuery.value = q }
    fun setReturnSearchQuery(q: String) { _returnSearchQuery.value = q }
    fun setOverdueSearchQuery(q: String) { _overdueSearchQuery.value = q }

    fun setAddStudentDialogOpen(open: Boolean) { _isAddStudentDialogOpen.value = open }
    fun setExportDialogOpen(open: Boolean) { _isExportDialogOpen.value = open }
    fun setSelectedStudentDetail(student: Student?) { _selectedStudentDetail.value = student }

    fun showFeedback(msg: String) {
        _snackBarMessage.value = msg
    }

    fun clearFeedback() {
        _snackBarMessage.value = null
    }

    fun saveBook(
        id: String?,
        title: String,
        author: String,
        isbn: String,
        genre: String,
        publisher: String,
        year: Int,
        totalCopies: Int,
        shelfLocation: String,
        description: String,
        coverGradientIndex: Int
    ): Boolean {
        if (title.isBlank() || author.isBlank() || isbn.isBlank()) {
            showFeedback("Please fill in Title, Author, and ISBN.")
            return false
        }

        if (id == null) {
            val newBook = Book(
                id = "BK-${System.currentTimeMillis().toString().takeLast(4)}",
                title = title.trim(),
                author = author.trim(),
                isbn = isbn.trim(),
                genre = genre.trim().ifBlank { "General" },
                publisher = publisher.trim().ifBlank { "University Press" },
                publishYear = year,
                totalCopies = totalCopies.coerceAtLeast(1),
                availableCopies = totalCopies.coerceAtLeast(1),
                shelfLocation = shelfLocation.trim().ifBlank { "Main Stacks" },
                description = description.trim().ifBlank { "No description available." },
                coverGradientIndex = coverGradientIndex
            )
            repository.addBook(newBook)
            showFeedback("Book \"$title\" added successfully.")
        } else {
            val existing = books.value.find { it.id == id }
            if (existing != null) {
                val copiesDiff = totalCopies - existing.totalCopies
                val newAvailable = (existing.availableCopies + copiesDiff).coerceIn(0, totalCopies)
                val updated = existing.copy(
                    title = title.trim(),
                    author = author.trim(),
                    isbn = isbn.trim(),
                    genre = genre.trim(),
                    publisher = publisher.trim(),
                    publishYear = year,
                    totalCopies = totalCopies,
                    availableCopies = newAvailable,
                    shelfLocation = shelfLocation.trim(),
                    description = description.trim(),
                    coverGradientIndex = coverGradientIndex
                )
                repository.updateBook(updated)
                showFeedback("Book \"$title\" updated.")
            }
        }
        navigateBack()
        return true
    }

    fun deleteBook(bookId: String) {
        val title = books.value.find { it.id == bookId }?.title ?: "Book"
        repository.deleteBook(bookId)
        showFeedback("\"$title\" removed from catalog.")
        navigateBack()
    }

    fun createStudent(
        studentCode: String,
        name: String,
        email: String,
        phone: String,
        dept: String,
        year: String
    ): Boolean {
        if (name.isBlank() || studentCode.isBlank() || email.isBlank()) {
            showFeedback("Name, Student ID, and Email are required.")
            return false
        }
        val newStudent = Student(
            id = "STU-${UUID.randomUUID().toString().take(6)}",
            studentId = studentCode.trim().uppercase(),
            fullName = name.trim(),
            email = email.trim(),
            phone = phone.trim().ifBlank { "+1 (555) 000-0000" },
            department = dept.trim().ifBlank { "General Studies" },
            gradeYear = year.trim().ifBlank { "Undergraduate" },
            avatarColorIndex = (0..5).random()
        )
        repository.addStudent(newStudent)
        _isAddStudentDialogOpen.value = false
        showFeedback("Student ${newStudent.fullName} registered.")
        return true
    }

    fun issueLoan(studentId: String, bookId: String, durationDays: Int, notes: String): Boolean {
        val result = repository.issueLoan(studentId, bookId, durationDays, notes)
        return result.fold(
            onSuccess = { loan ->
                showFeedback("Loan ${loan.id} created! Due on ${loan.dueDate}.")
                navigateTo(Screen.BorrowReturn(BorrowReturnTab.RETURN))
                true
            },
            onFailure = { err ->
                showFeedback(err.message ?: "Failed to issue loan.")
                false
            }
        )
    }

    fun markBookReturned(loanId: String, notes: String = "") {
        val result = repository.returnLoan(loanId, notes)
        result.fold(
            onSuccess = {
                showFeedback("Book return processed. Inventory restocked.")
            },
            onFailure = { err ->
                showFeedback(err.message ?: "Failed to process return.")
            }
        )
    }

    fun settleFineAndReturn(loanId: String) {
        val result = repository.settleFineAndReturn(loanId)
        result.fold(
            onSuccess = {
                showFeedback("Fine settled and book returned into circulation.")
            },
            onFailure = { err ->
                showFeedback(err.message ?: "Action failed.")
            }
        )
    }

    fun sendReminder(loanId: String) {
        val result = repository.sendOverdueReminder(loanId)
        result.fold(
            onSuccess = { msg ->
                showFeedback(msg)
            },
            onFailure = { err ->
                showFeedback(err.message ?: "Could not send reminder.")
            }
        )
    }

    fun getStudentLoanCount(studentId: String): Int {
        return loans.value.count { it.studentId == studentId && it.status != LoanStatus.RETURNED }
    }

    fun getStudentLoans(studentId: String): List<LoanRecord> {
        return loans.value.filter { it.studentId == studentId }
    }

    fun getBookHistory(bookId: String): List<LoanRecord> {
        return loans.value.filter { it.bookId == bookId }
    }
}
