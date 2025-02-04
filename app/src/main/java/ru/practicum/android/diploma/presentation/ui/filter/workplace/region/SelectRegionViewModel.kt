package ru.practicum.android.diploma.presentation.ui.filter.workplace.region

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.domain.api.FilterRequestInteractor
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.FilterParameters
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.util.debounce

class SelectRegionViewModel(
    private val filterRequestInteractor: FilterRequestInteractor,
    private val filterParameters: FilterParameters
) : ViewModel() {
    private var isLoadSuccess = false
    private var initListAreas = mutableListOf<Area>()
    private val screenState = MutableLiveData<SearchRegionScreenState>()
    fun getScreenState(): LiveData<SearchRegionScreenState> = screenState

    val searchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { request ->
        if (request.isNotEmpty()) {
            Log.d("mytag", "++click_debounce_ext++request=$request")
            val currentList = initListAreas.filter { it.name.startsWith(request, true) }
            if (currentList.isNotEmpty()) {
                screenState.postValue(SearchRegionScreenState.ShowAreas(currentList))
                Log.d("mytag", "++click_debounce++")
            }
        } else {
            screenState.postValue(SearchRegionScreenState.ShowAreas(initListAreas))
        }
    }

    fun initLoadAreas() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (filterParameters.country.isNullOrEmpty()) {
                    filterRequestInteractor.getAreas().collect() { result -> resultHandler(result) }
                } else {
                    var countryId: String? = null
                    filterRequestInteractor.getCountries().collect() { result ->
                        when (result) {
                            is Resource.Success -> {
                                val listCountries = result.data
                                countryId = listCountries.find { it.name == filterParameters.country }?.id
                            }

                            else -> {
                            }
                        }
                    }
                    if (!countryId.isNullOrEmpty()) {
                        filterRequestInteractor.getAreasById(filterParameters.country!!)
                            .collect() { result -> resultHandler(result) }
                    } else {
                        screenState.postValue(SearchRegionScreenState.Error)
                    }
                }
            }
        }
    }

    private fun resultHandler(result: Resource<List<Area>>) {
        when (result) {
            is Resource.Error -> {
                screenState.postValue(SearchRegionScreenState.Error)
            }

            is Resource.Success -> {
                if (result.data.isEmpty()) {
                    screenState.postValue(SearchRegionScreenState.NoAreas)
                } else {
                    initListAreas.addAll(result.data)
                    isLoadSuccess = true
                    screenState.postValue(SearchRegionScreenState.ShowAreas(initListAreas))
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
