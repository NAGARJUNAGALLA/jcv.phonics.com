package jcv.phonics.com

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        
        setContent {
            // State to control when to show the splash screen vs the main app
            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {
                SplashScreen(onTimeout = { showSplash = false })
            } else {
                PhonicsApp(tts)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            tts.setSpeechRate(0.85f)
            tts.setPitch(1.2f)
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}

// --- App Color Theme ---
val AppBackground = Color(0xFFF4F9F9)
val ThemeRed = Color(0xFFFF4757)
val ThemeBlue = Color(0xFF1E90FF)
val ThemeGreen = Color(0xFF2ED573)
val ThemeOrange = Color(0xFFFFA502)
val ThemePurple = Color(0xFF9B59B6)
val TextDark = Color(0xFF2F3542)
val StoryBackground = Color(0xFFFFF2CC)

// ==========================================
// SPLASH SCREEN COMPONENT
// ==========================================

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Timer waits 2.5 seconds before triggering the transition to the main app
    LaunchedEffect(Unit) {
        delay(2500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.boy_reading),
                contentDescription = "JCV Phonics Mascot",
                modifier = Modifier.size(250.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "JCV PHONICS",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ThemeBlue,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Learn to Read!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}

// ==========================================
// MAIN APP CONTENT
// ==========================================

@Composable
fun PhonicsApp(tts: TextToSpeech) {
    val currentDay = remember { mutableStateOf(1) }
    val totalDays = 5

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF6B81), shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .padding(vertical = 20.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Phonics Bootcamp", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "🔊 Tap any word or letter to hear it!",
                    modifier = Modifier
                        .background(Color(0xFFFECA57), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    fontSize = 14.sp
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                currentDay = currentDay.value,
                totalDays = totalDays,
                onPrev = { if (currentDay.value > 1) currentDay.value-- },
                onNext = { if (currentDay.value < totalDays) currentDay.value++ }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                MascotHeader(tts)
                Spacer(modifier = Modifier.height(16.dp))

                when (currentDay.value) {
                    1 -> DayOneContent(tts)
                    2 -> DayTwoContent(tts)
                    3 -> DayThreeContent(tts)
                    4 -> DayFourContent(tts)
                    5 -> DayFiveContent(tts)
                }
            }
        }
    }
}

// ==========================================
// MASCOT HEADER COMPONENT
// ==========================================

@Composable
fun MascotHeader(tts: TextToSpeech) {
    val coroutineScope = rememberCoroutineScope()
    var isSpeaking by remember { mutableStateOf(false) }
    val mascotScale by animateFloatAsState(if (isSpeaking) 1.05f else 1f, label = "mascot_bounce")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White, 
            modifier = Modifier
                .size(90.dp)
                .scale(mascotScale)
                .clickable {
                    coroutineScope.launch {
                        isSpeaking = true
                        tts.speak("Let's read together!", TextToSpeech.QUEUE_FLUSH, null, null)
                        delay(1500)
                        isSpeaking = false
                    }
                },
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.boy_reading),
                    contentDescription = "Boy Reading Mascot",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 4.dp),
            color = Color.White,
            border = BorderStroke(2.dp, ThemeBlue),
            shadowElevation = 2.dp,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Hi! I'm ready to read. Tap me to say hello!",
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Bold,
                color = ThemeBlue,
                fontSize = 14.sp
            )
        }
    }
}

// ==========================================
// DAY CONTENT SECTIONS
// ==========================================

@Composable
fun DayOneContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 1", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ThemeRed, letterSpacing = 2.sp)
        Text("Phonic Drill: 'at' and 'an'", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(20.dp))

        SectionBox(borderColor = ThemeRed) {
            Text("Word Family", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LetterBadge("a t", ThemeRed, tts, "at")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Words to Blend", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            WordGrid(listOf("bat", "cat", "fat", "hat", "mat", "pat", "rat", "sat"), tts)
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionBox(borderColor = ThemeBlue) {
            Text("Word Family", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LetterBadge("a n", ThemeBlue, tts, "an")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Words to Blend", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            WordGrid(listOf("ban", "can", "fan", "man", "pan", "ran", "tan", "van"), tts)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("STORY TIME", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = ThemeGreen, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StoryBackground),
            border = BorderStroke(2.dp, ThemeGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TapWord("A cat is on a mat.", tts, modifier = Modifier.fillMaxWidth(), alignLeft = true)
                TapWord("It is a fat cat.", tts, modifier = Modifier.fillMaxWidth(), alignLeft = true)
                TapWord("A man is sitting on the mat.", tts, modifier = Modifier.fillMaxWidth(), alignLeft = true)
                TapWord("The man has a fan.", tts, modifier = Modifier.fillMaxWidth(), alignLeft = true)
            }
        }
    }
}

@Composable
fun DayTwoContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 2", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ThemeOrange, letterSpacing = 2.sp)
        Text("Phonic Drill: 'am', 'ag', 'ap'", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(borderColor = ThemeOrange) {
            Text("Word Family", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LetterBadge("a m", ThemeOrange, tts, "am")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Words to Blend", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            WordGrid(listOf("jam", "ram", "yam", "pam"), tts)
        }
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(borderColor = Theme blue) {
            Text("Word Family", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LetterBadge("a p", ThemeOrange, tts, "ap")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Words to Blend", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            WordGrid(listOf("cap", "gap", "lap", "map","nap","rap","sap","tap"), tts)
        }
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(borderColor = ThemeOrange) {
            Text("Word Family", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LetterBadge("a g", ThemeOrange, tts, "ag")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Words to Blend", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            WordGrid(listOf("bag", "rag", "tag", "wag"), tts)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text("STORY TIME", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = ThemeGreen, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = StoryBackground),
            border = BorderStroke(2.dp, ThemeGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TapWord("A bag is on my lap.", tts, modifier = Modifier.fillMaxWidth(), alignLeft = true)
                TapWord("It is my bag.", tts, modifier = Modifier.fillMaxWidth(), alignLeft = true)
                TapWord("A can of jam is in my bag.", tts, modifier = Modifier.fillMaxWidth(), alignLeft = true)
                TapWord("A tag is on the bag.", tts, modifier = Modifier.fillMaxWidth(), alignLeft = true)
            }
        }
    }
}

@Composable
fun DayThreeContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 3", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ThemeGreen, letterSpacing = 2.sp)
        Text("Phonic Drill: 'ad', 'ab', 'ay', 'as'", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(borderColor = ThemeGreen) {
            Text("Word Family", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LetterBadge("a d", ThemeGreen, tts, "ad")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Words to Blend", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            WordGrid(listOf("bad", "dad", "had", "lad", "mad", "sad"), tts)
        }
    }
}

@Composable
fun DayFourContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 4", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ThemeBlue, letterSpacing = 2.sp)
        Text("Phonic Drill: 'en', 'et', 'ed'", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(borderColor = ThemeBlue) {
            Text("Word Family", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LetterBadge("e n", ThemeBlue, tts, "en")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Words to Blend", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            WordGrid(listOf("hen", "men", "pen", "ten"), tts)
        }
    }
}

@Composable
fun DayFiveContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 5", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ThemePurple, letterSpacing = 2.sp)
        Text("Phonic Drill: 'ig', 'in', 'ip'", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(borderColor = ThemePurple) {
            Text("Word Family", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                LetterBadge("i g", ThemePurple, tts, "ig")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Words to Blend", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            DashedDivider(modifier = Modifier.padding(vertical = 12.dp))
            WordGrid(listOf("big", "dig", "pig", "wig"), tts)
        }
    }
}

// ==========================================
// CUSTOM UI COMPONENTS & ANIMATIONS
// ==========================================

@Composable
fun SectionBox(borderColor: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun LetterBadge(word: String, color: Color, tts: TextToSpeech, phonetic: String) {
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPlaying) 1.15f else 1f, label = "badge_scale")

    Surface(
        modifier = Modifier
            .scale(scale)
            .clickable {
                coroutineScope.launch {
                    isPlaying = true
                    tts.speak(phonetic, TextToSpeech.QUEUE_FLUSH, null, null)
                    delay(600) 
                    isPlaying = false
                }
            },
        color = color,
        shape = RoundedCornerShape(50),
        shadowElevation = if (isPlaying) 8.dp else 4.dp
    ) {
        Text(
            text = word,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun TapWord(
    word: String, 
    tts: TextToSpeech, 
    phoneticPronunciation: String = word, 
    modifier: Modifier = Modifier,
    alignLeft: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(if (isPlaying) 1.1f else 1f, label = "word_scale")
    val textColor by animateColorAsState(if (isPlaying) ThemeRed else TextDark, label = "word_color")
    val shadow by animateDpAsState(if (isPlaying) 6.dp else 1.dp, label = "word_shadow")
    val bgColor by animateColorAsState(if (isPlaying) Color(0xFFFFF2CC) else Color.White, label = "word_bg")

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable {
                coroutineScope.launch {
                    isPlaying = true 
                    tts.speak(phoneticPronunciation, TextToSpeech.QUEUE_FLUSH, null, null)
                    delay(600) 
                    isPlaying = false 
                }
            },
        shape = RoundedCornerShape(25.dp),
        color = bgColor,
        border = BorderStroke(1.dp, Color(0xFFDFE4EA)),
        shadowElevation = shadow
    ) {
        Text(
            text = word,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = textColor,
            textAlign = if (alignLeft) TextAlign.Start else TextAlign.Center
        )
    }
}

@Composable
fun WordGrid(words: List<String>, tts: TextToSpeech) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        words.chunked(2).forEach { rowWords ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowWords.forEach { word ->
                    TapWord(
                        word = word,
                        tts = tts,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowWords.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DashedDivider(modifier: Modifier = Modifier, color: Color = Color(0xFFCED6E0), thickness: Dp = 1.dp) {
    Canvas(modifier = modifier.fillMaxWidth().height(thickness)) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = thickness.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}

@Composable
fun BottomNavigationBar(currentDay: Int, totalDays: Int, onPrev: () -> Unit, onNext: () -> Unit) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPrev, 
                enabled = currentDay > 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3742FA),
                    disabledContainerColor = Color(0xFFCED6E0)
                ),
                shape = RoundedCornerShape(25.dp)
            ) { Text("◀ Prev", fontWeight = FontWeight.Bold) }
            
            Text(
                text = "Day $currentDay / $totalDays", 
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier
                    .background(Color(0xFFF1F2F6), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            Button(
                onClick = onNext, 
                enabled = currentDay < totalDays,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3742FA),
                    disabledContainerColor = Color(0xFFCED6E0)
                ),
                shape = RoundedCornerShape(25.dp)
            ) { Text("Next ▶", fontWeight = FontWeight.Bold) }
        }
    }
}
