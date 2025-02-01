package ru.practicum.android.diploma.presentation.ui.search

import ru.practicum.android.diploma.domain.models.Page

sealed class SearchScreenState {
    data object StartScreen : SearchScreenState()
    data object EmptyScreen : SearchScreenState()
    data object Loading : SearchScreenState()
    data object InternetConnError : SearchScreenState()
    data object ServerError : SearchScreenState()
    data object NoVacancies : SearchScreenState()
    data object PagingErrInternet : SearchScreenState()
    data object PagingSuccess : SearchScreenState()
    data object PagingErrServer : SearchScreenState()
    data class ShowVacancies(val page: Page) : SearchScreenState()
}
