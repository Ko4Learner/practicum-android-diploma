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
    companion object {
        private const val DEF_PARAMETERS = ""
        private const val KEY_FOR_PARAMETERS = "KEY_FOR_PARAMETERS"
    }

    override suspend fun saveParameters(parameters: FilterParameters) {
        sharedPrefs.edit()
            .putString(KEY_FOR_PARAMETERS, gson.toJson(parameters))
            .apply()
    }

    override fun getParameters(): Flow<FilterParameters> = flow {
        val parameters = gson.fromJson(
            sharedPrefs.getString(KEY_FOR_PARAMETERS, DEF_PARAMETERS),
            FilterParameters::class.java
        )
        emit(parameters)
    }
}
