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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import java.text.SimpleDateFormat
import java.util.Locale

private val HistoryContentPadding = 16.dp
private val HistoryHeaderBottomGap = 16.dp
private val HistorySummaryBottomGap = 12.dp
private val HistoryRowGap = 12.dp

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
