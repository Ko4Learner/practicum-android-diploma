package ru.practicum.android.diploma.presentation.ui.filter.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.FilterParametersInteractor
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterSettingsViewModel(
    private val filterParameters: FilterParameters,
    private val filterInteractor: FilterParametersInteractor
) : ViewModel() {

    private val _filter = MutableLiveData<FilterParameters>()
    fun observeFilter(): LiveData<FilterParameters> = _filter

    init {
        viewModelScope.launch {
            filterInteractor.getParameters()
                .collect { param -> setupFilterParameters(param) }
            Log.d("myTag", filterParameters.toString())
        }.invokeOnCompletion { _filter.postValue(filterParameters) }
    }

    private fun setupFilterParameters(parameters: FilterParameters) {
        filterParameters.apply {
            country = parameters.country
            area = parameters.area
            industry = parameters.industry
            expectedSalary = parameters.expectedSalary
            onlyWithSalary = parameters.onlyWithSalary
        }
    }
}
