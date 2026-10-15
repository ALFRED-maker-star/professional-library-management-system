package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Librarian
import com.example.ui.theme.AccentGold
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavyPrimaryDark
import com.example.ui.theme.NavyPrimaryLight
import com.example.ui.theme.OverdueRed
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

enum class NavItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen,
    val testTag: String
) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, Screen.Dashboard, "nav_dashboard"),
    BOOKS("Book Catalog", Icons.Default.Book, Screen.BookCatalog, "nav_books"),
    STUDENTS("Students", Icons.Default.People, Screen.Students, "nav_students"),
    CIRCULATION("Borrow & Return", Icons.Default.SwapHoriz, Screen.BorrowReturn(), "nav_circulation"),
    OVERDUE("Overdue List", Icons.Default.Warning, Screen.OverdueList, "nav_overdue"),
    REPORTS("Reports", Icons.Default.Assessment, Screen.Reports, "nav_reports")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppResponsiveShell(
    currentScreen: Screen,
    currentLibrarian: Librarian?,
    overdueCount: Int,
    feedbackMessage: String?,
    onClearFeedback: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(feedbackMessage) {
        if (!feedbackMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(feedbackMessage)
            onClearFeedback()
        }
    }

    // If on Login screen, render clean full bleed without sidebar
    if (currentScreen is Screen.Login) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateBackground)
        ) {
            content()
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        if (isWideScreen) {
            // Tablet / Desktop sidebar layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SlateBackground)
            ) {
                SidebarContent(
                    currentScreen = currentScreen,
                    currentLibrarian = currentLibrarian,
                    overdueCount = overdueCount,
                    onNavigate = onNavigate,
                    onLogout = onLogout,
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight()
                )
                // Main content surface
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    content()
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    )
                }
            }
        } else {
            // Compact / Mobile view: Modal Drawer + Top App Bar + Bottom Nav
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = NavyPrimaryDark,
                        modifier = Modifier.width(280.dp)
                    ) {
                        SidebarContent(
                            currentScreen = currentScreen,
                            currentLibrarian = currentLibrarian,
                            overdueCount = overdueCount,
                            onNavigate = { screen ->
                                onNavigate(screen)
                                scope.launch { drawerState.close() }
                            },
                            onLogout = {
                                scope.launch { drawerState.close() }
                                onLogout()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "LibraTrack",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = getScreenTitle(currentScreen),
                                        fontSize = 12.sp,
                                        color = Color(0xFF93C5FD)
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("drawer_menu_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Open Navigation Menu",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = NavyPrimary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            val mainNav = listOf(
                                NavItem.DASHBOARD,
                                NavItem.BOOKS,
                                NavItem.CIRCULATION,
                                NavItem.OVERDUE,
                                NavItem.REPORTS
                            )
                            mainNav.forEach { item ->
                                val selected = isNavSelected(item, currentScreen)
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { onNavigate(item.screen) },
                                    icon = {
                                        Box {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.title
                                            )
                                            if (item == NavItem.OVERDUE && overdueCount > 0) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(OverdueRed)
                                                        .align(Alignment.TopEnd)
                                                )
                                            }
                                        }
                                    },
                                    label = { Text(item.title, fontSize = 10.sp, maxLines = 1) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyPrimary,
                                        selectedTextColor = NavyPrimary,
                                        indicatorColor = Color(0xFFDBEAFE),
                                        unselectedIconColor = SlateTextSecondary,
                                        unselectedTextColor = SlateTextSecondary
                                    ),
                                    modifier = Modifier.testTag("bottom_${item.testTag}")
                                )
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = SlateBackground
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarContent(
    currentScreen: Screen,
    currentLibrarian: Librarian?,
    overdueCount: Int,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E3A8A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // App Branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2563EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = "LibraTrack",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "LIBRARY ADMIN SUITE",
                    color = Color(0xFF93C5FD),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color(0x22FFFFFF))
        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Items
        Text(
            text = "MAIN NAVIGATION",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        NavItem.entries.forEach { item ->
            val isSelected = isNavSelected(item, currentScreen)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) Color(0xFF2563EB) else Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onNavigate(item.screen) }
                    .testTag(item.testTag)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) Color.White else Color(0xFFCBD5E1),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = item.title,
                            color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    if (item == NavItem.OVERDUE && overdueCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentGold)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = overdueCount.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // System Status Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x1AFFFFFF))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Text(
                    text = "Catalog Online • Term Fall '26",
                    color = Color(0xFFE2E8F0),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0x22FFFFFF))
        Spacer(modifier = Modifier.height(12.dp))

        // Librarian profile card & logout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x1A000000))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                StudentAvatar(
                    name = currentLibrarian?.name ?: "Admin",
                    colorIndex = 1,
                    size = 36.dp
                )
                Column {
                    Text(
                        text = currentLibrarian?.name ?: "Dr. Eleanor Vance",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Text(
                        text = currentLibrarian?.role ?: "Chief Librarian",
                        color = Color(0xFF93C5FD),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("logout_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = Color(0xFFF87171),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun isNavSelected(item: NavItem, currentScreen: Screen): Boolean {
    return when (item) {
        NavItem.DASHBOARD -> currentScreen is Screen.Dashboard
        NavItem.BOOKS -> currentScreen is Screen.BookCatalog || currentScreen is Screen.BookDetail || currentScreen is Screen.AddEditBook
        NavItem.STUDENTS -> currentScreen is Screen.Students
        NavItem.CIRCULATION -> currentScreen is Screen.BorrowReturn
        NavItem.OVERDUE -> currentScreen is Screen.OverdueList
        NavItem.REPORTS -> currentScreen is Screen.Reports
    }
}

private fun getScreenTitle(screen: Screen): String {
    return when (screen) {
        Screen.Login -> "Admin Login"
        Screen.Dashboard -> "Executive Dashboard"
        Screen.BookCatalog -> "Catalog Management"
        is Screen.BookDetail -> "Book Details"
        is Screen.AddEditBook -> if (screen.bookId == null) "Add New Book" else "Edit Book"
        Screen.Students -> "Student Directory"
        is Screen.BorrowReturn -> "Circulation Desk"
        Screen.OverdueList -> "Overdue & Penalties"
        Screen.Reports -> "Circulation Reports"
    }
}
