package com.noemi.tracker.screens.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi.tracker.R
import com.noemi.tracker.providers.ExpenseDetailsProvider.getCurrencies
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesTypes
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesMonths
import com.noemi.tracker.providers.ExpenseDetailsProvider.expensesYears
import com.noemi.tracker.utils.components.ExpenseDropDown
import com.noemi.tracker.utils.components.ExpenseOutlineTextField
import com.noemi.tracker.utils.components.ProgressIndicator
import com.noemi.tracker.utils.components.SmallCircularButton
import com.noemi.tracker.utils.components.SmallHeadlineText
import com.noemi.tracker.viewmodel.ExpensesViewModel

@Composable
fun ExpensesScreen(modifier: Modifier = Modifier) {

    val viewModel = hiltViewModel<ExpensesViewModel>()
    val isEnabled by viewModel.isSaveEnabled.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        LazyColumn {

            item {
                ExpenseTypeAndNumber(
                    expanded = viewModel.typeExpanded,
                    onExpandedChanged = viewModel::onTypeExpandedChanged,
                    typeIndex = viewModel.typeIndex,
                    onIndexChanged = viewModel::onTypePositionChanged,
                    document = viewModel.documentNumber,
                    onDocumentChanged = viewModel::onDocumentNumberChanged
                )
            }

            item { Spacer(modifier = modifier.padding(12.dp)) }

            item {
                ExpensePeriod(
                    monthExpanded = viewModel.monthExpanded,
                    onMonthExpandedChanged = viewModel::onMonthExpandedChanged,
                    yearExpanded = viewModel.yearExpanded,
                    onYearExpandedChanged = viewModel::onYearExpandedChanged,
                    monthIndex = viewModel.monthIndex,
                    onMonthIndexChanged = viewModel::onMonthIndexChanged,
                    yearIndex = viewModel.yearIndex,
                    onYearIndexChanged = viewModel::onYearsIndexChanged
                )
            }

            item { Spacer(modifier = modifier.padding(12.dp)) }

            item {
                ExpenseCurrencyWithAmount(
                    expanded = viewModel.currencyExpanded,
                    onExpandedChanged = viewModel::onCurrencyExpandedChanged,
                    index = viewModel.currencyIndex,
                    onIndexChanged = viewModel::onCurrencyIndexChanged,
                    amount = viewModel.expensesAmount,
                    onAmountChanged = viewModel::onAmountChanged
                )
            }

            item { Spacer(modifier = modifier.padding(12.dp)) }
        }

        SaveExpense(
            isLoading = viewModel.isLoading,
            isEnabled = isEnabled,
            onSave = { viewModel.saveExpenses() }
        )

        Spacer(modifier = modifier.padding(30.dp))
    }
}

@Composable
fun ExpenseTypeAndNumber(
    expanded: Boolean,
    onExpandedChanged: (Boolean) -> Unit,
    typeIndex: Int,
    onIndexChanged: (Int, String) -> Unit,
    document: String,
    onDocumentChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val types = expensesTypes()

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
                    .padding(end = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                SmallHeadlineText(text = stringResource(id = R.string.label_expense_type))

                SmallHeadlineText(text = stringResource(id = R.string.label_document_number))
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                ExpenseDropDown(
                    elements = expensesTypes(),
                    index = typeIndex,
                    onIndexChanged = onIndexChanged,
                    expanded = expanded,
                    onExpandedChanged = onExpandedChanged,
                    weight = 0.7f
                )

                ExpenseOutlineTextField(
                    value = document,
                    onValueChanged = onDocumentChanged,
                    placeHolderTest = stringResource(id = R.string.label_placeholder_document),
                    keyboardType = KeyboardType.Number,
                    keyBoardController = keyboardController,
                    weight = 0.3f
                )
            }
        }
    }
}

@Composable
fun ExpensePeriod(
    monthExpanded: Boolean,
    onMonthExpandedChanged: (Boolean) -> Unit,
    yearExpanded: Boolean,
    onYearExpandedChanged: (Boolean) -> Unit,
    monthIndex: Int,
    onMonthIndexChanged: (Int, String) -> Unit,
    yearIndex: Int,
    onYearIndexChanged: (Int, Int) -> Unit,
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
                    .padding(end = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                SmallHeadlineText(text = stringResource(id = R.string.label_reference_period_month))

                SmallHeadlineText(text = stringResource(id = R.string.label_reference_period_year))
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp),
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
        }
    }
}

@Composable
fun ExpenseCurrencyWithAmount(
    expanded: Boolean,
    onExpandedChanged: (Boolean) -> Unit,
    index: Int,
    onIndexChanged: (Int, String) -> Unit,
    amount: String,
    onAmountChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val keyboardController = LocalSoftwareKeyboardController.current

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
                    .padding(end = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SmallHeadlineText(text = stringResource(id = R.string.label_expense_currency))

                SmallHeadlineText(text = stringResource(id = R.string.label_expense_amount))
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                ExpenseDropDown(
                    elements = getCurrencies(),
                    index = index,
                    onIndexChanged = onIndexChanged,
                    expanded = expanded,
                    onExpandedChanged = onExpandedChanged,
                    weight = 0.5f
                )
                ExpenseOutlineTextField(
                    value = amount,
                    onValueChanged = onAmountChanged,
                    placeHolderTest = stringResource(id = R.string.label_placeholder_amount),
                    keyboardType = KeyboardType.Decimal,
                    keyBoardController = keyboardController,
                    weight = 0.5f
                )
            }
        }
    }
}

@Composable
fun SaveExpense(
    isEnabled: Boolean,
    isLoading: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(30.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SmallCircularButton(
            isEnabled = isEnabled,
            buttonText = stringResource(id = R.string.label_save),
            onClick = { onSave.invoke() }
        )

        if (isLoading) ProgressIndicator(size = 46, strokeWidth = 3)
    }
}