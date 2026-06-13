package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.github.valecarrirolo.dicescroll.data.DefaultDataRepository
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository()) }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    var showHistorySheet by remember { mutableStateOf(false) }

    // Trigger haptic feedback when rolling state changes
    LaunchedEffect(state.isRolling) {
        if (state.isRolling) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } else if (state.currentRollResult != null) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DiceScroll",
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.clearTray() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Clear Tray",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { showHistorySheet = true }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "View History",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TRAY AREA: Displays selected dice and their roll values
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.5f),
                                NeonTeal.copy(alpha = 0.5f)
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (state.selectedDice.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tray is empty",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Tap dice below to populate the tray.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    TrayContent(state = state)
                }
            }

            // 2. MODIFIER & QUICK STATS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Modifier: ${if (state.modifier >= 0) "+" else ""}${state.modifier}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.setModifier(state.modifier - 1) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear, // Close-like minus representation
                            contentDescription = "Minus 1",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.setModifier(state.modifier + 1) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Plus 1",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. MAIN ACTION ROLL BUTTON
            val buttonScale = remember { Animatable(1f) }
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    coroutineScope.launch {
                        buttonScale.animateTo(0.92f, animationSpec = tween(100))
                        buttonScale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                    viewModel.rollTray()
                },
                enabled = state.selectedDice.isNotEmpty() && !state.isRolling,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .scale(buttonScale.value)
                    .clip(RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (state.selectedDice.isNotEmpty() && !state.isRolling) {
                                Brush.linearGradient(colors = listOf(NeonPurple, NeonTeal))
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            state.isRolling -> "ROLLING..."
                            state.totalDiceCount > 0 -> "ROLL ${state.totalDiceCount} DICE"
                            else -> "SELECT DICE"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.selectedDice.isNotEmpty() && !state.isRolling) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. DICE SELECTOR DOCK
            Text(
                text = "Dice Pool",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(DiceType.values()) { type ->
                    val count = state.selectedDice[type] ?: 0
                    DiceSelectionCard(
                        type = type,
                        count = count,
                        onAdd = { viewModel.addDie(type) },
                        onRemove = { viewModel.removeDie(type) }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for History
    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            HistorySheetContent(
                state = state,
                onClearHistory = { viewModel.clearHistory() },
                onDismiss = { showHistorySheet = false }
            )
        }
    }
}

@Composable
fun TrayContent(state: DiceUiState) {
    val itemsToDisplay = remember(state.isRolling, state.animatedValues, state.currentRollResult) {
        if (state.isRolling) {
            state.animatedValues.map { Pair(null, it) }
        } else if (state.currentRollResult != null) {
            state.currentRollResult.rolls.map { Pair(it.diceType, it.value) }
        } else {
            state.selectedDice.flatMap { (type, count) -> List(count) { Pair(type, 0) } }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top total display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = Pair(state.isRolling, state.currentRollResult),
                transitionSpec = {
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
                            fadeOut(animationSpec = tween(90))
                },
                label = "TotalDisplay"
            ) { (rolling, result) ->
                if (rolling) {
                    Text(
                        text = "Rolling...",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NeonTeal
                    )
                } else if (result != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "${result.total}",
                            fontWeight = FontWeight.Black,
                            fontSize = 36.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NeonTeal
                        )
                    }
                } else {
                    Text(
                        text = "Ready to Roll",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Dice grid/flow
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Chunk the list into rows of up to 4 dice
                val chunks = itemsToDisplay.chunked(4)
                items(chunks) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowItems.forEach { (type, value) ->
                            DieItem(
                                type = type,
                                value = value,
                                isRolling = state.isRolling
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DieItem(
    type: DiceType?,
    value: Int,
    isRolling: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ShakeAnim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShakeRotate"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShakeScale"
    )

    val modifier = if (isRolling) {
        Modifier
            .graphicsLayer {
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
            }
    } else {
        Modifier
    }

    val color = type?.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } ?: NeonPurple

    Box(
        modifier = modifier
            .padding(8.dp)
            .size(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.15f))
            .border(2.dp, color, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (type != null) {
                Text(
                    text = type.displayName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Text(
                text = if (value > 0) "$value" else "?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = if (value > 0) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun DiceSelectionCard(
    type: DiceType,
    count: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    val color = Color(android.graphics.Color.parseColor(type.colorHex))

    Card(
        modifier = Modifier
            .width(86.dp)
            .border(
                width = 1.dp,
                color = if (count > 0) color else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (count > 0) {
                color.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Die Display Icon/Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .clickable { onAdd() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type.displayName,
                    fontWeight = FontWeight.ExtraBold,
                    color = color,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Counter Label
            Text(
                text = if (count > 0) "$count selected" else "Tap to add",
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Normal,
                color = if (count > 0) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.height(14.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Plus/Minus Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onRemove,
                    enabled = count > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .background(
                            if (count > 0) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        modifier = Modifier.size(12.dp)
                    )
                }

                IconButton(
                    onClick = onAdd,
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(6.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HistorySheetContent(
    state: DiceUiState,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Roll History",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            if (state.rollHistory.isNotEmpty()) {
                TextButton(onClick = onClearHistory) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.rollHistory.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No rolls in this session yet.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.rollHistory) { roll ->
                    HistoryItem(roll = roll, dateFormat = dateFormat)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Close")
        }
    }
}

@Composable
fun HistoryItem(roll: RollResult, dateFormat: SimpleDateFormat) {
    val timeStr = remember(roll.timestamp) { dateFormat.format(Date(roll.timestamp)) }
    // Calculate a summary string like "2D6, 1D20 (+2)"
    val diceSummary = remember(roll.rolls) {
        roll.rolls.groupBy { it.diceType }
            .map { (type, list) -> "${list.size}${type.displayName}" }
            .joinToString(", ")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = diceSummary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (roll.modifier != 0) {
                        Text(
                            text = "(${if (roll.modifier > 0) "+" else ""}${roll.modifier})",
                            fontSize = 12.sp,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Detail rolls list: [4, 6] [12]
                val rollsDetail = remember(roll.rolls) {
                    roll.rolls.map { it.value }.joinToString(", ")
                }
                Text(
                    text = "Rolls: [$rollsDetail]",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "= ${roll.total}",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NeonTeal
                )
                Text(
                    text = timeStr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        }
    }
}
