package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.WeatherCondition
import com.example.domain.model.WorldClockItem

@Composable
fun OneUiDualClockSection(
    primaryClock: WorldClockItem,
    secondaryClock: WorldClockItem,
    modifier: Modifier = Modifier,
    onClockClick: (WorldClockItem) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "WORLD CLOCK & TIMEZONES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 1.sp
            )
            Text(
                text = "One UI Dual Clock",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OneUiClockCard(
                item = primaryClock,
                modifier = Modifier.weight(1f),
                onClick = { onClockClick(primaryClock) }
            )
            OneUiClockCard(
                item = secondaryClock,
                modifier = Modifier.weight(1f),
                onClick = { onClockClick(secondaryClock) }
            )
        }
    }
}

@Composable
fun OneUiClockCard(
    item: WorldClockItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Dynamic Day/Night Theme styling matching One UI Dual Clock widget
    val isDay = item.isDay
    val cardBackground = if (isDay) Color(0xFFFFFFFF) else Color(0xFF16181D)
    val cityTextColor = if (isDay) Color(0xFFD97706) else Color(0xFF9E77ED)
    val dateTextColor = if (isDay) Color(0xFF78350F) else Color(0xFFE2E8F0)
    val timeTextColor = if (isDay) Color(0xFFD97706) else Color(0xFFB4C6FF)
    val subTextColor = if (isDay) Color(0xFF92400E) else Color(0xFF94A3B8)

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(26.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp),
        color = cardBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Column {
                // Top Row: City Name & Weather / Sun/Moon Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.cityName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = cityTextColor,
                            fontSize = 17.sp
                        )
                        Text(
                            text = item.formattedDate.ifEmpty { "Today" },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = dateTextColor,
                            fontSize = 13.sp
                        )
                    }

                    // Weather condition icon or Day/Night icon
                    WeatherConditionIcon(
                        condition = item.condition ?: if (isDay) WeatherCondition.CLEAR_DAY else WeatherCondition.CLEAR_NIGHT,
                        isDay = isDay,
                        size = 24.dp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Row: Large Time digits with AM/PM pill + Time Diff
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = item.formattedTime.ifEmpty { "12:00" },
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Normal,
                            color = timeTextColor,
                            fontSize = 32.sp,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.formattedAmPm.ifEmpty { "PM" },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = timeTextColor,
                            modifier = Modifier.padding(bottom = 4.dp),
                            fontSize = 13.sp
                        )
                    }

                    // Time difference (e.g. "+8 hrs" or "Local")
                    val diffLabel = if (item.timeDiffHours == 0.0) {
                        "Local"
                    } else {
                        val sign = if (item.timeDiffHours > 0) "+" else ""
                        val hrs = if (item.timeDiffHours % 1.0 == 0.0) "${item.timeDiffHours.toInt()}" else "%.1f".format(item.timeDiffHours)
                        "$sign$hrs hrs"
                    }

                    Text(
                        text = diffLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = subTextColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}
