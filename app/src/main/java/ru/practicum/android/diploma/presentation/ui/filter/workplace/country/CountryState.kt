package ru.practicum.android.diploma.presentation.ui.filter.workplace.country

import ru.practicum.android.diploma.domain.models.Area

sealed interface CountryState {
    object Loading : CountryState
    data class Success(val countries: List<Area>) : CountryState
    data class Error(val errorMessage: String) : CountryState
}
