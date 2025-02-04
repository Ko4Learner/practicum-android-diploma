package ru.practicum.android.diploma.presentation.ui.filter.workplace

import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.models.FilterParameters

class SelectWorkplaceViewModel(
    private val filterParameters: FilterParameters
) : ViewModel() {
    fun getFilterParameters(): FilterParameters {
        return filterParameters
    }
}
