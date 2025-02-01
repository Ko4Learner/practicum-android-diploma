package ru.practicum.android.diploma.presentation.ui.vacancy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.VacancyInteractor
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.Vacancy

enum class VacancyStatus {
    LOADING,
    SUCCESS,
    NOT_FOUND,
    SERVER_ERROR
}

class VacancyViewModel(
    private val vacancyInteractor: VacancyInteractor
) : ViewModel() {

    private val _vacancy = MutableLiveData<Resource<Vacancy>>()
    val vacancy: LiveData<Resource<Vacancy>> = _vacancy

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _status = MutableLiveData<VacancyStatus>()
    val status: LiveData<VacancyStatus> = _status

    fun prepareVacancy(vacancyId: String) {
        _status.value = VacancyStatus.LOADING
        viewModelScope.launch {
            val result = vacancyInteractor.getVacancy(vacancyId)
            _vacancy.value = result
            _status.value = if (result is Resource.Success) {
                VacancyStatus.SUCCESS
            } else {
                VacancyStatus.SERVER_ERROR
            }
        }
    }
}
