package ru.practicum.android.diploma.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.FilterParameters

interface FilterParametersInteractor {
    suspend fun saveParameters(parameters: FilterParameters)
    fun getParameters(): Flow<FilterParameters>
}
