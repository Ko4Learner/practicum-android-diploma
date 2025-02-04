package ru.practicum.android.diploma.presentation.ui.vacancy

import ru.practicum.android.diploma.domain.models.Vacancy

sealed class VacancyState {
    data object Loading : VacancyState()
    data class Content(val vacancy: Vacancy) : VacancyState()
    data object NotFound : VacancyState()
    data object ServerError : VacancyState()
    data object NoInternet : VacancyState()
}
