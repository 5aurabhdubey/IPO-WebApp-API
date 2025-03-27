package com.example.aktubaar

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

data class PdfNote(
    val title: String,
    val semester: Int,
    val url: String
)

object NotesData {
    val branchSemesterSubjectNotes: Map<String, Map<Int, Map<String, List<PdfNote>>>> = mapOf(
        "CSE" to mapOf(
            1 to mapOf(
                "Programming" to listOf(
                    PdfNote("Unit 1 - Intro to Programming", 1, "https://drive.google.com/uc?export=download&id=1LwDSXPJ_dHlPr8hYKXSX1P-5X-kVoFap"),
                    PdfNote("Unit 2 - Variables", 1, "https://drive.google.com/uc?export=download&id=1LwDSXPJ_dHlPr8hYKXSX1P-5X-kVoFap")
                ),
                "Data Structures" to listOf(
                    PdfNote("Unit 1 - Arrays Basics", 1, "https://drive.google.com/uc?export=download&id=1_17Dj5A60fcE0ilRI13TvQ9-t03zuYfX"),
                    PdfNote("Unit 2 - Stacks", 1, "https://drive.google.com/uc?export=download&id=1_17Dj5A60fcE0ilRI13TvQ9-t03zuYfX")
                ),
                "Algorithms" to listOf(
                    PdfNote("Unit 1 - Sorting Intro", 1, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_5"),
                    PdfNote("Unit 2 - Searching Basics", 1, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_6")
                )
            ),
            2 to mapOf(
                "Programming" to listOf(
                    PdfNote("Unit 1 - Functions", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_13"),
                    PdfNote("Unit 2 - OOP Basics", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_14")
                ),
                "Data Structures" to listOf(
                    PdfNote("Unit 1 - Queues", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_15"),
                    PdfNote("Unit 2 - Trees", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_16")
                ),
                "Algorithms" to listOf(
                    PdfNote("Unit 1 - Graph Basics", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_17"),
                    PdfNote("Unit 2 - Complexity", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_18")
                )
            )
        ),
        "Textile" to mapOf(
            1 to mapOf(
                "Textile Basics" to listOf(
                    PdfNote("Unit 1 - Fiber Intro", 1, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_7"),
                    PdfNote("Unit 2 - Yarn Basics", 1, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_8")
                ),
                "Weaving" to listOf(
                    PdfNote("Unit 1 - Loom Types", 1, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_9"),
                    PdfNote("Unit 2 - Weave Patterns", 1, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_10")
                ),
                "Dyeing" to listOf(
                    PdfNote("Unit 1 - Dye Types", 1, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_11"),
                    PdfNote("Unit 2 - Dyeing Methods", 1, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_12")
                )
            ),
            2 to mapOf(
                "Textile Basics" to listOf(
                    PdfNote("Unit 1 - Fabric Types", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_19"),
                    PdfNote("Unit 2 - Textile Testing", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_20")
                ),
                "Weaving" to listOf(
                    PdfNote("Unit 1 - Advanced Looms", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_21"),
                    PdfNote("Unit 2 - Complex Patterns", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_22")
                ),
                "Dyeing" to listOf(
                    PdfNote("Unit 1 - Color Theory", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_23"),
                    PdfNote("Unit 2 - Industrial Dyeing", 2, "https://drive.google.com/uc?export=download&id=YOUR_FILE_ID_24")
                )
            )
        ),
        "Electronics" to mapOf(
            1 to mapOf(
                "Circuit Theory" to listOf(
                    PdfNote("Unit 1 - Basic Circuits", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_ID_1"),
                    PdfNote("Unit 2 - Kirchhoff's Laws", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_ID_2")
                ),
                "Digital Electronics" to listOf(
                    PdfNote("Unit 1 - Logic Gates", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_ID_3"),
                    PdfNote("Unit 2 - Flip-Flops", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_ID_4")
                )
            )
        ),
        "Mechanical" to mapOf(
            1 to mapOf(
                "Thermodynamics" to listOf(
                    PdfNote("Unit 1 - Laws of Thermodynamics", 1, "https://drive.google.com/uc?export=download&id=YOUR_MECH_ID_1"),
                    PdfNote("Unit 2 - Heat Transfer", 1, "https://drive.google.com/uc?export=download&id=YOUR_MECH_ID_2")
                ),
                "Mechanics" to listOf(
                    PdfNote("Unit 1 - Statics", 1, "https://drive.google.com/uc?export=download&id=YOUR_MECH_ID_3"),
                    PdfNote("Unit 2 - Dynamics", 1, "https://drive.google.com/uc?export=download&id=YOUR_MECH_ID_4")
                )
            )
        ),
        "Electrical" to mapOf(
            1 to mapOf(
                "Electrical Circuits" to listOf(
                    PdfNote("Unit 1 - Ohm's Law", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_ID_5"),
                    PdfNote("Unit 2 - AC Circuits", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_ID_6")
                ),
                "Power Systems" to listOf(
                    PdfNote("Unit 1 - Generators", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_ID_7"),
                    PdfNote("Unit 2 - Transformers", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_ID_8")
                )
            )
        ),
        "AIML" to mapOf(
            1 to mapOf(
                "Machine Learning" to listOf(
                    PdfNote("Unit 1 - Intro to ML", 1, "https://drive.google.com/uc?export=download&id=YOUR_AIML_ID_1"),
                    PdfNote("Unit 2 - Regression", 1, "https://drive.google.com/uc?export=download&id=YOUR_AIML_ID_2")
                ),
                "AI Basics" to listOf(
                    PdfNote("Unit 1 - AI Concepts", 1, "https://drive.google.com/uc?export=download&id=YOUR_AIML_ID_3"),
                    PdfNote("Unit 2 - Search Algorithms", 1, "https://drive.google.com/uc?export=download&id=YOUR_AIML_ID_4")
                )
            )
        )
    )

    val branchSemesterQuestionBanks: Map<String, Map<Int, List<PdfNote>>> = mapOf(
        "CSE" to mapOf(
            1 to listOf(
                PdfNote("Question Bank 1 - Programming", 1, "https://drive.google.com/uc?export=download&id=YOUR_QB_ID_1"),
                PdfNote("Question Bank 2 - Data Structures", 1, "https://drive.google.com/uc?export=download&id=YOUR_QB_ID_2")
            ),
            2 to listOf(
                PdfNote("Question Bank 1 - Algorithms", 2, "https://drive.google.com/uc?export=download&id=YOUR_QB_ID_3"),
                PdfNote("Question Bank 2 - Programming", 2, "https://drive.google.com/uc?export=download&id=YOUR_QB_ID_4")
            )
        ),
        "Textile" to mapOf(
            1 to listOf(
                PdfNote("Question Bank 1 - Textile Basics", 1, "https://drive.google.com/uc?export=download&id=YOUR_QB_ID_5"),
                PdfNote("Question Bank 2 - Weaving", 1, "https://drive.google.com/uc?export=download&id=YOUR_QB_ID_6")
            ),
            2 to listOf(
                PdfNote("Question Bank 1 - Dyeing", 2, "https://drive.google.com/uc?export=download&id=YOUR_QB_ID_7"),
                PdfNote("Question Bank 2 - Textile Basics", 2, "https://drive.google.com/uc?export=download&id=YOUR_QB_ID_8")
            )
        ),
        "Electronics" to mapOf(
            1 to listOf(
                PdfNote("Question Bank 1 - Circuit Theory", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_QB_ID_1"),
                PdfNote("Question Bank 2 - Digital Electronics", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_QB_ID_2")
            )
        ),
        "Mechanical" to mapOf(
            1 to listOf(
                PdfNote("Question Bank 1 - Thermodynamics", 1, "https://drive.google.com/uc?export=download&id=YOUR_MECH_QB_ID_1"),
                PdfNote("Question Bank 2 - Mechanics", 1, "https://drive.google.com/uc?export=download&id=YOUR_MECH_QB_ID_2")
            )
        ),
        "Electrical" to mapOf(
            1 to listOf(
                PdfNote("Question Bank 1 - Electrical Circuits", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_QB_ID_3"),
                PdfNote("Question Bank 2 - Power Systems", 1, "https://drive.google.com/uc?export=download&id=YOUR_ELEC_QB_ID_4")
            )
        ),
        "AIML" to mapOf(
            1 to listOf(
                PdfNote("Question Bank 1 - Machine Learning", 1, "https://drive.google.com/uc?export=download&id=YOUR_AIML_QB_ID_1"),
                PdfNote("Question Bank 2 - AI Basics", 1, "https://drive.google.com/uc?export=download&id=YOUR_AIML_QB_ID_2")
            )
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    userName: String,
    email: String,
    drawerState1: DrawerState,
    scope1: CoroutineScope
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val viewModel = mainViewModel()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                navController = navController,
                drawerState = drawerState,
                userName = userName,
                email = email,
                isDarkMode = isDarkMode,
                onDarkModeToggle = { viewModel.toggleDarkMode(!isDarkMode) },
                onProfileUpdate = { name, email -> viewModel.updateProfile(name, email) },
                snackbarHostState = snackbarHostState
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AktuBaar",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.shadow(elevation = 8.dp)
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val welcomeAlpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 800, delayMillis = 200),
                    label = "welcomeFadeIn"
                )
                Text(
                    text = "Welcome, $userName!",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.alpha(welcomeAlpha)
                )

                ContentSlider(isDarkMode, navController, scope, snackbarHostState)

                val buttonScale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 600),
                    label = "buttonScale"
                )
                Button(
                    onClick = {
                        scope.launch {
                            navController.safeNavigate("syllabus", snackbarHostState, scope)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(buttonScale),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Syllabus",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Syllabus",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                BranchGrid(navController, snackbarHostState, scope)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContentSlider(
    isDarkMode: Boolean,
    navController: NavHostController,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val sliderItems = listOf(
        SliderItem(
            "image",
            "https://picsum.photos/200/300?random=1",
            "Explore AKTU Updates",
            "Stay informed with the latest news and events."
        ),
        SliderItem(
            "video",
            "https://www.w3schools.com/html/mov_bbb.mp4",
            "Watch Tutorials",
            "Learn with engaging video guides."
        ),
        SliderItem(
            "image",
            "https://picsum.photos/200/300?random=2",
            "Study Resources",
            "Access notes, question banks, and more."
        )
    )

    val pagerState = rememberPagerState(pageCount = { sliderItems.size })
    val autoScrollDuration = 4000L

    LaunchedEffect(pagerState) {
        while (true) {
            delay(autoScrollDuration)
            val nextPage = (pagerState.currentPage + 1) % sliderItems.size
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(1000))
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
    ) { page ->
        val item = sliderItems[page]
        val context = LocalContext.current
        val scale by animateFloatAsState(
            targetValue = if (pagerState.currentPage == page) 1f else 0.95f,
            animationSpec = tween(durationMillis = 500),
            label = "cardScale"
        )

        Card(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .clickable {
                    scope.launch {
                        when (item.type) {
                            "image" -> snackbarHostState.showSnackbar("Clicked on ${item.title}")
                            "video" -> {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse(item.url), "video/*")
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    snackbarHostState.showSnackbar("No video player installed")
                                    Log.e("AktuBaar", "No video player: ${e.message}")
                                }
                            }
                        }
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (item.type == "image") {
                    AsyncImage(
                        model = item.url,
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        onError = { Log.e("AktuBaar", "Failed to load image: ${it.result.throwable?.message}") }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = "Play Video",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(90.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(sliderItems.size) { index ->
            val dotSize by animateDpAsState(
                targetValue = if (pagerState.currentPage == index) 10.dp else 6.dp,
                animationSpec = tween(300),
                label = "dotSize"
            )
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(
                        if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
            )
        }
    }
}

data class SliderItem(
    val type: String,
    val url: String,
    val title: String,
    val description: String
)

@Composable
fun DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    userName: String,
    email: String,
    isDarkMode: Boolean,
    onDarkModeToggle: () -> Unit,
    onProfileUpdate: (String, String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                ) {
                    AsyncImage(
                        model = "https://img.freepik.com/premium-vector/avatar-profile-icon-flat-style-male-user-profile-vector-illustration-isolated-background-man-profile-sign-business-concept_157943-38764.jpg",
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape),
                        onError = { Log.e("AktuBaar", "Failed to load profile image: ${it.result.throwable?.message}") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = userName.takeIf { it.isNotBlank() } ?: "User",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = email.takeIf { it.isNotBlank() } ?: "email@example.com",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DrawerItem("Home", Icons.Default.Home, navController, "home", drawerState, snackbarHostState, scope)
            DrawerItem("About", Icons.Default.Info, navController, "about", drawerState, snackbarHostState, scope)
            DrawerItem("Settings", Icons.Default.Settings, navController, "settings", drawerState, snackbarHostState, scope)
            DrawerItem("Feedback", Icons.Default.Chat, navController, "feedback", drawerState, snackbarHostState, scope)
            DrawerItem("Report an Issue", Icons.Default.BugReport, navController, "report_issue", drawerState, snackbarHostState, scope)

            Button(
                onClick = {
                    scope.launch { navController.safeNavigate("profile", snackbarHostState, scope) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Edit Profile", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(
            onClick = {
                scope.launch {
                    try {
                        with(context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE).edit()) {
                            remove("userName")
                            remove("email")
                            apply()
                        }
                        drawerState.close()
                        navController.safeNavigate("splash", snackbarHostState, scope)
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Logout failed, please try again")
                        Log.e("AktuBaar", "Logout error: ${e.message}")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun DrawerItem(
    text: String,
    icon: ImageVector,
    navController: NavController,
    route: String,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    TextButton(
        onClick = {
            scope.launch {
                drawerState.close()
                navController.safeNavigate(route, snackbarHostState, scope)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 8.dp, end = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BranchGrid(navController: NavController, snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
    val branches = listOf("CSE", "Textile", "Electronics", "Mechanical", "Electrical", "AIML")
    val vibrantColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(branches) { branch ->
            val index = branches.indexOf(branch)
            val alpha by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, delayMillis = index * 100),
                label = "branchFadeIn"
            )
            Card(
                modifier = Modifier
                    .height(120.dp)
                    .alpha(alpha)
                    .clickable {
                        scope.launch {
                            navController.safeNavigate("semesters/$branch", snackbarHostState, scope)
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = vibrantColors[index % vibrantColors.size]
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = branch,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun mainViewModel(): MainViewModel {
    val context = LocalContext.current
    return androidx.lifecycle.viewmodel.compose.viewModel(factory = MainViewModelFactory(context.applicationContext))
}

// Placeholder for safeNavigate (ensure it's defined elsewhere in your project)
fun NavHostController.safeNavigate(route: String, snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
    try {
        navigate(route)
    } catch (e: Exception) {
        scope.launch {
            snackbarHostState.showSnackbar("Navigation failed: ${e.message}")
        }
        Log.e("AktuBaar", "Navigation error: ${e.message}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterScreen(branch: String, navController: NavController, userName: String, email: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                navController = navController,
                drawerState = drawerState,
                userName = userName,
                email = email,
                isDarkMode = isDarkMode,
                onDarkModeToggle = { isDarkMode = !isDarkMode; saveDarkMode(context, isDarkMode) },
                onProfileUpdate = { name, email -> updateProfile(context, name, email) },
                snackbarHostState = snackbarHostState
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "$branch - Semesters",
                            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose a Semester",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
                )
                SemesterGrid(branch = branch, navController = navController, snackbarHostState = snackbarHostState, scope = scope)
            }
        }
    }
}

@Composable
fun SemesterGrid(branch: String, navController: NavController, snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
    val semesters = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(semesters) { semester ->
            Button(
                onClick = {
                    scope.launch {
                        navController.safeNavigate("subjects/$branch/$semester", snackbarHostState, scope)
                    }
                },
                modifier = Modifier.height(100.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Semester $semester",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(branch: String, semester: Int, navController: NavController, userName: String, email: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val subjects = when (branch) {
        "CSE" -> listOf("Programming", "Data Structures", "Algorithms")
        "Textile" -> listOf("Textile Basics", "Weaving", "Dyeing")
        "Electronics" -> listOf("Circuit Theory", "Digital Electronics")
        "Mechanical" -> listOf("Thermodynamics", "Mechanics")
        "Electrical" -> listOf("Electrical Circuits", "Power Systems")
        "AIML" -> listOf("Machine Learning", "AI Basics")
        else -> emptyList()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                navController = navController,
                drawerState = drawerState,
                userName = userName,
                email = email,
                isDarkMode = isDarkMode,
                onDarkModeToggle = { isDarkMode = !isDarkMode; saveDarkMode(context, isDarkMode) },
                onProfileUpdate = { name, email -> updateProfile(context, name, email) },
                snackbarHostState = snackbarHostState
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "$branch - Semester $semester - Subjects",
                            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { subject ->
                        Button(
                            onClick = {
                                scope.launch {
                                    navController.safeNavigate("units/$branch/$semester/$subject", snackbarHostState, scope)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = subject,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitScreen(branch: String, semester: Int, subject: String, navController: NavController, userName: String, email: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val allNotes = remember { NotesData.branchSemesterSubjectNotes[branch]?.get(semester)?.get(subject) ?: emptyList() }
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    val filteredNotes = allNotes.filter { it.title.contains(searchQuery, ignoreCase = true) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                navController = navController,
                drawerState = drawerState,
                userName = userName,
                email = email,
                isDarkMode = isDarkMode,
                onDarkModeToggle = { isDarkMode = !isDarkMode; saveDarkMode(context, isDarkMode) },
                onProfileUpdate = { name, email -> updateProfile(context, name, email) },
                snackbarHostState = snackbarHostState
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "$branch - Sem $semester - $subject - Units",
                            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search units...",
                            color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isDarkMode) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                        cursorColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredNotes) { note ->
                        NoteItem(note = note, snackbarHostState = snackbarHostState, scope = scope)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(navController: NavHostController) {
    val branches = listOf("CSE", "Textile", "Electronics", "Mechanical", "Electrical", "AIML")
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Question Bank",
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select a Branch",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(branches) { branch ->
                    Button(
                        onClick = {
                            scope.launch {
                                navController.safeNavigate("qb_semesters/$branch", snackbarHostState, scope)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = branch,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankSemesterScreen(branch: String, navController: NavController, userName: String, email: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                navController = navController,
                drawerState = drawerState,
                userName = userName,
                email = email,
                isDarkMode = isDarkMode,
                onDarkModeToggle = { isDarkMode = !isDarkMode; saveDarkMode(context, isDarkMode) },
                onProfileUpdate = { name, email -> updateProfile(context, name, email) },
                snackbarHostState = snackbarHostState
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "$branch - Question Bank Semesters",
                            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose a Semester",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
                )
                SemesterGridForQuestionBank(branch = branch, navController = navController, snackbarHostState = snackbarHostState, scope = scope)
            }
        }
    }
}

@Composable
fun SemesterGridForQuestionBank(branch: String, navController: NavController, snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
    val semesters = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(semesters) { semester ->
            Button(
                onClick = {
                    scope.launch {
                        navController.safeNavigate("qb_units/$branch/$semester", snackbarHostState, scope)
                    }
                },
                modifier = Modifier.height(100.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Semester $semester",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankUnitsScreen(branch: String, semester: Int, navController: NavController, userName: String, email: String) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val allQuestionBanks = remember { NotesData.branchSemesterQuestionBanks[branch]?.get(semester) ?: emptyList() }
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    val filteredQuestionBanks = allQuestionBanks.filter { it.title.contains(searchQuery, ignoreCase = true) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(
                navController = navController,
                drawerState = drawerState,
                userName = userName,
                email = email,
                isDarkMode = isDarkMode,
                onDarkModeToggle = { isDarkMode = !isDarkMode; saveDarkMode(context, isDarkMode) },
                onProfileUpdate = { name, email -> updateProfile(context, name, email) },
                snackbarHostState = snackbarHostState
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "$branch - Sem $semester - Question Banks",
                            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search question banks...",
                            color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isDarkMode) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                        cursorColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredQuestionBanks) { note ->
                        NoteItem(note = note, snackbarHostState = snackbarHostState, scope = scope)
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: PdfNote, snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    val isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    try {
                        viewPdf(context, note.url)
                    } catch (e: ActivityNotFoundException) {
                        snackbarHostState.showSnackbar("No PDF viewer installed")
                        Log.e("AktuBaar", "No PDF viewer: ${e.message}")
                    } catch (e: SecurityException) {
                        snackbarHostState.showSnackbar("Permission denied to open PDF")
                        Log.e("AktuBaar", "Security error: ${e.message}")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Failed to open PDF")
                        Log.e("AktuBaar", "PDF open error: ${e.message}")
                    }
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = "PDF Icon",
                    modifier = Modifier.size(40.dp),
                    tint = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = note.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Semester ${note.semester}",
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color.White.copy(alpha = 0.7f) else Color.Gray
                    )
                }
            }
            IconButton(
                onClick = {
                    scope.launch {
                        try {
                            if (!isNetworkAvailable(context)) {
                                snackbarHostState.showSnackbar("No internet connection")
                                return@launch
                            }
                            downloadPdf(note.url, context)
                            snackbarHostState.showSnackbar("Download started")
                        } catch (e: IOException) {
                            snackbarHostState.showSnackbar("Download failed: Network or file error")
                            Log.e("AktuBaar", "Download IOException: ${e.message}")
                        } catch (e: SecurityException) {
                            snackbarHostState.showSnackbar("Permission denied for download")
                            Log.e("AktuBaar", "Download security error: ${e.message}")
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Download failed unexpectedly")
                            Log.e("AktuBaar", "Download unexpected error: ${e.message}")
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

suspend fun downloadPdf(url: String, context: Context) = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Failed to download: ${response.code}")
            val fileName = url.substringAfterLast("/").takeIf { it.isNotBlank() } ?: "document.pdf"
            val file = File(
                context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                    ?: File(context.cacheDir, "downloads"), fileName
            )

            response.body?.byteStream()?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        }
    } catch (e: IOException) {
        Log.e("AktuBaar", "Download IOException: ${e.message}")
        throw e
    } catch (e: Exception) {
        Log.e("AktuBaar", "Download unexpected error: ${e.message}")
        throw e
    }
}

fun viewPdf(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(url), "application/pdf")
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e("AktuBaar", "No PDF viewer found: ${e.message}")
        throw e
    } catch (e: SecurityException) {
        Log.e("AktuBaar", "Security error opening PDF: ${e.message}")
        throw e
    } catch (e: Exception) {
        Log.e("AktuBaar", "PDF view error: ${e.message}")
        throw e
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Feedback",
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "We value your feedback!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
            )
            var feedback by remember { mutableStateOf("") }
            TextField(
                value = feedback,
                onValueChange = { feedback = it },
                label = {
                    Text(
                        text = "Your Feedback",
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        color = if (isDarkMode) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                    cursorColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 5
            )
            Button(
                onClick = {
                    scope.launch {
                        if (feedback.isNotBlank()) {
                            snackbarHostState.showSnackbar("Thank you for your feedback!")
                            navController.navigateUp()
                        } else {
                            snackbarHostState.showSnackbar("Please enter feedback")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Submit",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Report an Issue",
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Report an Issue",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
            )
            var issue by remember { mutableStateOf("") }
            TextField(
                value = issue,
                onValueChange = { issue = it },
                label = {
                    Text(
                        text = "Describe the Issue",
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        color = if (isDarkMode) Color(0xFF2E2E2E) else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface,
                    cursorColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 5
            )
            Button(
                onClick = {
                    scope.launch {
                        if (issue.isNotBlank()) {
                            try {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:support@aktubaar.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Issue Report - AktuBaar")
                                    putExtra(Intent.EXTRA_TEXT, issue)
                                }
                                context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
                                navController.navigateUp()
                            } catch (e: ActivityNotFoundException) {
                                snackbarHostState.showSnackbar("No email app found")
                                Log.e("AktuBaar", "Email error: ${e.message}")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error sending report")
                                Log.e("AktuBaar", "Report issue error: ${e.message}")
                            }
                        } else {
                            snackbarHostState.showSnackbar("Please describe the issue")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Submit",
                    fontSize = 16.sp
                )
            }
        }
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    return try {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } catch (e: Exception) {
        Log.e("AktuBaar", "Network check error: ${e.message}")
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusScreen(navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("darkMode", false)) }

    LaunchedEffect(Unit) {
        scope.launch {
            val pdfUri = Uri.parse("https://example.com/syllabus.pdf") // Replace with actual URL
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                context.startActivity(intent)
                navController.navigateUp()
            } catch (e: ActivityNotFoundException) {
                snackbarHostState.showSnackbar("No PDF viewer installed")
                Log.e("AktuBaar", "No PDF viewer: ${e.message}")
            } catch (e: SecurityException) {
                snackbarHostState.showSnackbar("Permission denied to open PDF")
                Log.e("AktuBaar", "Security error: ${e.message}")
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error opening syllabus")
                Log.e("AktuBaar", "PDF open error: ${e.message}")
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Syllabus",
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = if (isDarkMode) Color(0xFF1A1A1A) else Color(0xFFF5F7FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Opening Syllabus...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

fun saveDarkMode(context: Context, isDarkMode: Boolean) {
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putBoolean("darkMode", isDarkMode)
        apply()
    }
}

fun updateProfile(context: Context, name: String, email: String) {
    val sharedPreferences = context.getSharedPreferences("AktuBaarPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("userName", name)
        putString("email", email)
        apply()
    }
}