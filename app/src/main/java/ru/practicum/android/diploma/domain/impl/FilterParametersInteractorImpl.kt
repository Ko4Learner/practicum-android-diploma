package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.FilterParametersInteractor
import ru.practicum.android.diploma.domain.api.FilterParametersRepository
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterParametersInteractorImpl(
    private val repository: FilterParametersRepository
) : FilterParametersInteractor {
    override suspend fun saveParameters(parameters: FilterParameters) {
        repository.saveParameters(parameters)
    }

    override fun getParameters(): Flow<FilterParameters> {
        return repository.getParameters()
    }
}
