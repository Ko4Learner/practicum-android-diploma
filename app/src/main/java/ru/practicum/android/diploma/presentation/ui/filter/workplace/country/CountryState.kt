package ru.practicum.android.diploma.presentation.ui.filter.workplace.country

import androidx.annotation.StringRes
import ru.practicum.android.diploma.domain.models.Area

sealed interface CountryState {
    object Loading : CountryState
    data class Success(
        val countries: List<Area>
    ) : CountryState

    data class Error(
        @StringRes val message: Int
    ) : CountryState
}
