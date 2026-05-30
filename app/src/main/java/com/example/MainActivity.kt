package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryGlow
import kotlinx.coroutines.delay

data class Quote(val text: String, val src: String)
val QUOTES = listOf(
    Quote("كل مرة تقاوم فيها الإلهاء، تبني خلية عصبية جديدة.", "علم الأعصاب"),
    Quote("الدوبامين لا يكافئك على الإنجاز، بل على التوقع — تحكّم في التوقع.", "علم النفس")
)

data class Challenge(val icon: String, val name: String, val desc: String, val dur: String, val xp: Int, val pro: Boolean)
val CHALLENGES = listOf(
    Challenge("🔇", "تحدي الصمت", "ساعة كاملة بدون أي صوت رقمي", "٦٠ دق", 40, false),
    Challenge("🧠", "تفكير عميق", "فكّر في موضوع واحد فقط بعمق", "٢٠ دق", 35, false),
    Challenge("⏳", "تأخير الإشباع", "أجّل أي مكافأة فورية لمدة ساعة", "٦٠ دق", 50, false),
    Challenge("🎮", "مركز الألعاب", "ألعاب ذاكرة وتركيز مخصصة", "١٥ دق", 30, true),
    Challenge("✏️", "كلمات متقاطعة", "تحدي لغوي يحفّز الذاكرة", "١٠ دق", 25, true)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MyApplicationTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        BrainCleanProApp()
                    }
                }
            }
        }
    }
}

@Composable
fun CardTheme(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(MaterialTheme.colorScheme.surface, Color(0xFF10141A))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(20.dp),
        content = content
    )
}

@Composable
fun TopBar(title: String, onBack: (() -> Unit)? = null, innerPadding: PaddingValues = PaddingValues(0.dp)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp + innerPadding.calculateTopPadding(), bottom = 16.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Text("🇸🇦", fontSize = 16.sp)
        }
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        if (onBack != null) {
            Spacer(modifier = Modifier.width(48.dp))
        } else {
            Text("🌐", fontSize = 16.sp)
        }
    }
}

@Composable
fun CircleProgress(value: Float, max: Float = 100f, size: Int = 160, stroke: Int = 8, color: Color = MaterialTheme.colorScheme.primary, content: @Composable () -> Unit) {
    val animatedProgress by animateFloatAsState(
        targetValue = (value / max.coerceAtLeast(0.01f)).coerceIn(0f, 1f),
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Box(
        modifier = Modifier.size(size.dp).padding((stroke / 2).dp),
        contentAlignment = Alignment.Center
    ) {
        val surfaceHighlight = MaterialTheme.colorScheme.surfaceVariant
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = surfaceHighlight, style = Stroke(width = stroke.dp.toPx()))
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = stroke.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        content()
    }
}

@Composable
fun PomodoroScreen(onBack: () -> Unit) {
    var seconds by remember { mutableStateOf(25 * 60) }
    var running by remember { mutableStateOf(false) }
    var sessions by remember { mutableStateOf(0) }
    val total = 25 * 60

    LaunchedEffect(running) {
        while (running && seconds > 0) {
            delay(1000)
            seconds--
            if (seconds == 0) {
                running = false
                sessions++
            }
        }
    }

    val mins = (seconds / 60).toString().padStart(2, '0')
    val secs = (seconds % 60).toString().padStart(2, '0')
    val pct = ((total - seconds).toFloat() / total.toFloat()) * 100f

    Column(modifier = Modifier.fillMaxSize().padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) {
        TopBar(title = "وقت التركيز 🎯", onBack = onBack, innerPadding = WindowInsets.statusBars.asPaddingValues())
        
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircleProgress(value = pct, size = 260, stroke = 10, color = MaterialTheme.colorScheme.primary) {
                Text(
                    text = "$mins:$secs",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { seconds = 25 * 60; running = false }) {
                    Text("تخطي", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { running = !running },
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (running) "⏸" else "▶", fontSize = 32.sp, color = Color.Black)
                }
                TextButton(onClick = { seconds = 0 }) {
                    Text("إعادة", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
        CardTheme(modifier = Modifier.padding(24.dp)) {
            Text(
                "جلسات اليوم: $sessions",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DeepThinkScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) {
        TopBar(title = "تحدي التفكير المركز", onBack = onBack, innerPadding = WindowInsets.statusBars.asPaddingValues())
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("اختر موضوعاً وفكر فيه بعمق", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 20.dp))
        }
    }
}

@Composable
fun BottomNav(tab: String, onTabSelect: (String) -> Unit) {
    val items = listOf(
        Triple("account", "👤", "حسابي"),
        Triple("discover", "🧭", "اكتشف"),
        Triple("progress", "📊", "تقدمي"),
        Triple("missions", "✅", "مهامي"),
        Triple("today", "🏠", "اليوم")
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f))
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(vertical = 12.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { (id, icon, label) ->
            key(id) {
                NavItem(
                    id = id,
                    icon = icon,
                    label = label,
                    active = tab == id,
                    onTabSelect = onTabSelect,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NavItem(id: String, icon: String, label: String, active: Boolean, onTabSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onTabSelect(id) }
    ) {
        Text(
            text = icon,
            fontSize = 24.sp,
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ChallengeRow(c: Challenge, done: Boolean, onTap: () -> Unit) {
    CardTheme(modifier = Modifier.clickable { onTap() }.alpha(if (c.pro) 0.7f else 1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(c.icon, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(c.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
                    if (c.pro) {
                        Text("Pro", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.padding(start = 8.dp).background(Color(0xFFF59E0B), RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Text(c.desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
            Text("›", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.alpha(0.5f))
        }
    }
}

@Composable
fun BrainCleanProApp() {
    var tab by remember { mutableStateOf("today") }
    var subScreen by remember { mutableStateOf<String?>(null) }
    var freezeDialog by remember { mutableStateOf(false) }
    var streakFrozen by remember { mutableStateOf(false) }
    var liveTimer by remember { mutableStateOf(5) }
    val elapsedDays = 54
    val elapsedHours = 14
    val elapsedMins = 14
    val day = 55
    val journeyPct = ((day / 90f) * 100).toInt()
    val bcScore = 43

    LaunchedEffect(Unit) {
        while(true) {
            delay(1000)
            liveTimer++
        }
    }

    val totalSecs = elapsedDays * 86400 + elapsedHours * 3600 + elapsedMins * 60 + liveTimer
    val d = totalSecs / 86400
    val h = (totalSecs % 86400) / 3600
    val m = (totalSecs % 3600) / 60
    val s = totalSecs % 60

    if (subScreen == "pomodoro") {
        PomodoroScreen(onBack = { subScreen = null })
        return
    }
    if (subScreen == "deepthink") {
        DeepThinkScreen(onBack = { subScreen = null })
        return
    }

    Scaffold(
        bottomBar = { BottomNav(tab = tab, onTabSelect = { tab = it }) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (tab) {
                "today" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        TopBar(title = "الرئيسية", innerPadding = innerPadding)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .background(brush = Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)), shape = RoundedCornerShape(20.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                                    .padding(24.dp)
                            ) {
                                Column {
                                    Text(
                                        "\"${QUOTES[0].text}\"",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        lineHeight = 24.sp,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    Text(QUOTES[0].src, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircleProgress(value = bcScore.toFloat(), size = 200, color = MaterialTheme.colorScheme.primary) {
                                    Text(bcScore.toString(), fontSize = 64.sp, fontWeight = FontWeight.Black)
                                }
                                Text("نقاط صفاء الدماغ", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp))
                                Text("$bcScore% مكتمل", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
                            }

                            CardTheme {
                                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("رحلة التعافي", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                    Text(
                                        "اليوم $day من 90",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text("التعافي الحقيقي يبدأ بعد 21 يوماً ويكتمل في 90", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
                                
                                Box(modifier = Modifier.fillMaxWidth().height(10.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                                    Box(modifier = Modifier.fillMaxWidth((journeyPct / 100f).coerceIn(0.01f, 1f)).fillMaxHeight().background(
                                        Brush.horizontalGradient(listOf(Color(0xFF047857), MaterialTheme.colorScheme.primary)), CircleShape
                                    ))
                                }
                                Text("$journeyPct% مكتمل", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                            }

                            CardTheme {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                    listOf("أيام" to d, "ساعات" to h, "دقائق" to m, "ثواني" to s).forEach { (label, value) ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(value.toString().padStart(2, '0'), fontSize = 28.sp, fontWeight = FontWeight.Black)
                                            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = { freezeDialog = true },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6).copy(alpha = 0.05f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f)),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("❄️", fontSize = 18.sp, modifier = Modifier.padding(end = 10.dp))
                                Text("تجميد الإنجاز ${if(streakFrozen) "(مفعّل)" else "(1 متاح)"}", fontWeight = FontWeight.Bold, color = Color(0xFF60A5FA))
                            }

                            Button(
                                onClick = { subScreen = "pomodoro" },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues()
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, Color(0xFF059669))))
                                        .padding(18.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🚀 ابدأ أول جلسة تركيز الآن", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
                "discover" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        TopBar(title = "اكتشف", innerPadding = innerPadding)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp)
                        ) {
                            Text("تحديات التركيز", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 12.dp))
                            CHALLENGES.take(3).forEach { c ->
                                ChallengeRow(c, done = false, onTap = { if (c.name == "تفكير عميق") subScreen = "deepthink" })
                            }
                            
                            Text("ألعاب الذاكرة", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 12.dp))
                            CHALLENGES.drop(3).forEach { c ->
                                ChallengeRow(c, done = false, onTap = {})
                            }
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        TopBar(title = tab, innerPadding = innerPadding)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp)
                        ) {
                            CardTheme { Text("ليس متاحاً الآن", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
            }

            if (freezeDialog) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
                    contentAlignment = Alignment.Center
                ) {
                    CardTheme(modifier = Modifier.padding(24.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("❄️", fontSize = 32.sp, modifier = Modifier.padding(bottom = 12.dp))
                            Text("تجميد الإنجاز", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 8.dp))
                            Text("هل تريد تجميد الـ Streak؟ متاح مرة أسبوعياً للحفاظ على تقدمك.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 24.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { freezeDialog = false },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Text("إلغاء", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { streakFrozen = true; freezeDialog = false },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("تأكيد التجميد", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

