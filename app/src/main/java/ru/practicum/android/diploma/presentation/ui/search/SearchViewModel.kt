package ru.practicum.android.diploma.presentation.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.domain.api.VacancyInteractor
import ru.practicum.android.diploma.domain.models.Page
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.util.debounce

class SearchViewModel(
    private val vacancyInteractor: VacancyInteractor
) : ViewModel() {

    private var lastSearch: String = ""
    private var page = 0
    private var pages = 0
    private val vacancies = ArrayList<Vacancy>()
    private var isPadding = false

    private val screenState = MutableLiveData<SearchScreenState>(SearchScreenState.StartScreen)
    fun getScreenState(): LiveData<SearchScreenState> = screenState

    private val screenToast = MutableLiveData<SingleState>(SingleState.NoActions)
    fun getScreenToast(): LiveData<SingleState> = screenToast
    fun resetScreenToast() {
        screenToast.value = SingleState.NoActions
    }

    val searchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { request ->

        if (request != lastSearch) {
            lastSearch = request
            screenState.value = SearchScreenState.Loading
            startSearch(request, 0)

        }

    }

    private fun startSearch(request: String, page: Int) {
        val options: HashMap<String, String> = HashMap()
        options["text"] = request
        if (page != 0) {
            options["page"] = page.toString()
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                vacancyInteractor
                    .getVacancies(options)
                    .collect { result -> resultHandler(result) }
            }
        }
    }

    private fun resultHandler(result: Resource<Page>) {
        when (result) {
            is Resource.Error -> {
                if ((!isPadding) && (result.message == BAD_REQUEST)) {
                    screenState.postValue(SearchScreenState.ServerError)
                } else if ((!isPadding) && (result.message == CONNECT_ERR)) {
                    screenState.postValue(SearchScreenState.InternetConnError)
                } else if ((isPadding) && (result.message == BAD_REQUEST)) {
                    screenToast.postValue(SingleState.PagingErrServer)
                    SearchScreenState.NoActions
                } else {
                    screenToast.postValue(SingleState.PagingErrInternet)
                    SearchScreenState.NoActions
                }
            }

            is Resource.Success -> {
                if (!isPadding) {
                    vacancies.clear()
                }
                page = result.data.page
                pages = result.data.pages
                vacancies.addAll(result.data.vacancies)
                if (vacancies.isEmpty()) {
                    screenState.postValue(SearchScreenState.NoVacancies)
                } else {
                    screenState.postValue(SearchScreenState.ShowVacancies(result.data.copy(vacancies = vacancies)))
                }
            }
        }

       // screenState.postValue(state)
        isPadding = false

    }

    fun onLastItemReached() {
        if ((page < (pages - 1)) && !isPadding) {
            isPadding = true
            screenState.value = SearchScreenState.PagingSuccess
            startSearch(lastSearch, ++page)
        }
        Log.d("mytag", "---onLastItemReached:--- ")
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val BAD_REQUEST = "400"
        private const val CONNECT_ERR = "300"
    }
}
