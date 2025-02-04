package ru.practicum.android.diploma.presentation.ui.filter.workplace.region

import ru.practicum.android.diploma.domain.models.Area

sealed class SearchRegionScreenState {
    data object Error : SearchRegionScreenState()
    data object NoAreas : SearchRegionScreenState()
    data class ShowAreas(val areas: List<Area>) : SearchRegionScreenState()
}
