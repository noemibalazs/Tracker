package com.noemi.tracker.model

import androidx.compose.ui.graphics.Color
import com.noemi.tracker.ui.theme.apartmentRent
import com.noemi.tracker.ui.theme.apparel
import com.noemi.tracker.ui.theme.communication
import com.noemi.tracker.ui.theme.culture
import com.noemi.tracker.ui.theme.electricity
import com.noemi.tracker.ui.theme.groceries
import com.noemi.tracker.ui.theme.housingLoan
import com.noemi.tracker.ui.theme.other
import com.noemi.tracker.ui.theme.travel

enum class ExpensesTypes {
    ApartmentRent,
    HousingLoan,
    Electricity,
    Communication,
    Travel,
    Groceries,
    Culture,
    Apparel,
    Other;

    companion object {

        fun getColor(type: String): Color =
            when (type) {
                ApartmentRent.name -> apartmentRent
                HousingLoan.name -> housingLoan
                Electricity.name -> electricity
                Communication.name -> communication
                Travel.name -> travel
                Groceries.name -> groceries
                Culture.name -> culture
                Apparel.name -> apparel
                Other.name -> other
                else -> Color.Cyan
            }
    }
}