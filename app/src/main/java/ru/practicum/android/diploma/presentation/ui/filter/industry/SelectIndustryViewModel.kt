package ru.practicum.android.diploma.presentation.ui.filter.industry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.FilterRequestInteractor
import ru.practicum.android.diploma.domain.models.FilterParameters
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.util.debounce

class SelectIndustryViewModel(
    private val filterRequestInteractor: FilterRequestInteractor,
    private val filterParameters: FilterParameters
) : ViewModel() {
    private val industryStateLiveData = MutableLiveData<IndustryContentState>()
    private var lastSearch = ""
    val searchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { request ->
        if (request != lastSearch) {
            lastSearch = request
            searchIndustry(request)
        }
    }

    fun getIndustryStateLiveData(): LiveData<IndustryContentState> = industryStateLiveData

    fun getIndustries() {
        viewModelScope.launch {
            industryStateLiveData.postValue(IndustryContentState.Loading)
            filterRequestInteractor.getIndustries().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data.isEmpty()) {
                            industryStateLiveData.postValue(IndustryContentState.Error)
                        } else {
                            industryStateLiveData.postValue(
                                IndustryContentState.Content(result.data.sortedBy { it.name })
                            )
                        }
                    }

                    is Resource.Error -> {
                        industryStateLiveData.postValue(IndustryContentState.Error)
                    }
                }
            }
        }
    }

    private fun searchIndustry(searchText: String) {
        viewModelScope.launch {
            industryStateLiveData.postValue(IndustryContentState.Loading)
            filterRequestInteractor.getIndustries().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val findItems = ArrayList<Industry>()
                        result.data.forEach {
                            if (it.name.contains(searchText, ignoreCase = true)) findItems.add(it)
                        }
                        if (findItems.isEmpty()) {
                            industryStateLiveData.postValue(IndustryContentState.Error)
                        } else {
                            industryStateLiveData.postValue(
                                IndustryContentState.Content(findItems.sortedBy { it.name })
                            )
                        }
                    }

                    is Resource.Error -> {
                        industryStateLiveData.postValue(IndustryContentState.Error)
                    }
                }
            }
        }
    }

    fun chooseIndustry(industry: Industry?) {
        filterParameters.industry = Industry(industry!!.id, industry.name)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
