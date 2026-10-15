package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Book
import com.example.ui.components.BookCoverBox
import com.example.ui.components.BookCoverGradients
import com.example.ui.theme.AccentGold
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavyPrimaryDark
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary

@Composable
fun AddEditBookScreen(
    bookToEdit: Book?,
    onSave: (
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
    ) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(bookToEdit?.title ?: "") }
    var author by remember { mutableStateOf(bookToEdit?.author ?: "") }
    var isbn by remember { mutableStateOf(bookToEdit?.isbn ?: "") }
    var genre by remember { mutableStateOf(bookToEdit?.genre ?: "Computer Science") }
    var publisher by remember { mutableStateOf(bookToEdit?.publisher ?: "") }
    var yearText by remember { mutableStateOf(bookToEdit?.publishYear?.toString() ?: "2024") }
    var copiesText by remember { mutableStateOf(bookToEdit?.totalCopies?.toString() ?: "5") }
    var shelfLocation by remember { mutableStateOf(bookToEdit?.shelfLocation ?: "Rack A-01") }
    var description by remember { mutableStateOf(bookToEdit?.description ?: "") }
    var coverIndex by remember { mutableIntStateOf(bookToEdit?.coverGradientIndex ?: 0) }

    val isEditing = bookToEdit != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier.testTag("add_book_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NavyPrimary
                )
            }
            Column {
                Text(
                    text = if (isEditing) "Edit Catalog Item" else "Add New Book",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimaryDark
                )
                Text(
                    text = if (isEditing) "Updating metadata for \"${bookToEdit.title}\"" else "Register a new title into the institutional library",
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Book Cover Style Selector with live preview
                Text(
                    text = "COVER ARTWORK & BINDING STYLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextMuted,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BookCoverBox(
                        title = title.ifBlank { "Sample Book Title" },
                        author = author.ifBlank { "Author Name" },
                        genre = genre.ifBlank { "Genre" },
                        gradientIndex = coverIndex,
                        width = 80.dp,
                        height = 115.dp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Choose Cover Palette Theme:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SlateTextSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BookCoverGradients.indices.forEach { index ->
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(BookCoverGradients[index])
                                        .border(
                                            width = if (coverIndex == index) 3.dp else 1.dp,
                                            color = if (coverIndex == index) AccentGold else SlateBorder,
                                            shape = CircleShape
                                        )
                                        .clickable { coverIndex = index },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (coverIndex == index) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = "Select high-contrast binder tone for catalog display.",
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Core Metadata Grid
                Text(
                    text = "CORE METADATA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextMuted,
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("book_title_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author(s) *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("book_author_input")
                    )
                    OutlinedTextField(
                        value = isbn,
                        onValueChange = { isbn = it },
                        label = { Text("ISBN (e.g. 978-0134494166) *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("book_isbn_input")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = genre,
                        onValueChange = { genre = it },
                        label = { Text("Genre / Classification") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("book_genre_input")
                    )
                    OutlinedTextField(
                        value = publisher,
                        onValueChange = { publisher = it },
                        label = { Text("Publisher") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = yearText,
                        onValueChange = { yearText = it },
                        label = { Text("Publication Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = copiesText,
                        onValueChange = { copiesText = it },
                        label = { Text("Total Copies *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("book_copies_input")
                    )
                    OutlinedTextField(
                        value = shelfLocation,
                        onValueChange = { shelfLocation = it },
                        label = { Text("Shelf / Call Number") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Book Synopsis / Abstract") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag("book_cancel_button")
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val year = yearText.toIntOrNull() ?: 2024
                            val copies = copiesText.toIntOrNull() ?: 1
                            onSave(
                                bookToEdit?.id,
                                title,
                                author,
                                isbn,
                                genre,
                                publisher,
                                year,
                                copies,
                                shelfLocation,
                                description,
                                coverIndex
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier.testTag("book_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isEditing) "Save Changes" else "Save Book to Catalog")
                    }
                }
            }
        }
    }
}
