package ru.practicum.android.diploma.presentation.ui.filter.workplace.region

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.domain.api.FilterRequestInteractor
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.FilterParameters
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.util.debounce

class SelectRegionViewModel(
    private val filterRequestInteractor: FilterRequestInteractor,
    private val filterParameters: FilterParameters
) : ViewModel() {
    private var isLoadSuccess = false
    private var initListAreas = mutableListOf<Area>()
    private var initListAllAreas = mutableListOf<Area>()
    private var initListCountries = mutableListOf<Area>()
    private val screenState = MutableLiveData<SearchRegionScreenState>()
    fun getScreenState(): LiveData<SearchRegionScreenState> = screenState

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                filterRequestInteractor.getAreas().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            initListAllAreas.addAll(result.data)
                        }

                        else -> {
                        }
                    }
                }
                filterRequestInteractor.getCountries().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            initListCountries.addAll(result.data)
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }

    val searchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { request ->
        if (request.isNotEmpty()) {
            val currentList = initListAreas.filter { it.name.startsWith(request, true) }
            if (currentList.isNotEmpty()) {
                screenState.postValue(SearchRegionScreenState.ShowAreas(currentList))
            } else {
                screenState.postValue(SearchRegionScreenState.NoAreas)
            }
        } else {
            screenState.postValue(SearchRegionScreenState.ShowAreas(initListAreas))
        }
    }

    fun initLoadAreas() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (filterParameters.country == null) {
                    filterRequestInteractor.getAreas().collect { result -> resultHandler(result) }
                } else {
                    var countryId: String? = null
                    filterRequestInteractor.getCountries().collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                val listCountries = result.data
                                countryId =
                                    listCountries.find { it.name == filterParameters.country!!.name }?.id
                            }

                            else -> {
                            }
                        }
                    }
                    if (!countryId.isNullOrEmpty()) {
                        filterRequestInteractor.getAreasById(filterParameters.country!!.id)
                            .collect { result -> resultHandler(result) }
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

    fun saveRegion(region: Area) {
        filterParameters.area = Area(region.id, region.parentId, region.name)
        if (filterParameters.country == null) {
            saveCountryById(region.parentId)
        }
    }

    private fun saveCountryById(parentId: String?) {
        var itemId = parentId
        var parent: Area?

        while (true) {
            parent = initListAllAreas.find { cId -> itemId.equals(cId.id) }
            if (parent?.parentId == null) {
                break
            } else {
                itemId = parent.parentId
            }
        }
        parent = initListCountries.find { cId -> itemId.equals(cId.id) }
        parent?.let { filterParameters.country = Country(it.id!!, it.name) }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
