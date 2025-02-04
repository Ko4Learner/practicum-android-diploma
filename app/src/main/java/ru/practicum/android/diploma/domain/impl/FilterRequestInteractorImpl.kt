package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.FilterRequestInteractor
import ru.practicum.android.diploma.domain.api.FilterRequestRepository
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.Resource

class FilterRequestInteractorImpl(
    private val repository: FilterRequestRepository
) : FilterRequestInteractor {
    override fun getCountries(): Flow<Resource<List<Area>>> {
        return repository.getCountries()
    }

    override fun getAreas(): Flow<Resource<List<Area>>> {
        return repository.getAreas()
    }

    override fun getAreasById(areaId: String): Flow<Resource<List<Area>>> {
        return repository.getAreasById(areaId)
    }

    override fun getIndustries(): Flow<Resource<List<Industry>>> {
        return repository.getIndustries()
    }
}
