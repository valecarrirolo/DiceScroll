package com.github.valecarrirolo.dicescroll.ui.main

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
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.theme.NeonTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    Row(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(text = diceSummary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          if (roll.modifier != 0) {
            Text(
              text = "(${if (roll.modifier > 0) "+" else ""}${roll.modifier})",
              fontSize = 12.sp,
              color = NeonTeal,
              fontWeight = FontWeight.Bold,
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val rollsDetail = remember(roll.rolls) { roll.rolls.map { it.value }.joinToString(", ") }
        Text(
          text = "Rolls: [$rollsDetail]",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
      }

      Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
        Text(
          text = "= ${roll.total}",
          fontWeight = FontWeight.Black,
          fontSize = 20.sp,
          fontFamily = FontFamily.Monospace,
          color = NeonTeal,
        )
        Text(
          text = timeStr,
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        )
        TextButton(onClick = { onReroll(roll) }) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Reroll", fontSize = 11.sp)
        }
      }
    }
  }
}
