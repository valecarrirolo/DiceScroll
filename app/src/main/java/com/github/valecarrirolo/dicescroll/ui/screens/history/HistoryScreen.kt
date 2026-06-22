@file:OptIn(
  androidx.compose.material3.ExperimentalMaterial3Api::class,
  androidx.compose.foundation.ExperimentalFoundationApi::class,
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.screens.history

import com.github.valecarrirolo.dicescroll.ui.screens.roller.DiceUiState

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.data.model.SingleDieRoll
import com.github.valecarrirolo.dicescroll.theme.NeonTeal
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val HistoryContentPadding = 16.dp
private val HistoryHeaderBottomGap = 16.dp
private val HistorySummaryBottomGap = 12.dp
private val HistoryRowGap = 12.dp

private val HistorySummaryPadding = 14.dp
private val HistorySummaryGap = 10.dp
private val HistorySummaryMetricGap = 2.dp

private val HistoryItemPadding = 14.dp
private val HistoryItemContentGap = 10.dp
private val HistoryItemSetupGap = 3.dp
private val HistoryRerollIconSize = 14.dp

@ThemePreviews
@Composable
fun HistoryContentPreview() {
  ThemedPreview {
    HistoryContent(
      state =
        DiceUiState(
          rollHistory =
            listOf(
              RollResult(
                rolls =
                  listOf(
                    SingleDieRoll(diceType = DiceType.D6, value = 4),
                    SingleDieRoll(diceType = DiceType.D20, value = 15),
                  ),
                modifier = 2,
                timestamp = 1700000000000,
              )
            )
        ),
      onClearHistory = {},
      onReroll = {},
    )
  }
}

@Composable
fun HistoryContent(
  state: DiceUiState,
  onClearHistory: () -> Unit,
  onReroll: (RollResult) -> Unit,
  modifier: Modifier = Modifier,
) {
  val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

  Column(modifier = modifier.fillMaxWidth().padding(HistoryContentPadding)) {
    HistoryHeader(showClearAction = state.rollHistory.isNotEmpty(), onClearHistory = onClearHistory)

    Spacer(modifier = Modifier.height(HistoryHeaderBottomGap))

    if (state.rollHistory.isEmpty()) {
      EmptyHistoryMessage(modifier = Modifier.weight(1f).fillMaxWidth())
    } else {
      HistorySummary(stats = state.rollHistory.toHistoryStats())

      Spacer(modifier = Modifier.height(HistorySummaryBottomGap))

      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HistoryRowGap),
      ) {
        items(state.rollHistory) { roll ->
          HistoryItem(roll = roll, dateFormat = dateFormat, onReroll = onReroll)
        }
      }
    }
  }
}

@Composable
private fun HistoryHeader(showClearAction: Boolean, onClearHistory: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(text = "Roll History", fontWeight = FontWeight.Bold, fontSize = 20.sp)

    if (showClearAction) {
      TextButton(onClick = onClearHistory) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Clear All")
      }
    }
  }
}

@Composable
private fun EmptyHistoryMessage(modifier: Modifier = Modifier) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Text(
      text = "No rolls in this session yet.",
      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
      textAlign = TextAlign.Center,
    )
  }
}

@ThemePreviews
@Composable
fun HistorySummaryPreview() {
  ThemedPreview {
    HistorySummary(
      stats =
        HistoryStats(
          rollCount = 5,
          diceRolled = 12,
          averageTotal = 18.5,
          minTotal = 4,
          maxTotal = 42,
        )
    )
  }
}

@Composable
internal fun HistorySummary(stats: HistoryStats?) {
  if (stats == null) return

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f)
      ),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(HistorySummaryPadding),
      verticalArrangement = Arrangement.spacedBy(HistorySummaryGap),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HistorySummaryGap),
      ) {
        SummaryMetric(label = "Rolls", value = "${stats.rollCount}", modifier = Modifier.weight(1f))
        SummaryMetric(label = "Dice", value = "${stats.diceRolled}", modifier = Modifier.weight(1f))
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HistorySummaryGap),
      ) {
        SummaryMetric(
          label = "Avg",
          value = stats.averageTotal.asCompactStatValue(),
          modifier = Modifier.weight(1f),
        )
        SummaryMetric(label = "Min", value = "${stats.minTotal}", modifier = Modifier.weight(1f))
        SummaryMetric(label = "Max", value = "${stats.maxTotal}", modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun SummaryMetric(label: String, value: String, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(HistorySummaryMetricGap)) {
    Text(
      text = label,
      fontSize = 11.sp,
      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.48f),
    )
    Text(
      text = value,
      fontWeight = FontWeight.Black,
      fontSize = 18.sp,
      fontFamily = FontFamily.Monospace,
      color = NeonTeal,
    )
  }
}

@ThemePreviews
@Composable
fun HistoryItemPreview() {
  ThemedPreview {
    Box(modifier = Modifier.padding(16.dp).background(MaterialTheme.colorScheme.background)) {
      HistoryItem(
        roll =
          RollResult(
            rolls =
              listOf(
                SingleDieRoll(diceType = DiceType.D6, value = 4),
                SingleDieRoll(diceType = DiceType.D20, value = 15),
              ),
            modifier = 2,
          ),
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
      )
    }
  }
}

@Composable
fun HistoryItem(
  roll: RollResult,
  dateFormat: SimpleDateFormat,
  onReroll: (RollResult) -> Unit = {},
) {
  val timeStr = remember(roll.timestamp) { dateFormat.format(Date(roll.timestamp)) }
  val diceSummary =
    remember(roll.rolls) {
      roll.rolls
        .groupBy { it.diceSnapshot.displayName }
        .map { (displayName, list) -> "${list.size}$displayName" }
        .joinToString(", ")
    }

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
      ),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(HistoryItemPadding),
      verticalArrangement = Arrangement.spacedBy(HistoryItemContentGap),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(HistoryItemSetupGap),
        ) {
          Text(text = diceSummary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Text(
            text = "Setup",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f),
          )
        }
        Text(
          text = timeStr,
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f),
          textAlign = TextAlign.End,
        )
      }

      val rollsDetail = remember(roll.rolls) { roll.rolls.map { it.value }.joinToString(", ") }
      Text(
        text = roll.valuesDescription(rollsDetail),
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.68f),
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = "${roll.total}",
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            fontFamily = FontFamily.Monospace,
            color = NeonTeal,
          )
          Text(
            text = "total",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.48f),
            modifier = Modifier.padding(bottom = 5.dp),
          )
        }
        TextButton(onClick = { onReroll(roll) }) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(HistoryRerollIconSize),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reroll", fontSize = 12.sp)
        }
      }
    }
  }
}

private fun Double.asCompactStatValue(): String {
  val rounded = roundToInt()
  return if (this == rounded.toDouble()) {
    "$rounded"
  } else {
    String.format(Locale.US, "%.1f", this)
  }
}

private fun RollResult.valuesDescription(rollsDetail: String): String =
  if (modifier == 0) {
    "Values: $rollsDetail"
  } else {
    "Values: $rollsDetail   Modifier: ${if (modifier > 0) "+" else ""}$modifier"
  }
