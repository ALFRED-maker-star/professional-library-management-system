package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddStudentDialog
import com.example.ui.components.AppResponsiveShell
import com.example.ui.components.ExportReportDialog
import com.example.ui.components.StudentDetailDialog
import com.example.ui.screens.AddEditBookScreen
import com.example.ui.screens.BookCatalogScreen
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.BorrowReturnScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OverdueListScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.theme.LibraTrackTheme
import com.example.ui.viewmodel.LibraryViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraTrackTheme {
                LibraTrackApp()
            }
        }
    }
}

@Composable
fun LibraTrackApp(viewModel: LibraryViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentLibrarian by viewModel.currentLibrarian.collectAsStateWithLifecycle()
    val allBooks by viewModel.books.collectAsStateWithLifecycle()
    val filteredBooks by viewModel.filteredBooks.collectAsStateWithLifecycle()
    val allStudents by viewModel.students.collectAsStateWithLifecycle()
    val filteredStudents by viewModel.filteredStudents.collectAsStateWithLifecycle()
    val allLoans by viewModel.loans.collectAsStateWithLifecycle()
    val activeLoans by viewModel.activeLoans.collectAsStateWithLifecycle()
    val overdueLoans by viewModel.overdueLoans.collectAsStateWithLifecycle()
    val dashboardStats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val activities by viewModel.activities.collectAsStateWithLifecycle()
    val topBooks by viewModel.topBorrowedBooks.collectAsStateWithLifecycle()

    val bookSearchQuery by viewModel.bookSearchQuery.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val bookStatusFilter by viewModel.bookStatusFilter.collectAsStateWithLifecycle()

    val studentSearchQuery by viewModel.studentSearchQuery.collectAsStateWithLifecycle()
    val returnSearchQuery by viewModel.returnSearchQuery.collectAsStateWithLifecycle()
    val overdueSearchQuery by viewModel.overdueSearchQuery.collectAsStateWithLifecycle()

    val snackbarMsg by viewModel.snackBarMessage.collectAsStateWithLifecycle()
    val isAddStudentDialogOpen by viewModel.isAddStudentDialogOpen.collectAsStateWithLifecycle()
    val isExportDialogOpen by viewModel.isExportDialogOpen.collectAsStateWithLifecycle()
    val selectedStudentDetail by viewModel.selectedStudentDetail.collectAsStateWithLifecycle()

    // Android back button handling
    BackHandler(enabled = currentScreen !is Screen.Dashboard && currentScreen !is Screen.Login) {
        viewModel.navigateBack()
    }

    AppResponsiveShell(
        currentScreen = currentScreen,
        currentLibrarian = currentLibrarian,
        overdueCount = dashboardStats.overdueLoansCount,
        feedbackMessage = snackbarMsg,
        onClearFeedback = { viewModel.clearFeedback() },
        onNavigate = { screen -> viewModel.navigateTo(screen) },
        onLogout = { viewModel.logout() }
    ) {
        when (val screen = currentScreen) {
            Screen.Login -> {
                LoginScreen(
                    onLogin = { email, role -> viewModel.login(email, role) }
                )
            }
            Screen.Dashboard -> {
                DashboardScreen(
                    stats = dashboardStats,
                    activities = activities,
                    recentBooks = allBooks,
                    currentLibrarian = currentLibrarian,
                    onNavigate = { dest -> viewModel.navigateTo(dest) },
                    onOpenExport = { viewModel.setExportDialogOpen(true) }
                )
            }
            Screen.BookCatalog -> {
                BookCatalogScreen(
                    books = filteredBooks,
                    searchQuery = bookSearchQuery,
                    selectedGenre = selectedGenre,
                    statusFilter = bookStatusFilter,
                    onSearchChange = { viewModel.setBookSearchQuery(it) },
                    onGenreSelect = { viewModel.setSelectedGenre(it) },
                    onStatusSelect = { viewModel.setBookStatusFilter(it) },
                    onNavigate = { dest -> viewModel.navigateTo(dest) },
                    onDeleteBook = { viewModel.deleteBook(it) }
                )
            }
            is Screen.AddEditBook -> {
                val bookToEdit = allBooks.find { it.id == screen.bookId }
                AddEditBookScreen(
                    bookToEdit = bookToEdit,
                    onSave = { id, title, author, isbn, genre, pub, yr, copies, shelf, desc, covIdx ->
                        viewModel.saveBook(id, title, author, isbn, genre, pub, yr, copies, shelf, desc, covIdx)
                    },
                    onCancel = { viewModel.navigateBack() }
                )
            }
            is Screen.BookDetail -> {
                val book = allBooks.find { it.id == screen.bookId }
                val history = viewModel.getBookHistory(screen.bookId)
                BookDetailScreen(
                    book = book,
                    borrowHistory = history,
                    onBack = { viewModel.navigateBack() },
                    onNavigate = { dest -> viewModel.navigateTo(dest) }
                )
            }
            Screen.Students -> {
                StudentsScreen(
                    students = filteredStudents,
                    searchQuery = studentSearchQuery,
                    onSearchChange = { viewModel.setStudentSearchQuery(it) },
                    getStudentLoanCount = { viewModel.getStudentLoanCount(it) },
                    onStudentClick = { viewModel.setSelectedStudentDetail(it) },
                    onOpenAddStudent = { viewModel.setAddStudentDialogOpen(true) },
                    onNavigate = { dest -> viewModel.navigateTo(dest) }
                )
            }
            is Screen.BorrowReturn -> {
                BorrowReturnScreen(
                    books = allBooks,
                    students = allStudents,
                    activeLoans = activeLoans,
                    initialTab = screen.initialTab,
                    searchQuery = returnSearchQuery,
                    onSearchChange = { viewModel.setReturnSearchQuery(it) },
                    onIssueLoan = { stuId, bkId, days, notes ->
                        viewModel.issueLoan(stuId, bkId, days, notes)
                    },
                    onMarkReturned = { loanId ->
                        viewModel.markBookReturned(loanId)
                    }
                )
            }
            Screen.OverdueList -> {
                OverdueListScreen(
                    overdueLoans = overdueLoans,
                    searchQuery = overdueSearchQuery,
                    onSearchChange = { viewModel.setOverdueSearchQuery(it) },
                    onSettleAndReturn = { loanId ->
                        viewModel.settleFineAndReturn(loanId)
                    },
                    onSendReminder = { loanId ->
                        viewModel.sendReminder(loanId)
                    }
                )
            }
            Screen.Reports -> {
                ReportsScreen(
                    stats = dashboardStats,
                    topBooks = topBooks,
                    monthlyTrends = viewModel.monthlyTrends,
                    onExportClick = { viewModel.setExportDialogOpen(true) }
                )
            }
        }
    }

    // Modal Dialogs
    AddStudentDialog(
        isOpen = isAddStudentDialogOpen,
        onDismiss = { viewModel.setAddStudentDialogOpen(false) },
        onSave = { code, name, email, phone, dept, year ->
            viewModel.createStudent(code, name, email, phone, dept, year)
        }
    )

    StudentDetailDialog(
        student = selectedStudentDetail,
        activeLoans = selectedStudentDetail?.let { viewModel.getStudentLoans(it.id).filter { l -> l.status != com.example.data.model.LoanStatus.RETURNED } } ?: emptyList(),
        onDismiss = { viewModel.setSelectedStudentDetail(null) },
        onReturnBook = { loanId ->
            viewModel.markBookReturned(loanId)
        }
    )

    ExportReportDialog(
        isOpen = isExportDialogOpen,
        onDismiss = { viewModel.setExportDialogOpen(false) },
        books = allBooks,
        loans = allLoans,
        students = allStudents
    )
}
