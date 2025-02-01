package ru.practicum.android.diploma.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.dto.AreaDto
import ru.practicum.android.diploma.data.dto.AreasResponse
import ru.practicum.android.diploma.data.dto.IndustriesResponse
import ru.practicum.android.diploma.data.dto.Request
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.FilterRequestRepository
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.Resource

class FilterRequestRepositoryImpl(
    private val networkClient: NetworkClient
) : FilterRequestRepository {
    companion object {
        private const val BAD_REQUEST = "BAD_REQUEST"
        private const val SUCCESSFUL_REQUEST = 200
    }

    override fun getCountries(): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(Request.CountriesRequest)
        val result: Resource<List<Area>> = if (response.resultCode == SUCCESSFUL_REQUEST) {
            val countries = (response as AreasResponse).areas.map { it.toDomain() }
            Resource.Success(countries)
        } else {
            Resource.Error(BAD_REQUEST)
        }
        emit(result)
    }

    override fun getAreasById(areaId: String): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(Request.AreasByIdRequest(areaId))
        val result: Resource<List<Area>> = if (response.resultCode == SUCCESSFUL_REQUEST) {
            val areas = mergeAreas((response as AreasResponse).areas)
            Resource.Success(areas)
        } else {
            Resource.Error(BAD_REQUEST)
        }
        emit(result)
    }

    override fun getAreas(): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(Request.AreasRequest)
        val result: Resource<List<Area>> = if (response.resultCode == SUCCESSFUL_REQUEST) {
            val areas = mergeAreas((response as AreasResponse).areas)
            Resource.Success(areas)
        } else {
            Resource.Error(BAD_REQUEST)
        }
        emit(result)
    }

    override fun getIndustries(): Flow<Resource<List<Industry>>> = flow {
        val response = networkClient.doRequest(Request.Industries)
        val result: Resource<List<Industry>> = if (response.resultCode == SUCCESSFUL_REQUEST) {
            val industries = (response as IndustriesResponse).industries.map {
                Industry(
                    it.id,
                    it.name
                )
            }
            Resource.Success(industries)
        } else {
            Resource.Error(BAD_REQUEST)
        }
        emit(result)
    }

    private fun mergeAreas(areas: List<AreaDto>): List<Area> {
        val result: ArrayList<Area> = ArrayList()
        if (areas.isNotEmpty()) {
            areas.forEach {
                if (it.parentId != null) result.add(it.toDomain())
                it.areas?.let { areas ->
                    result.addAll(mergeAreas(areas))
                }
            }
        }
        return result.sortedBy { it.name }
    }

    private fun AreaDto.toDomain(): Area {
        return Area(
            this.id,
            this.parentId,
            this.name
        )
    }
}
