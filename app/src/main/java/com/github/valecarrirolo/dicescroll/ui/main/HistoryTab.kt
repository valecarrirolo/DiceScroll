package com.github.valecarrirolo.dicescroll.ui.main

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.data.model.SingleDieRoll
import com.github.valecarrirolo.dicescroll.theme.DiceScrollTheme
import com.github.valecarrirolo.dicescroll.theme.NeonTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HistoryTabContent(
  state: DiceUiState,
  onClearHistory: () -> Unit,
  onReroll: (RollResult) -> Unit,
  modifier: Modifier = Modifier,
) {
  val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

  Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = "Roll History", fontWeight = FontWeight.Bold, fontSize = 20.sp)

      if (state.rollHistory.isNotEmpty()) {
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

    Spacer(modifier = Modifier.height(16.dp))

    if (state.rollHistory.isEmpty()) {
      Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
          text = "No rolls in this session yet.",
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
          textAlign = TextAlign.Center,
        )
      }
    } else {
      HistoryStatsSummary(stats = state.rollHistory.toHistoryStats())

      Spacer(modifier = Modifier.height(12.dp))

      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        items(state.rollHistory) { roll ->
          HistoryItem(roll = roll, dateFormat = dateFormat, onReroll = onReroll)
        }
      }
    }
  }
}

@Composable
private fun HistoryStatsSummary(stats: HistoryStats?) {
  if (stats == null) return

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f)
      ),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        HistoryStatCell(
          label = "Rolls",
          value = "${stats.rollCount}",
          modifier = Modifier.weight(1f),
        )
        HistoryStatCell(
          label = "Dice",
          value = "${stats.diceRolled}",
          modifier = Modifier.weight(1f),
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        HistoryStatCell(
          label = "Avg",
          value = stats.averageTotal.asStatValue(),
          modifier = Modifier.weight(1f),
        )
        HistoryStatCell(label = "Min", value = "${stats.minTotal}", modifier = Modifier.weight(1f))
        HistoryStatCell(label = "Max", value = "${stats.maxTotal}", modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun HistoryStatCell(label: String, value: String, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
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

private fun Double.asStatValue(): String {
  val rounded = roundToInt()
  return if (this == rounded.toDouble()) {
    "$rounded"
  } else {
    String.format(Locale.US, "%.1f", this)
  }
}

@Preview(showBackground = true, name = "History Item")
@Composable
fun HistoryItemPreview() {
  DiceScrollTheme(darkTheme = true) {
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
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
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
        text =
          if (roll.modifier == 0) {
            "Values: $rollsDetail"
          } else {
            "Values: $rollsDetail   Modifier: ${if (roll.modifier > 0) "+" else ""}${roll.modifier}"
          },
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
            modifier = Modifier.size(14.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reroll", fontSize = 12.sp)
        }
      }
    }
  }
}
