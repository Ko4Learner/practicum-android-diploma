package ru.practicum.android.diploma.presentation.ui.filter.industry

import ru.practicum.android.diploma.domain.models.Industry

sealed interface IndustryContentState {
    data object Loading : IndustryContentState
    data object Error : IndustryContentState
    data class Content(val data: List<Industry>) : IndustryContentState
}
