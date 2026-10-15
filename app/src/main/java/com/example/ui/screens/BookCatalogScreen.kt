package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Book
import com.example.ui.components.AvailabilityStatusBadge
import com.example.ui.components.BookCoverBox
import com.example.ui.theme.AccentGold
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
fun BookCatalogScreen(
    books: List<Book>,
    searchQuery: String,
    selectedGenre: String,
    statusFilter: String,
    onSearchChange: (String) -> Unit,
    onGenreSelect: (String) -> Unit,
    onStatusSelect: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onDeleteBook: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val genres = listOf("All", "Computer Science", "Economics", "History", "Literature", "Medicine", "Philosophy", "Science", "Law")
    val statuses = listOf("All", "Available", "Low Stock", "Checked Out")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & "+ Add Book"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Book Catalog",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimaryDark
                )
                Text(
                    text = "${books.size} books matching current criteria",
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )
            }

            Button(
                onClick = { onNavigate(Screen.AddEditBook()) },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_book_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ Add Book", fontWeight = FontWeight.Bold)
            }
        }

        // Search bar & filters
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search by title, author, or ISBN...") },
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
                        .testTag("catalog_search_input")
                )

                // Genre Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Text(
                            text = "Genre:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextMuted
                        )
                    }
                    items(genres) { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { onGenreSelect(genre) },
                            label = { Text(genre, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Status Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Text(
                            text = "Status:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextMuted
                        )
                    }
                    items(statuses) { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { onStatusSelect(status) },
                            label = { Text(status, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentGold,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
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

                if (books.isEmpty()) {
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
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = SlateTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No books found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            Text(
                                text = "Try modifying your search or filters to see more results.",
                                fontSize = 13.sp,
                                color = SlateTextSecondary
                            )
                        }
                    }
                } else if (isWide) {
                    // Wide Data Table
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateSurfaceVariant)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BOOK TITLE & AUTHOR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SlateTextMuted,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.weight(2.6f)
                            )
                            Text(
                                text = "ISBN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SlateTextMuted,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.weight(1.3f)
                            )
                            Text(
                                text = "GENRE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SlateTextMuted,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = "COPIES (AVAIL/TOTAL)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SlateTextMuted,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.weight(1.4f)
                            )
                            Text(
                                text = "STATUS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SlateTextMuted,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.weight(1.3f)
                            )
                            Text(
                                text = "ACTIONS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SlateTextMuted,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.width(130.dp)
                            )
                        }
                        HorizontalDivider(color = SlateBorder)

                        // Table Rows
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(books, key = { it.id }) { book ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigate(Screen.BookDetail(book.id)) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Title & Cover
                                    Row(
                                        modifier = Modifier.weight(2.6f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        BookCoverBox(
                                            title = book.title,
                                            author = book.author,
                                            genre = book.genre,
                                            gradientIndex = book.coverGradientIndex,
                                            width = 38.dp,
                                            height = 52.dp
                                        )
                                        Column {
                                            Text(
                                                text = book.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = SlateTextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${book.author} (${book.publishYear})",
                                                fontSize = 11.sp,
                                                color = SlateTextSecondary,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "Shelf: ${book.shelfLocation}",
                                                fontSize = 10.sp,
                                                color = SlateTextMuted
                                            )
                                        }
                                    }

                                    // ISBN
                                    Text(
                                        text = book.isbn,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = SlateTextSecondary,
                                        modifier = Modifier.weight(1.3f)
                                    )

                                    // Genre
                                    Text(
                                        text = book.genre,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = SlateTextPrimary,
                                        modifier = Modifier.weight(1.2f)
                                    )

                                    // Copies
                                    Column(modifier = Modifier.weight(1.4f)) {
                                        Text(
                                            text = "${book.availableCopies} available",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (book.availableCopies > 0) NavyPrimary else OverdueRed
                                        )
                                        Text(
                                            text = "${book.totalCopies} total copies",
                                            fontSize = 10.sp,
                                            color = SlateTextMuted
                                        )
                                    }

                                    // Status Badge
                                    Box(modifier = Modifier.weight(1.3f)) {
                                        AvailabilityStatusBadge(
                                            availableCopies = book.availableCopies,
                                            totalCopies = book.totalCopies
                                        )
                                    }

                                    // Actions
                                    Row(
                                        modifier = Modifier.width(130.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { onNavigate(Screen.BookDetail(book.id)) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = "View Details",
                                                tint = NavyPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        if (book.availableCopies > 0) {
                                            IconButton(
                                                onClick = { onNavigate(Screen.BorrowReturn(BorrowReturnTab.BORROW)) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.SwapHoriz,
                                                    contentDescription = "Quick Loan",
                                                    tint = AccentGold,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = { onNavigate(Screen.AddEditBook(book.id)) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Book",
                                                tint = SlateTextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteBook(book.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Book",
                                                tint = OverdueRed,
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
                    // Responsive Mobile / Compact Card List with Table Attributes
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(books, key = { it.id }) { book ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(Screen.BookDetail(book.id)) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        BookCoverBox(
                                            title = book.title,
                                            author = book.author,
                                            genre = book.genre,
                                            gradientIndex = book.coverGradientIndex,
                                            width = 54.dp,
                                            height = 76.dp
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = book.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = SlateTextPrimary
                                            )
                                            Text(
                                                text = book.author,
                                                fontSize = 12.sp,
                                                color = SlateTextSecondary
                                            )
                                            Text(
                                                text = "ISBN: ${book.isbn}",
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = SlateTextMuted
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            AvailabilityStatusBadge(
                                                availableCopies = book.availableCopies,
                                                totalCopies = book.totalCopies
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = SlateBorder)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Shelf: ${book.shelfLocation}",
                                            fontSize = 11.sp,
                                            color = SlateTextSecondary
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Button(
                                                onClick = { onNavigate(Screen.BookDetail(book.id)) },
                                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                                modifier = Modifier.height(32.dp),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                                            ) {
                                                Text("Details", fontSize = 11.sp)
                                            }
                                            IconButton(
                                                onClick = { onNavigate(Screen.AddEditBook(book.id)) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
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
}
