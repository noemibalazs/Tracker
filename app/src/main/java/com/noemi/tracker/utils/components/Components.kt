package com.noemi.tracker.utils.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noemi.tracker.R
import com.noemi.tracker.model.ChartData
import com.noemi.tracker.model.PieChartData
import kotlin.math.atan2
import kotlin.math.sqrt

@Composable
fun ProgressIndicator(size: Int, strokeWidth: Int, modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(size.dp),
        strokeWidth = strokeWidth.dp,
        color = Color.Red,
        trackColor = Color.Black
    )
}

@Composable
fun HeaderText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 32.dp),
        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SmallHeadlineText(
    text: String,
    modifier: Modifier = Modifier,
    paddingStart: Int = 30,
    paddingEnd: Int = 0,
    paddingBottom: Int = 6,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = paddingStart.dp, bottom = paddingBottom.dp, end = paddingEnd.dp)
    )
}

@Composable
fun SmallCircularButton(
    isEnabled: Boolean,
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController? = null
) {
    Button(
        onClick = {
            onClick.invoke()
            keyboardController?.hide()
        },
        modifier = modifier
            .height(50.dp)
            .wrapContentWidth()
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        ),
        enabled = isEnabled
    ) {
        Text(
            text = buttonText,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun LargeActionButton(buttonText: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onTertiaryContainer)
            .clickable {
                onClick.invoke()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = buttonText,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = modifier.padding(12.dp)
        )
    }
}

@Composable
fun EmailOutlineTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    hasError: Boolean,
    imeAction: ImeAction,
    interactionSource: MutableInteractionSource,
    keyBoardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = value,
        onValueChange = { onValueChanged.invoke(it) },
        label = {
            Text(
                text = stringResource(id = R.string.label_placeholder_email),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        isError = hasError,
        supportingText = {
            AnimatedVisibility(visible = hasError) {
                Text(
                    text = stringResource(id = R.string.label_invalid_email_address),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Email, contentDescription = stringResource(id = R.string.label_placeholder_email),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = KeyboardType.Email
        ),
        interactionSource = interactionSource,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyBoardController?.hide()
            }
        )
    )
}

@Composable
fun PasswordOutlineTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    hasError: Boolean,
    trailingIcon: @Composable () -> Unit,
    imeAction: ImeAction,
    passwordVisualTransformation: VisualTransformation,
    interactionSource: MutableInteractionSource,
    keyBoardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = { onValueChanged.invoke(it) },
        label = {
            Text(
                text = stringResource(id = R.string.label_placeholder_password),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        isError = hasError,
        supportingText = {
            AnimatedVisibility(visible = hasError) {
                Text(
                    text = stringResource(id = R.string.label_invalid_password),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1,
        trailingIcon = {
            trailingIcon.invoke()
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock, contentDescription = stringResource(id = R.string.label_placeholder_password),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyBoardController?.hide()
            }
        ),
        interactionSource = interactionSource,
        visualTransformation = passwordVisualTransformation
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T> RowScope.ExpenseDropDown(
    elements: List<T>,
    index: Int,
    onIndexChanged: (Int, T) -> Unit,
    expanded: Boolean,
    onExpandedChanged: (Boolean) -> Unit,
    weight: Float,
    modifier: Modifier = Modifier,
    hasPaddingStart: Boolean = false
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChanged.invoke(!expanded) },
        modifier = modifier
            .weight(weight)
            .padding(end = if (hasPaddingStart) 0.dp else 6.dp, start = if (hasPaddingStart) 6.dp else 0.dp)
    ) {

        OutlinedTextField(
            value = elements[index].toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChanged.invoke(false) },
            shape = MaterialTheme.shapes.medium
        ) {

            elements.forEachIndexed { position, element ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = element.toString(),
                            style = MaterialTheme.typography.bodyLarge
                                .copy(
                                    fontWeight = if (position == index) FontWeight.Bold else FontWeight.SemiBold,
                                    fontStyle = if (position == index) FontStyle.Italic else FontStyle.Normal
                                ),
                            color = if (position == index) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                        )
                    },
                    onClick = {
                        onIndexChanged.invoke(position, element)
                        onExpandedChanged.invoke(false)
                    }
                )
            }
        }
    }
}

@Composable
fun RowScope.ExpenseOutlineTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    placeHolderTest: String,
    keyboardType: KeyboardType,
    keyBoardController: SoftwareKeyboardController?,
    weight: Float,
    modifier: Modifier = Modifier
) {

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChanged.invoke(it)
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        placeholder = {
            Text(text = placeHolderTest)
        },
        keyboardActions = KeyboardActions(
            onDone = {
                keyBoardController?.hide()
            }
        ),
        modifier = modifier
            .weight(weight)
            .padding(start = 6.dp)
    )
}

@Composable
fun PieChartWithAnimation(
    chartsData: List<ChartData>,
    currency: String,
    title: String,
    percentage: Float,
    modifier: Modifier = Modifier
) {

    val pieChart = PieChartData(chartsData)

    var selectedIndex by remember { mutableIntStateOf(-1) }
    val onChanged = { index: Int ->
        selectedIndex = index
    }

    val animatable = remember { Animatable(-90f) }
    var radius = 0f

    LaunchedEffect(key1 = animatable) {
        animatable.animateTo(
            targetValue = 270f,
            animationSpec = tween(
                delayMillis = 300,
                durationMillis = 1000
            )
        )
    }

    val currentSweepAngle = animatable.value

    Box(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {

        Column(
            modifier = modifier.fillMaxSize()
        ) {

            Text(
                modifier = modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                text = title
            )

            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                text = if (selectedIndex != -1) stringResource(
                    id = R.string.label_chart_slice,
                    chartsData[selectedIndex].type,
                    "${chartsData[selectedIndex].value} - $currency"
                ) else ""
            )

            Box(
                modifier = modifier
                    .padding(20.dp)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->

                                val xPos = size.center.x - offset.x
                                val yPos = size.center.y - offset.y
                                val position = sqrt(xPos * xPos + yPos * yPos)
                                val isTouched = position in 0f..radius

                                if (isTouched) {
                                    var touchAngle = (270f + atan2(yPos, xPos) * 180 / Math.PI) % 360f

                                    if (touchAngle < 0) touchAngle += 360f

                                    chartsData.forEachIndexed { index, chart ->
                                        val isTouchInArcSegment = touchAngle in chart.range
                                        if (isTouchInArcSegment) onChanged.invoke(index)
                                    }
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {

                Canvas(
                    modifier = modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .padding(24.dp)
                        .aspectRatio(1f),
                    onDraw = {
                        radius = size.width
                        var startAngle = -90f

                        chartsData.forEachIndexed { index, chartData ->
                            val sweepAngle = pieChart.sweepAngle(index, percentage)
                            val gap = pieChart.gapAngle(percentage)

                            if (startAngle <= currentSweepAngle) {
                                drawArc(
                                    startAngle = startAngle,
                                    sweepAngle = sweepAngle.coerceAtMost(currentSweepAngle - startAngle),
                                    useCenter = false,
                                    size = Size(radius, radius),
                                    style = Stroke(radius),
                                    color = chartData.color
                                )
                            }

                            startAngle += sweepAngle + gap
                        }
                    }
                )
            }
        }
    }
}