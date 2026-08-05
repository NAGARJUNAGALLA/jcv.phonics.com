package jcv.phonics.com

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        tts = TextToSpeech(this, this)

        setContent {
            PhonicsApp(tts)
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

@Composable
fun PhonicsApp(tts: TextToSpeech) {
    val currentDay = remember { mutableStateOf(1) }
    
    // --- 1. INCREASE TOTAL DAYS HERE ---
    val totalDays = 5 

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF6B81), shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Phonics Bootcamp", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold) 
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🔊 Tap any word or letter to hear it!", 
                    modifier = Modifier
                        .background(Color(0xFFFECA57), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 15.dp, vertical = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
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
                // --- 2. ADD NEW DAYS TO THE ROUTER HERE ---
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

@Composable
fun BottomNavigationBar(currentDay: Int, totalDays: Int, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onPrev, enabled = currentDay > 1) { Text("◀ Prev") } 
        Text("Day $currentDay / $totalDays", fontWeight = FontWeight.Bold) 
        Button(onClick = onNext, enabled = currentDay < totalDays) { Text("Next ▶") } 
    }
}

// ==========================================
// DAY CONTENT SECTIONS
// ==========================================

@Composable
fun DayOneContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 1", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF4757)) 
        Text("Phonic Drill: 'at' and 'an'", color = Color.Gray) 
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(title = "Word Family: at", color = Color(0xFFFF4757)) {
            TapWord("a t", tts, "at") 
            Spacer(modifier = Modifier.height(10.dp))
            Text("Words to Blend", fontWeight = FontWeight.Bold)
            
            // --- 3. STACK MULTIPLE ROWS FOR MORE WORDS ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TapWord("bat", tts) 
                    TapWord("cat", tts) 
                    TapWord("fat", tts) 
                    TapWord("hat", tts)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TapWord("mat", tts)
                    TapWord("pat", tts)
                    TapWord("rat", tts)
                    TapWord("sat", tts)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionBox(title = "Story Time", color = Color(0xFF2ED573)) {
            TapWord("A cat is on a mat.", tts) 
            TapWord("It is a fat cat.", tts) 
            TapWord("A man is sitting on the mat.", tts) 
            TapWord("The man has a fan.", tts) 
        }
    }
}

@Composable
fun DayTwoContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 2", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFA502))
        Text("Phonic Drill: 'am', 'ag', 'ap'", color = Color.Gray) 
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(title = "Word Family: am", color = Color(0xFFFFA502)) {
            TapWord("a m", tts, "am") 
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TapWord("jam", tts) 
                TapWord("ram", tts) 
                TapWord("yam", tts) 
                TapWord("pam", tts) 
            }
        }
    }
}

@Composable
fun DayThreeContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 3", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2ED573))
        Text("Phonic Drill: 'ad', 'ab', 'ay', 'as'", color = Color.Gray) 
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(title = "Word Family: ad", color = Color(0xFF2ED573)) {
            TapWord("a d", tts, "ad") 
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TapWord("bad", tts) 
                    TapWord("dad", tts) 
                    TapWord("had", tts) 
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TapWord("lad", tts) 
                    TapWord("mad", tts) 
                    TapWord("sad", tts) 
                }
            }
        }
    }
}

// --- 4. CREATE NEW DAY FUNCTIONS ---
@Composable
fun DayFourContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 4", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E90FF))
        Text("Phonic Drill: 'en', 'et', 'ed'", color = Color.Gray) 
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(title = "Word Family: en", color = Color(0xFF1E90FF)) {
            TapWord("e n", tts, "en") 
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TapWord("hen", tts) 
                TapWord("men", tts) 
                TapWord("pen", tts) 
                TapWord("ten", tts) 
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TapWord("den", tts) 
                TapWord("ben", tts) 
                TapWord("sen", tts) 
                TapWord("den", tts) 
            }
        }
    }
}

@Composable
fun DayFiveContent(tts: TextToSpeech) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("DAY 5", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9B59B6))
        Text("Phonic Drill: 'ig', 'in', 'ip'", color = Color.Gray) 
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionBox(title = "Word Family: ig", color = Color(0xFF9B59B6)) {
            TapWord("i g", tts, "ig") 
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TapWord("big", tts) 
                TapWord("dig", tts) 
                TapWord("pig", tts) 
                TapWord("wig", tts) 
            }
        }
    }
}

// ==========================================
// REUSABLE UI COMPONENTS
// ==========================================

@Composable
fun SectionBox(title: String, color: Color, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F2F6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun TapWord(word: String, tts: TextToSpeech, phoneticPronunciation: String = word) {
    Surface(
        modifier = Modifier
            .clickable { tts.speak(phoneticPronunciation, TextToSpeech.QUEUE_FLUSH, null, null) } 
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Text(text = word, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
    }
}
