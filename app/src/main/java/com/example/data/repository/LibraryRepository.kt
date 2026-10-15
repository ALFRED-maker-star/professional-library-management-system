package com.example.data.repository

import com.example.data.model.ActivityItem
import com.example.data.model.ActivityType
import com.example.data.model.Book
import com.example.data.model.Librarian
import com.example.data.model.LoanRecord
import com.example.data.model.LoanStatus
import com.example.data.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class LibraryRepository {

    private val currentDate = "2026-10-15"

    private val _books = MutableStateFlow<List<Book>>(getInitialBooks())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _students = MutableStateFlow<List<Student>>(getInitialStudents())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _loans = MutableStateFlow<List<LoanRecord>>(getInitialLoans())
    val loans: StateFlow<List<LoanRecord>> = _loans.asStateFlow()

    private val _activities = MutableStateFlow<List<ActivityItem>>(getInitialActivities())
    val activities: StateFlow<List<ActivityItem>> = _activities.asStateFlow()

    private val _currentLibrarian = MutableStateFlow<Librarian?>(
        Librarian(
            id = "lib-1",
            name = "Dr. Eleanor Vance",
            email = "e.vance@libratrack.edu",
            role = "Chief Librarian",
            department = "Main Library - Administration"
        )
    )
    val currentLibrarian: StateFlow<Librarian?> = _currentLibrarian.asStateFlow()

    fun login(email: String, role: String) {
        val name = when {
            email.contains("marcus", ignoreCase = true) -> "Marcus Thorne"
            email.contains("sarah", ignoreCase = true) -> "Sarah Lin"
            else -> "Dr. Eleanor Vance"
        }
        _currentLibrarian.value = Librarian(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email.ifBlank { "admin@libratrack.edu" },
            role = role,
            department = "Circulation & Resource Center"
        )
    }

    fun logout() {
        _currentLibrarian.value = null
    }

    fun addBook(book: Book) {
        _books.value = listOf(book) + _books.value
        logActivity(
            type = ActivityType.BOOK_ADDED,
            title = "New Title Added",
            description = "\"${book.title}\" (${book.totalCopies} copies) added to shelf ${book.shelfLocation}"
        )
    }

    fun updateBook(updatedBook: Book) {
        _books.value = _books.value.map { if (it.id == updatedBook.id) updatedBook else it }
    }

    fun deleteBook(bookId: String) {
        val book = _books.value.find { it.id == bookId }
        _books.value = _books.value.filterNot { it.id == bookId }
        book?.let {
            logActivity(
                type = ActivityType.BOOK_ADDED,
                title = "Catalog Updated",
                description = "Removed \"${it.title}\" from catalog"
            )
        }
    }

    fun addStudent(student: Student) {
        _students.value = listOf(student) + _students.value
        logActivity(
            type = ActivityType.STUDENT_REGISTERED,
            title = "New Student Registered",
            description = "${student.fullName} (${student.studentId}) registered for ${student.department}"
        )
    }

    fun issueLoan(
        studentId: String,
        bookId: String,
        durationDays: Int = 14,
        notes: String = ""
    ): Result<LoanRecord> {
        val book = _books.value.find { it.id == bookId }
            ?: return Result.failure(Exception("Book not found"))

        if (book.availableCopies <= 0) {
            return Result.failure(Exception("No copies available for \"${book.title}\""))
        }

        val student = _students.value.find { it.id == studentId }
            ?: return Result.failure(Exception("Student not found"))

        val today = LocalDate.parse(currentDate)
        val due = today.plusDays(durationDays.toLong())

        val newLoan = LoanRecord(
            id = "LN-${System.currentTimeMillis().toString().takeLast(6)}",
            bookId = book.id,
            bookTitle = book.title,
            bookIsbn = book.isbn,
            studentId = student.id,
            studentName = student.fullName,
            studentCode = student.studentId,
            borrowDate = today.format(DateTimeFormatter.ISO_LOCAL_DATE),
            dueDate = due.format(DateTimeFormatter.ISO_LOCAL_DATE),
            status = LoanStatus.ACTIVE,
            notes = notes
        )

        // Decrement available copies
        _books.value = _books.value.map {
            if (it.id == bookId) it.copy(availableCopies = (it.availableCopies - 1).coerceAtLeast(0)) else it
        }

        _loans.value = listOf(newLoan) + _loans.value

        logActivity(
            type = ActivityType.LOAN_ISSUED,
            title = "Book Checked Out",
            description = "\"${book.title}\" loaned to ${student.fullName} (Due ${newLoan.dueDate})"
        )

        return Result.success(newLoan)
    }

    fun returnLoan(loanId: String, returnNote: String = ""): Result<Unit> {
        val loan = _loans.value.find { it.id == loanId }
            ?: return Result.failure(Exception("Loan record not found"))

        if (loan.status == LoanStatus.RETURNED) {
            return Result.failure(Exception("Book is already marked returned"))
        }

        val updatedLoan = loan.copy(
            status = LoanStatus.RETURNED,
            returnDate = currentDate,
            notes = if (returnNote.isNotBlank()) "${loan.notes} [Returned: $returnNote]".trim() else loan.notes
        )

        _loans.value = _loans.value.map { if (it.id == loanId) updatedLoan else it }

        // Restock available copy
        _books.value = _books.value.map {
            if (it.id == loan.bookId) {
                it.copy(availableCopies = (it.availableCopies + 1).coerceAtMost(it.totalCopies))
            } else it
        }

        logActivity(
            type = ActivityType.BOOK_RETURNED,
            title = "Book Returned",
            description = "\"${loan.bookTitle}\" returned by ${loan.studentName} into circulation"
        )

        return Result.success(Unit)
    }

    fun settleFineAndReturn(loanId: String): Result<Unit> {
        return returnLoan(loanId, returnNote = "Fine settled in full at circulation desk")
    }

    fun sendOverdueReminder(loanId: String): Result<String> {
        val loan = _loans.value.find { it.id == loanId }
            ?: return Result.failure(Exception("Loan not found"))
        val student = _students.value.find { it.id == loan.studentId }
        val days = loan.daysOverdue(currentDate)
        val fine = String.format("$%.2f", loan.fineAmount(currentDate))

        logActivity(
            type = ActivityType.OVERDUE_ALERT,
            title = "Reminder Notice Sent",
            description = "Overdue notice sent to ${student?.fullName ?: loan.studentName} for \"${loan.bookTitle}\" ($fine fine)"
        )
        return Result.success("Reminder email dispatched to ${student?.email ?: "student"}")
    }

    private fun logActivity(type: ActivityType, title: String, description: String) {
        val newActivity = ActivityItem(
            id = UUID.randomUUID().toString(),
            type = type,
            title = title,
            description = description,
            timeAgo = "Just now"
        )
        _activities.value = listOf(newActivity) + _activities.value.take(20)
    }

    private fun getInitialBooks(): List<Book> = listOf(
        Book(
            id = "BK-101",
            title = "Clean Architecture: A Craftsman's Guide",
            author = "Robert C. Martin",
            isbn = "978-0134494166",
            genre = "Computer Science",
            publisher = "Prentice Hall",
            publishYear = 2017,
            totalCopies = 6,
            availableCopies = 3,
            shelfLocation = "Rack A-04",
            description = "A practical guide to software structure and design rules by industry pioneer Uncle Bob.",
            coverGradientIndex = 0
        ),
        Book(
            id = "BK-102",
            title = "Introduction to Algorithms (CLRS)",
            author = "Thomas H. Cormen, Charles E. Leiserson",
            isbn = "978-0262046305",
            genre = "Computer Science",
            publisher = "MIT Press",
            publishYear = 2022,
            totalCopies = 8,
            availableCopies = 2,
            shelfLocation = "Rack A-01",
            description = "The definitive international textbook covering a comprehensive spectrum of modern algorithms.",
            coverGradientIndex = 1
        ),
        Book(
            id = "BK-103",
            title = "Design Patterns: Elements of Reusable Object-Oriented Software",
            author = "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides",
            isbn = "978-0201633610",
            genre = "Computer Science",
            publisher = "Addison-Wesley",
            publishYear = 1994,
            totalCopies = 5,
            availableCopies = 0,
            shelfLocation = "Rack A-07",
            description = "The Gang of Four classic on 23 fundamental software patterns that shaped modern computing.",
            coverGradientIndex = 2
        ),
        Book(
            id = "BK-104",
            title = "Sapiens: A Brief History of Humankind",
            author = "Yuval Noah Harari",
            isbn = "978-0062316097",
            genre = "History",
            publisher = "Harper",
            publishYear = 2015,
            totalCopies = 7,
            availableCopies = 4,
            shelfLocation = "Rack H-12",
            description = "A narrative spanning human evolution from foraging hunter-gatherers to the 21st century.",
            coverGradientIndex = 3
        ),
        Book(
            id = "BK-105",
            title = "Principles of Economics",
            author = "N. Gregory Mankiw",
            isbn = "978-0357038314",
            genre = "Economics",
            publisher = "Cengage Learning",
            publishYear = 2020,
            totalCopies = 10,
            availableCopies = 6,
            shelfLocation = "Rack E-02",
            description = "The most widely used introduction to microeconomic and macroeconomic theory and policy.",
            coverGradientIndex = 4
        ),
        Book(
            id = "BK-106",
            title = "To Kill a Mockingbird",
            author = "Harper Lee",
            isbn = "978-0060935467",
            genre = "Literature",
            publisher = "J. B. Lippincott",
            publishYear = 1960,
            totalCopies = 12,
            availableCopies = 7,
            shelfLocation = "Rack L-03",
            description = "Pulitzer Prize-winning masterpiece exploring empathy, racism, and integrity in the American South.",
            coverGradientIndex = 5
        ),
        Book(
            id = "BK-107",
            title = "Artificial Intelligence: A Modern Approach",
            author = "Stuart Russell, Peter Norvig",
            isbn = "978-0134610993",
            genre = "Computer Science",
            publisher = "Pearson",
            publishYear = 2020,
            totalCopies = 6,
            availableCopies = 1,
            shelfLocation = "Rack A-09",
            description = "The leading textbook in AI, exploring intelligent agents, neural networks, and decision theory.",
            coverGradientIndex = 0
        ),
        Book(
            id = "BK-108",
            title = "Gray's Anatomy: The Anatomical Basis of Clinical Practice",
            author = "Susan Standring",
            isbn = "978-0702077050",
            genre = "Medicine",
            publisher = "Elsevier",
            publishYear = 2021,
            totalCopies = 4,
            availableCopies = 2,
            shelfLocation = "Rack M-01",
            description = "The gold standard anatomical reference for medical students, clinicians, and surgeons.",
            coverGradientIndex = 1
        ),
        Book(
            id = "BK-109",
            title = "A Brief History of Time",
            author = "Stephen Hawking",
            isbn = "978-0553380163",
            genre = "Science",
            publisher = "Bantam Books",
            publishYear = 1988,
            totalCopies = 9,
            availableCopies = 5,
            shelfLocation = "Rack S-05",
            description = "Iconic exploration of cosmology, black holes, space-time, and the origin of the cosmos.",
            coverGradientIndex = 2
        ),
        Book(
            id = "BK-110",
            title = "The Republic",
            author = "Plato",
            isbn = "978-0140455526",
            genre = "Philosophy",
            publisher = "Penguin Classics",
            publishYear = 2007,
            totalCopies = 8,
            availableCopies = 5,
            shelfLocation = "Rack P-04",
            description = "Foundational Socratic dialogue concerning justice, morality, and the ideal philosophical city-state.",
            coverGradientIndex = 3
        ),
        Book(
            id = "BK-111",
            title = "Database System Concepts",
            author = "Abraham Silberschatz, Henry F. Korth",
            isbn = "978-0078022159",
            genre = "Computer Science",
            publisher = "McGraw-Hill",
            publishYear = 2019,
            totalCopies = 6,
            availableCopies = 3,
            shelfLocation = "Rack A-11",
            description = "Comprehensive study of relational algebra, SQL optimization, and distributed database systems.",
            coverGradientIndex = 4
        ),
        Book(
            id = "BK-112",
            title = "The Concept of Law",
            author = "H. L. A. Hart",
            isbn = "978-0199644704",
            genre = "Law",
            publisher = "Oxford University Press",
            publishYear = 2012,
            totalCopies = 5,
            availableCopies = 4,
            shelfLocation = "Rack J-02",
            description = "Preeminent treatise on legal positivism, social rules, and jurisprudence in modern legal thought.",
            coverGradientIndex = 5
        )
    )

    private fun getInitialStudents(): List<Student> = listOf(
        Student(
            id = "STU-001",
            studentId = "CS-2024-041",
            fullName = "Sophia Martinez",
            email = "sophia.m@student.edu",
            phone = "+1 (555) 234-8901",
            department = "Computer Science",
            gradeYear = "Junior (3rd Year)",
            avatarColorIndex = 0
        ),
        Student(
            id = "STU-002",
            studentId = "ME-2023-112",
            fullName = "Liam O'Connor",
            email = "liam.oc@student.edu",
            phone = "+1 (555) 345-6712",
            department = "Mechanical Engineering",
            gradeYear = "Senior (4th Year)",
            avatarColorIndex = 1
        ),
        Student(
            id = "STU-003",
            studentId = "EC-2025-089",
            fullName = "Amina Zahra",
            email = "amina.z@student.edu",
            phone = "+1 (555) 876-1209",
            department = "Economics",
            gradeYear = "Sophomore (2nd Year)",
            avatarColorIndex = 2
        ),
        Student(
            id = "STU-004",
            studentId = "MD-2023-005",
            fullName = "Ethan Tremblay",
            email = "ethan.t@student.edu",
            phone = "+1 (555) 901-4432",
            department = "Medicine",
            gradeYear = "Graduate (MD Year 2)",
            avatarColorIndex = 3
        ),
        Student(
            id = "STU-005",
            studentId = "CS-2025-201",
            fullName = "Devon Patel",
            email = "devon.p@student.edu",
            phone = "+1 (555) 432-9870",
            department = "Computer Science",
            gradeYear = "Sophomore (2nd Year)",
            avatarColorIndex = 4
        ),
        Student(
            id = "STU-006",
            studentId = "LT-2024-033",
            fullName = "Clara Beauchamp",
            email = "clara.b@student.edu",
            phone = "+1 (555) 678-3214",
            department = "Literature",
            gradeYear = "Junior (3rd Year)",
            avatarColorIndex = 5
        ),
        Student(
            id = "STU-007",
            studentId = "LW-2024-019",
            fullName = "Marcus Washington",
            email = "marcus.w@student.edu",
            phone = "+1 (555) 789-0123",
            department = "Law",
            gradeYear = "2L (Law School)",
            avatarColorIndex = 0
        ),
        Student(
            id = "STU-008",
            studentId = "PH-2026-004",
            fullName = "Elena Rostova",
            email = "elena.r@student.edu",
            phone = "+1 (555) 512-8876",
            department = "Philosophy",
            gradeYear = "Freshman (1st Year)",
            avatarColorIndex = 1
        )
    )

    private fun getInitialLoans(): List<LoanRecord> = listOf(
        // Active loans (on track)
        LoanRecord(
            id = "LN-840121",
            bookId = "BK-101",
            bookTitle = "Clean Architecture: A Craftsman's Guide",
            bookIsbn = "978-0134494166",
            studentId = "STU-001",
            studentName = "Sophia Martinez",
            studentCode = "CS-2024-041",
            borrowDate = "2026-10-08",
            dueDate = "2026-10-22",
            status = LoanStatus.ACTIVE,
            notes = "Standard loan for SE402 project"
        ),
        LoanRecord(
            id = "LN-840122",
            bookId = "BK-105",
            bookTitle = "Principles of Economics",
            bookIsbn = "978-0357038314",
            studentId = "STU-003",
            studentName = "Amina Zahra",
            studentCode = "EC-2025-089",
            borrowDate = "2026-10-10",
            dueDate = "2026-10-24",
            status = LoanStatus.ACTIVE,
            notes = "Midterm preparation"
        ),
        // Active loan due today
        LoanRecord(
            id = "LN-840123",
            bookId = "BK-109",
            bookTitle = "A Brief History of Time",
            bookIsbn = "978-0553380163",
            studentId = "STU-002",
            studentName = "Liam O'Connor",
            studentCode = "ME-2023-112",
            borrowDate = "2026-10-01",
            dueDate = "2026-10-15",
            status = LoanStatus.ACTIVE,
            notes = "Extended reading assignment"
        ),
        // Overdue Loans (Actionable with fines!)
        LoanRecord(
            id = "LN-840098",
            bookId = "BK-103",
            bookTitle = "Design Patterns: Elements of Reusable Object-Oriented Software",
            bookIsbn = "978-0201633610",
            studentId = "STU-005",
            studentName = "Devon Patel",
            studentCode = "CS-2025-201",
            borrowDate = "2026-09-20",
            dueDate = "2026-10-04", // 11 days overdue
            status = LoanStatus.OVERDUE,
            notes = "First reminder sent Oct 7"
        ),
        LoanRecord(
            id = "LN-840082",
            bookId = "BK-108",
            bookTitle = "Gray's Anatomy: The Anatomical Basis of Clinical Practice",
            bookIsbn = "978-0702077050",
            studentId = "STU-004",
            studentName = "Ethan Tremblay",
            studentCode = "MD-2023-005",
            borrowDate = "2026-09-15",
            dueDate = "2026-09-29", // 16 days overdue
            status = LoanStatus.OVERDUE,
            notes = "Clinical clerkship reference copy"
        ),
        LoanRecord(
            id = "LN-840071",
            bookId = "BK-102",
            bookTitle = "Introduction to Algorithms (CLRS)",
            bookIsbn = "978-0262046305",
            studentId = "STU-001",
            studentName = "Sophia Martinez",
            studentCode = "CS-2024-041",
            borrowDate = "2026-09-25",
            dueDate = "2026-10-09", // 6 days overdue
            status = LoanStatus.OVERDUE,
            notes = "Competitive programming team study"
        ),
        // Past Returned Loans
        LoanRecord(
            id = "LN-840050",
            bookId = "BK-106",
            bookTitle = "To Kill a Mockingbird",
            bookIsbn = "978-0060935467",
            studentId = "STU-006",
            studentName = "Clara Beauchamp",
            studentCode = "LT-2024-033",
            borrowDate = "2026-09-10",
            dueDate = "2026-09-24",
            returnDate = "2026-09-22",
            status = LoanStatus.RETURNED,
            notes = "Returned in pristine condition"
        ),
        LoanRecord(
            id = "LN-840044",
            bookId = "BK-110",
            bookTitle = "The Republic",
            bookIsbn = "978-0140455526",
            studentId = "STU-008",
            studentName = "Elena Rostova",
            studentCode = "PH-2026-004",
            borrowDate = "2026-09-05",
            dueDate = "2026-09-19",
            returnDate = "2026-09-18",
            status = LoanStatus.RETURNED,
            notes = "Returned on time"
        )
    )

    private fun getInitialActivities(): List<ActivityItem> = listOf(
        ActivityItem(
            id = "ACT-1",
            type = ActivityType.LOAN_ISSUED,
            title = "Book Checked Out",
            description = "\"Clean Architecture\" loaned to Sophia Martinez (CS-2024-041)",
            timeAgo = "12m ago"
        ),
        ActivityItem(
            id = "ACT-2",
            type = ActivityType.BOOK_RETURNED,
            title = "Book Checked In",
            description = "\"To Kill a Mockingbird\" returned by Clara Beauchamp",
            timeAgo = "1h ago"
        ),
        ActivityItem(
            id = "ACT-3",
            type = ActivityType.OVERDUE_ALERT,
            title = "Overdue Alert Generated",
            description = "3 active loans passed due date with accrued penalties",
            timeAgo = "3h ago"
        ),
        ActivityItem(
            id = "ACT-4",
            type = ActivityType.BOOK_ADDED,
            title = "Inventory Received",
            description = "4 new reference volumes of Gray's Anatomy cataloged",
            timeAgo = "Yesterday"
        ),
        ActivityItem(
            id = "ACT-5",
            type = ActivityType.STUDENT_REGISTERED,
            title = "Patron Onboarded",
            description = "Elena Rostova (Philosophy Department) library card activated",
            timeAgo = "2 days ago"
        )
    )
}
