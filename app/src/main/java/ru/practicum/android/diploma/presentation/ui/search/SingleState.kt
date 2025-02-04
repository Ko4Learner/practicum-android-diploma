package ru.practicum.android.diploma.presentation.ui.search

sealed class SingleState {
    data object PagingErrInternet : SingleState()
    data object PagingErrServer : SingleState()
    data object NoActions : SingleState()
}
