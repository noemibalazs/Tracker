package com.noemi.tracker.screens.charts

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi.tracker.R
import com.noemi.tracker.model.Expense
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesMonths
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesYears
import com.noemi.tracker.utils.components.ExpenseDropDown
import com.noemi.tracker.utils.components.PieChartWithAnimation
import com.noemi.tracker.utils.components.ProgressIndicator
import com.noemi.tracker.utils.components.SmallCircularButton
import com.noemi.tracker.utils.components.SmallHeadlineText
import com.noemi.tracker.viewmodel.ChartsViewModel
import kotlinx.coroutines.delay

@Composable
fun ChartsScreen(modifier: Modifier = Modifier) {

    val viewModel = hiltViewModel<ChartsViewModel>()

    val currentExpenses by viewModel.currentPeriodExpenses.collectAsStateWithLifecycle()
    val currentChartsData by viewModel.currentPeriodDataCharts.collectAsStateWithLifecycle()
    val isLoadingCurrentPeriodDetails by viewModel.loadingCurrentPeriodExpenses.collectAsStateWithLifecycle()

    val selectedPeriodExpenses by viewModel.selectedPeriodExpenses.collectAsStateWithLifecycle()
    val selectedChartsData by viewModel.selectedPeriodDataCharts.collectAsStateWithLifecycle()
    val isLoadingSelectedPeriodDetails by viewModel.loadingSelectedPeriodExpenses.collectAsStateWithLifecycle()

    val error by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isContinueEnabled by viewModel.isContinueEnabled.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lazyState = rememberLazyListState()

    if (error.isNotEmpty()) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        LaunchedEffect(key1 = error.isNotEmpty()) {
            delay(1500)
            viewModel.onErrorChanged()
        }
    }

    if (selectedPeriodExpenses.keys.size > 1) {
        LaunchedEffect(selectedPeriodExpenses.keys.size > 1) {
            delay(300)
            lazyState.animateScrollToItem(lazyState.layoutInfo.totalItemsCount)
        }
    }

    Column(
        modifier = modifier
            .padding(bottom = 60.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        when (isLoadingCurrentPeriodDetails) {
            true -> ProgressIndicator(size = 90, strokeWidth = 9)
            else ->
                LazyColumn(state = lazyState) {
                    item {
                        SmallHeadlineText(
                            text = stringResource(id = R.string.label_current_month_expenses),
                            paddingStart = 20,
                            paddingEnd = 20
                        )
                    }

                    currentExpenses.keys.forEach { expense ->
                        val expenses = currentExpenses[expense] ?: emptyList()
                        val amount = expenses.sumOf { it.amount }

                        item {
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SmallHeadlineText(
                                    text = expense,
                                    paddingStart = 20,
                                    paddingEnd = 20,
                                    paddingBottom = 0
                                )
                                SmallHeadlineText(
                                    text = amount.toString(),
                                    paddingStart = 20,
                                    paddingEnd = 20,
                                    paddingBottom = 0
                                )
                            }
                        }

                        items(
                            items = expenses,
                            key = { it.id }
                        ) { cost ->
                            ExpenseItemRow(expense = cost)
                        }
                    }

                    if (currentChartsData.size > 1)
                        item {

                            PieChartWithAnimation(
                                chartsData = currentChartsData,
                                percentage = 0.04f,
                                title = stringResource(id = R.string.label_chart, viewModel.currentPeriod.second, viewModel.currentPeriod.first),
                                currency = currency
                            )
                        }

                    item {
                        SmallHeadlineText(
                            text = stringResource(id = R.string.label_selected_month_expenses),
                            paddingStart = 20,
                            paddingEnd = 20,
                            paddingBottom = 0
                        )
                    }

                    item {
                        ExpensesPeriod(
                            monthExpanded = viewModel.monthExpanded,
                            onMonthExpandedChanged = viewModel::onMonthExpandedChanged,
                            yearExpanded = viewModel.yearExpanded,
                            onYearExpandedChanged = viewModel::onYearExpandedChanged,
                            monthIndex = viewModel.monthIndex,
                            onMonthIndexChanged = viewModel::onMonthIndexChanged,
                            yearIndex = viewModel.yearIndex,
                            onYearIndexChanged = viewModel::onYearsIndexChanged,
                            isEnabled = isContinueEnabled,
                            onContinueClicked = viewModel::getSelectedPeriodExpenses,
                            isLoading = isLoadingSelectedPeriodDetails
                        )
                    }

                    item { Spacer(modifier = modifier.padding(12.dp)) }

                    selectedPeriodExpenses.keys.forEach { expense ->
                        val expenses = selectedPeriodExpenses[expense] ?: emptyList()
                        val amount = expenses.sumOf { it.amount }

                        item {
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SmallHeadlineText(
                                    text = expense,
                                    paddingStart = 20,
                                    paddingEnd = 20,
                                    paddingBottom = 0
                                )
                                SmallHeadlineText(
                                    text = amount.toString(),
                                    paddingStart = 20,
                                    paddingEnd = 20,
                                    paddingBottom = 0
                                )
                            }
                        }

                        items(
                            items = expenses,
                            key = { it.id }
                        ) { cost ->
                            ExpenseItemRow(expense = cost)
                        }
                    }

                    if (selectedChartsData.size > 1)
                        item {
                            val year = viewModel.selectedPeriod.first
                            val month = viewModel.selectedPeriod.second

                            PieChartWithAnimation(
                                chartsData = selectedChartsData,
                                title = stringResource(id = R.string.label_chart, month, year),
                                percentage = 0.04f,
                                currency = currency
                            )
                        }

                    item { Spacer(modifier = modifier.padding(30.dp)) }
                }
        }
    }
}

@Composable
fun ExpenseItemRow(
    expense: Expense, modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 6.dp)
            .background(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.label_doc_number, expense.documentNumber),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = expense.amount.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun ExpensesPeriod(
    monthExpanded: Boolean,
    onMonthExpandedChanged: (Boolean) -> Unit,
    yearExpanded: Boolean,
    onYearExpandedChanged: (Boolean) -> Unit,
    monthIndex: Int,
    onMonthIndexChanged: (Int, String) -> Unit,
    yearIndex: Int,
    onYearIndexChanged: (Int, Int) -> Unit,
    isEnabled: Boolean,
    isLoading: Boolean,
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SmallHeadlineText(
                    text = stringResource(id = R.string.label_reference_period_month),
                    paddingStart = 0,
                    paddingEnd = 0
                )
                SmallHeadlineText(
                    text = stringResource(id = R.string.label_reference_period_year),
                    paddingStart = 0,
                    paddingEnd = 0
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                ExpenseDropDown(
                    elements = expensesMonths(),
                    index = monthIndex,
                    onIndexChanged = onMonthIndexChanged,
                    expanded = monthExpanded,
                    onExpandedChanged = onMonthExpandedChanged,
                    weight = 0.5f
                )

                ExpenseDropDown(
                    elements = expensesYears(),
                    index = yearIndex,
                    onIndexChanged = onYearIndexChanged,
                    expanded = yearExpanded,
                    onExpandedChanged = onYearExpandedChanged,
                    weight = 0.5f,
                    hasPaddingStart = true
                )
            }

            Spacer(modifier = modifier.padding(16.dp))

            Box(
                modifier = modifier.wrapContentWidth(),
                contentAlignment = Alignment.Center
            ) {
                SmallCircularButton(
                    isEnabled = isEnabled,
                    buttonText = stringResource(id = R.string.label_continue),
                    onClick = { onContinueClicked.invoke() }
                )

                if (isLoading) ProgressIndicator(size = 46, strokeWidth = 3)
            }
        }
    }
}