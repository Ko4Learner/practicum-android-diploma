package ru.practicum.android.diploma.data.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.domain.api.FilterParametersRepository
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterParametersRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) : FilterParametersRepository {

    override suspend fun saveParameters(parameters: FilterParameters) {
        if (parameters.onlyWithSalary == false) {
            parameters.onlyWithSalary = null
        }
        sharedPrefs.edit()
            .putString(KEY_FOR_PARAMETERS, gson.toJson(parameters))
            .apply()
    }

    override fun getParameters(): Flow<FilterParameters> = flow {
        val parameters = gson.fromJson(
            sharedPrefs.getString(KEY_FOR_PARAMETERS, gson.toJson(FilterParameters())),
            FilterParameters::class.java
        )
        emit(parameters)
    }

    companion object {
        private const val KEY_FOR_PARAMETERS = "KEY_FOR_PARAMETERS"
    }
}
