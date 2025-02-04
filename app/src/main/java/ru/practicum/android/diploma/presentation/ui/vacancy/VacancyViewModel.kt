package ru.practicum.android.diploma.presentation.ui.vacancy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.FavouritesInteractor
import ru.practicum.android.diploma.domain.api.VacancyInteractor
import ru.practicum.android.diploma.domain.models.Resource

class VacancyViewModel(
    private val vacancyInteractor: VacancyInteractor,
    private val favoritesInteractor: FavouritesInteractor
) : ViewModel() {

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _state = MutableLiveData<VacancyState>()
    val state: LiveData<VacancyState> = _state

    fun prepareVacancy(vacancyId: String) {
        viewModelScope.launch {
            _state.value = VacancyState.Loading
            when (val vacancyResource = vacancyInteractor.getVacancy(vacancyId)) {
                is Resource.Success -> {
                    _state.value = VacancyState.Content(vacancyResource.data)
                }

                is Resource.Error -> {
                    _state.value = when {
                        vacancyResource.message.contains("404") -> VacancyState.NotFound
                        vacancyResource.message.contains("500") -> VacancyState.ServerError
                        else -> VacancyState.NoInternet
                    }
                }
            }
        }
        viewModelScope.launch {
            _isFavorite.value = favoritesInteractor.isVacancyFavorite(vacancyId)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is VacancyState.Content) {
                val isCurrentlyFavorite = _isFavorite.value ?: false
                val updateVacancy = currentState.vacancy.copy(isFavorite = !isCurrentlyFavorite)
                _isFavorite.value = updateVacancy.isFavorite
                if (updateVacancy.isFavorite) {
                    favoritesInteractor.likeVacancy(updateVacancy)
                } else {
                    favoritesInteractor.dislikeVacancy(updateVacancy)
                }
            }
        }
    }
}
