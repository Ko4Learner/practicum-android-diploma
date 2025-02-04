package ru.practicum.android.diploma.presentation.ui.filter.settings

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

    private var sharedPreferencesFilter: FilterParameters? = null

    private val filterLiveData = MutableLiveData<FilterParameters>()
    fun observeFilter(): LiveData<FilterParameters> = filterLiveData

    private val applyButtonLiveData = MutableLiveData<Boolean>()
    fun observeApplyButtonLiveData(): LiveData<Boolean> = applyButtonLiveData

    private val resetButtonLiveData = MutableLiveData<Boolean>()
    fun observeResetButtonLiveData(): LiveData<Boolean> = resetButtonLiveData

    init {
        viewModelScope.launch {
            filterInteractor.getParameters()
                .collect { param ->
                    setupFilterParameters(param)
                }
        }
    }

    fun changeExpectedSalary(salary: String) {
        filterParameters.expectedSalary = if (salary.isNotEmpty()) {
            salary.toInt()
        } else {
            null
        }
        equalsFilters()
        checkEmptyFilter()
    }

    fun clearArea(){
        filterParameters.area = null
        equalsFilters()
        checkEmptyFilter()
    }

    fun clearIndustry(){
        filterParameters.industry = null
        equalsFilters()
        checkEmptyFilter()
    }

    fun changeOnlyWithSalary() {
        filterParameters.onlyWithSalary = !filterParameters.onlyWithSalary!!
        equalsFilters()
        checkEmptyFilter()
    }

    fun equalsFilters() {
        applyButtonLiveData.postValue(filterParameters != sharedPreferencesFilter)
    }

    fun saveFilterParameters() {
        viewModelScope.launch { filterInteractor.saveParameters(filterParameters) }
        sharedPreferencesFilter = filterParameters.copy()
        equalsFilters()
    }

    fun resetFilterParameters() {
        viewModelScope.launch {
            filterInteractor.saveParameters(
                FilterParameters(
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
        }
        filterParameters.apply {
            country = null
            area = null
            industry = null
            expectedSalary = null
            onlyWithSalary = false
        }
        sharedPreferencesFilter = filterParameters.copy()
        postValueFilter(filterParameters)
        equalsFilters()
    }

    fun checkEmptyFilter() {
        resetButtonLiveData.postValue(
            filterParameters != FilterParameters(
                null,
                null,
                null,
                null,
                false
            )
        )
    }

    private fun setupFilterParameters(parameters: FilterParameters) {
        filterParameters.apply {
            country = parameters.country
            area = parameters.area
            industry = parameters.industry
            expectedSalary = parameters.expectedSalary
            onlyWithSalary = parameters.onlyWithSalary ?: false
        }
        sharedPreferencesFilter = filterParameters.copy()
        postValueFilter(filterParameters)
    }

    private fun postValueFilter(parameters: FilterParameters) {
        filterLiveData.postValue(parameters)
        checkEmptyFilter()
    }
}
