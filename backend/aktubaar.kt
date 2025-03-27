package com.example.aktubaar
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.ArrowBack
import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

class MainViewModel(private val context: Context) : ViewModel() {
    private val _userName = MutableStateFlow<String>("User")
    val userName: StateFlow<String> = _userName

    private val _email = MutableStateFlow<String>("")
    val email: StateFlow<String> = _email

    private val _isDarkMode = MutableStateFlow<Boolean>(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    init {
        val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
        _userName.value = sharedPreferences.getString("userName", "User") ?: "User"
        _email.value = sharedPreferences.getString("email", "") ?: ""
        _isDarkMode.value = sharedPreferences.getBoolean("darkMode", false)
    }

    fun updateProfile(name: String, email: String) {
        val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("userName", name)
            putString("email", email)
            apply()
        }
        _userName.value = name
        _email.value = email
    }

    fun toggleDarkMode(isDarkMode: Boolean) {
        val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("darkMode", isDarkMode)
            apply()
        }
        _isDarkMode.value = isDarkMode
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun AktuBaarTheme(
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkMode) {
        darkColorScheme(
            primary = Color(0xFF4A90E2),
            onPrimary = Color.White,
            secondary = Color(0xFF50E3C2),
            onSecondary = Color.Black,
            background = Color(0xFF1A1A1A),
            onBackground = Color.White,
            surface = Color(0xFF2E2E2E),
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF4A90E2),
            onPrimary = Color.White,
            secondary = Color(0xFF50E3C2),
            onSecondary = Color.Black,
            background = Color(0xFFF5F7FA),
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentDestination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val items = listOf(
            BottomNavItem("Home", Icons.Default.Home, "home"),
            BottomNavItem("AKTU Info", Icons.Default.Info, "aktu_info"),
            BottomNavItem("QBank", Icons.Default.QuestionAnswer, "question_bank"),
            BottomNavItem("Community", Icons.Default.Group, "community")
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                animationSpec = tween(200)
            )
            val labelColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                animationSpec = tween(200)
            )
            val iconSize by animateDpAsState(
                targetValue = if (isSelected) 28.dp else 24.dp,
                animationSpec = tween(200)
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier = Modifier.size(iconSize)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = labelColor
                    )
                },
                selected = isSelected,
                onClick = {
                    scope.launch {
                        navController.safeNavigate(item.route, snackbarHostState, scope)
                    }
                },
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(context.applicationContext))
    val navController: NavHostController = rememberNavController()
    val userName by mainViewModel.userName.collectAsState()
    val email by mainViewModel.email.collectAsState()
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()
    val systemDarkMode = isSystemInDarkTheme()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (!mainViewModel.isDarkMode.value) {
            mainViewModel.toggleDarkMode(systemDarkMode)
        }
    }

    AktuBaarTheme(isDarkMode = isDarkMode) {
        ModalNavigationDrawer(
            drawerContent = {
                DrawerContent(
                    navController = navController,
                    drawerState = drawerState,
                    userName = userName,
                    email = email,
                    isDarkMode = isDarkMode,
                    onDarkModeToggle = { mainViewModel.toggleDarkMode(!isDarkMode) },
                    onProfileUpdate = { name, email -> mainViewModel.updateProfile(name, email) },
                    snackbarHostState = snackbarHostState
                )
            },
            drawerState = drawerState,
            gesturesEnabled = true,
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                bottomBar = {
                    val currentRoute by navController.currentBackStackEntryAsState()
                    val showBottomNav = currentRoute?.destination?.route in listOf(
                        "home", "aktu_info", "question_bank", "community", "syllabus",
                        "feedback", "report_issue", "about", "settings", "profile"
                    )
                    if (showBottomNav) BottomNavigationBar(navController)
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("splash") {
                        SplashScreen(navController) { name ->
                            mainViewModel.updateProfile(name, mainViewModel.email.value)
                        }
                    }
                    composable("home") {
                        HomeScreen(navController, userName, email, drawerState, scope)
                    }
                    composable("aktu_info") { AktuInfoScreen(navController) }
                    composable("question_bank") { QuestionBankScreen(navController) }
                    composable("community") { CommunityScreen(navController) }
                    composable("about") { AboutScreen(navController) }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            userName = userName,
                            email = email,
                            mainViewModel = mainViewModel,
                            onProfileUpdate = { name, email -> mainViewModel.updateProfile(name, email) },
                            onDarkModeToggle = { mainViewModel.toggleDarkMode(!isDarkMode) }
                        )
                    }
                    composable("feedback") { FeedbackScreen(navController) }
                    composable("report_issue") { ReportIssueScreen(navController) }
                    composable("syllabus") { SyllabusScreen(navController) }
                    composable("profile") {
                        ProfileScreen(
                            navController = navController,
                            initialName = userName,
                            initialEmail = email,
                            onProfileUpdated = { name, email -> mainViewModel.updateProfile(name, email) }
                        )
                    }
                    composable(
                        "semesters/{branch}",
                        arguments = listOf(navArgument("branch") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val branch = backStackEntry.arguments?.getString("branch") ?: "CSE"
                        SemesterScreen(branch = branch, navController = navController, userName = userName, email = email)
                    }
                    composable(
                        "subjects/{branch}/{semester}",
                        arguments = listOf(
                            navArgument("branch") { type = NavType.StringType },
                            navArgument("semester") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val branch = backStackEntry.arguments?.getString("branch") ?: "CSE"
                        val semester = backStackEntry.arguments?.getInt("semester") ?: 1
                        SubjectScreen(branch = branch, semester = semester, navController = navController, userName = userName, email = email)
                    }
                    composable(
                        "units/{branch}/{semester}/{subject}",
                        arguments = listOf(
                            navArgument("branch") { type = NavType.StringType },
                            navArgument("semester") { type = NavType.IntType },
                            navArgument("subject") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val branch = backStackEntry.arguments?.getString("branch") ?: "CSE"
                        val semester = backStackEntry.arguments?.getInt("semester") ?: 1
                        val subject = backStackEntry.arguments?.getString("subject") ?: ""
                        UnitScreen(branch = branch, semester = semester, subject = subject, navController = navController, userName = userName, email = email)
                    }
                    composable(
                        "qb_semesters/{branch}",
                        arguments = listOf(navArgument("branch") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val branch = backStackEntry.arguments?.getString("branch") ?: "CSE"
                        QuestionBankSemesterScreen(branch = branch, navController = navController, userName = userName, email = email)
                    }
                    composable(
                        "qb_units/{branch}/{semester}",
                        arguments = listOf(
                            navArgument("branch") { type = NavType.StringType },
                            navArgument("semester") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val branch = backStackEntry.arguments?.getString("branch") ?: "CSE"
                        val semester = backStackEntry.arguments?.getInt("semester") ?: 1
                        QuestionBankUnitsScreen(branch = branch, semester = semester, navController = navController, userName = userName, email = email)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavHostController) {
    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(context.applicationContext))
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "About AktuBaar",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Version 1.0",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Privacy Policy: [Your Link Here]",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavHostController) {
    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(context.applicationContext))
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Community Hub",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Connect with peers, share resources, and discuss AKTU-related topics.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AktuInfoScreen(navController: NavHostController) {
    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(context.applicationContext))
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // State to hold the WebView instance
    var webView by remember { mutableStateOf<WebView?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AKTU Info", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (webView?.canGoBack() == true) {
                            webView?.goBack() // Go back in WebView history
                        } else {
                            navController.navigateUp() // Exit screen if no history
                        }
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AndroidView(
            factory = {
                WebView(context).apply {
                    settings.javaScriptEnabled = true // Enable JS for interactivity
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    webViewClient = WebViewClient() // Handle navigation within WebView
                    loadUrl("https://aktu.ac.in/") // Replace with specific info page if needed
                    webView = this // Store the WebView instance
                }
            },
            update = { wv -> webView = wv }, // Update the stored WebView instance
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        )

        // Handle system back button
        BackHandler(enabled = true) {
            if (webView?.canGoBack() == true) {
                webView?.goBack() // Go back in WebView history
            } else {
                navController.navigateUp() // Exit screen if no history
            }
        }
    }

    // Handle network errors
    if (!isNetworkAvailable(context)) {
        LaunchedEffect(Unit) {
            scope.launch {
                snackbarHostState.showSnackbar("No internet connection. Please check your network.")
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    userName: String,
    email: String,
    mainViewModel: MainViewModel,
    onProfileUpdate: (String, String) -> Unit,
    onDarkModeToggle: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        "Name: $userName",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Email: $email",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Appearance",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Dark Mode",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        DarkModeSwitch(isDarkMode = isDarkMode, onToggle = onDarkModeToggle)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "About",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        onClick = { navController.navigate("about") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("About AktuBaar", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    initialName: String,
    initialEmail: String,
    onProfileUpdated: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    val isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .background(if (isDarkMode) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(if (isDarkMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondary, CircleShape)
            ) {
                AsyncImage(
                    model = "https://img.freepik.com/premium-vector/avatar-profile-icon-flat-style-male-user-profile-vector-illustration-isolated-background-man-profile-sign-business-concept_157943-38764.jpg",
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkMode) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                textStyle = TextStyle(color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (isDarkMode) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ),
                isError = name.isBlank(),
                supportingText = {
                    if (name.isBlank()) {
                        Text("Name cannot be empty", color = if (isDarkMode) Color.Red.copy(alpha = 0.7f) else MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkMode) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                textStyle = TextStyle(color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (isDarkMode) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ),
                isError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                supportingText = {
                    if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Text("Enter a valid email", color = if (isDarkMode) Color.Red.copy(alpha = 0.7f) else MaterialTheme.colorScheme.error)
                    }
                }
            )

            Button(
                onClick = {
                    scope.launch {
                        if (name.isNotBlank() && (email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                            onProfileUpdated(name, email)
                            navController.navigateUp()
                            snackbarHostState.showSnackbar("Profile updated successfully")
                        } else {
                            snackbarHostState.showSnackbar("Please fix the errors in the form")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save", Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun DarkModeSwitch(isDarkMode: Boolean, onToggle: () -> Unit) {
    val thumbColor by animateColorAsState(
        targetValue = if (isDarkMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 300)
    )
    val trackColor by animateColorAsState(
        targetValue = if (isDarkMode) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        animationSpec = tween(durationMillis = 300)
    )
    val thumbSize by animateDpAsState(targetValue = if (isDarkMode) 20.dp else 16.dp, animationSpec = tween(durationMillis = 300))

    Switch(
        checked = isDarkMode,
        onCheckedChange = { onToggle() },
        thumbContent = {
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .background(thumbColor, CircleShape)
            )
        },
        colors = SwitchDefaults.colors(
            checkedTrackColor = trackColor,
            uncheckedTrackColor = trackColor,
            checkedThumbColor = thumbColor,
            uncheckedThumbColor = thumbColor
        ),
        modifier = Modifier.padding(4.dp)
    )
}

fun NavController.safeNavigate(route: String, snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
    try {
        navigate(route)
    } catch (e: IllegalArgumentException) {
        scope.launch {
            snackbarHostState.showSnackbar("Navigation failed: Invalid route - $route")
        }
    } catch (e: IllegalStateException) {
        scope.launch {
            snackbarHostState.showSnackbar("Navigation failed: State error")
        }
    } catch (e: Exception) {
        scope.launch {
            snackbarHostState.showSnackbar("Unexpected navigation error occurred")
        }
    }
}