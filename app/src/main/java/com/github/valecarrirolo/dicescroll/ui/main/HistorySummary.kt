package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.theme.NeonTeal
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview
import java.util.Locale
import kotlin.math.roundToInt

private val HistorySummaryPadding = 14.dp
private val HistorySummaryGap = 10.dp
private val HistorySummaryMetricGap = 2.dp

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

private fun Double.asCompactStatValue(): String {
  val rounded = roundToInt()
  return if (this == rounded.toDouble()) {
    "$rounded"
  } else {
    String.format(Locale.US, "%.1f", this)
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
