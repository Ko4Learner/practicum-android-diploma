package ru.practicum.android.diploma.presentation.ui.filter.workplace.country

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.api.FilterRequestInteractor
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.FilterParameters
import ru.practicum.android.diploma.domain.models.Resource

class SelectCountryViewModel(
    private val filterRequestInteractor: FilterRequestInteractor,
    private val filterParameters: FilterParameters
) : ViewModel() {
    private val _stateLiveData = MutableLiveData<CountryState>()
    fun observeState(): LiveData<CountryState> = _stateLiveData

    fun getCountry() {
        _stateLiveData.value = CountryState.Loading
        viewModelScope.launch {
            filterRequestInteractor
                .getCountries()
                .collect { country ->
                    processResult(country)
                }
        }
    }

    private fun processResult(resource: Resource<List<Area>>) {
        when (resource) {
            is Resource.Success -> {
                _stateLiveData.value = CountryState.Success(resource.data)
            }

            is Resource.Error -> {
                _stateLiveData.value = CountryState.Error(R.string.could_not_get_list)
            }
        }
    }

    fun saveCountry(country: Area) {
        if (country.name == "Другое") {
            filterParameters.country = Country(country.id!!, "Другие регионы")
        } else {
            filterParameters.country = Country(country.id!!, country.name)
        }
    }
}
