package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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

private val HistoryItemPadding = 14.dp
private val HistoryItemContentGap = 10.dp
private val HistoryItemSetupGap = 3.dp
private val HistoryRerollIconSize = 14.dp

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

private fun RollResult.valuesDescription(rollsDetail: String): String =
  if (modifier == 0) {
    "Values: $rollsDetail"
  } else {
    "Values: $rollsDetail   Modifier: ${if (modifier > 0) "+" else ""}$modifier"
  }
