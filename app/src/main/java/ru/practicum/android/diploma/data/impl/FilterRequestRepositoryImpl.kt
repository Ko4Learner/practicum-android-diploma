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
    override fun getCountries(): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(Request.AreasRequest)
        val result: Resource<List<Area>> = when (response.resultCode) {
            SUCCESSFUL_REQUEST -> {
                val countries = selectCountries((response as AreasResponse).areas)
                Resource.Success(countries)
            }

            CONNECT_ERR -> {
                Resource.Error(NO_CONNECTION)
            }

            else -> {
                Resource.Error(BAD_REQUEST)
            }
        }
        emit(result)

    }

    override fun getAreasById(areaId: String): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(Request.AreasByIdRequest(areaId))
        val result: Resource<List<Area>> = when (response.resultCode) {
            SUCCESSFUL_REQUEST -> {
                val areas = mergeAreas((response as AreasResponse).areas)
                Resource.Success(areas)
            }

            CONNECT_ERR -> {
                Resource.Error(NO_CONNECTION)
            }

            else -> {
                Resource.Error(BAD_REQUEST)
            }
        }
        emit(result)
    }

    override fun getAreas(): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(Request.AreasRequest)
        val result: Resource<List<Area>> = when (response.resultCode) {
            SUCCESSFUL_REQUEST -> {
                val areas = mergeAreas((response as AreasResponse).areas)
                Resource.Success(areas)
            }

            CONNECT_ERR -> {
                Resource.Error(NO_CONNECTION)
            }

            else -> {
                Resource.Error(BAD_REQUEST)
            }
        }
        emit(result)
    }

    override fun getIndustries(): Flow<Resource<List<Industry>>> = flow {
        val response = networkClient.doRequest(Request.Industries)
        val result: Resource<List<Industry>> = when (response.resultCode) {
            SUCCESSFUL_REQUEST -> {
                val industries = (response as IndustriesResponse).industries.map {
                    Industry(
                        it.id,
                        it.name
                    )
                }
                Resource.Success(industries)
            }

            CONNECT_ERR -> {
                Resource.Error(NO_CONNECTION)
            }

            else -> {
                Resource.Error(BAD_REQUEST)
            }
        }
        emit(result)
    }

    private fun mergeAreas(areas: List<AreaDto>): List<Area> {
        val result: ArrayList<Area> = ArrayList()
        if (areas.isNotEmpty()) {
            areas.forEach {
                if (it.parentId != null) result.add(it.toDomain())
                it.areas?.let { areas ->
                    val mergedAreas = changeParentId(mergeAreas(areas), it.parentId)
                    result.addAll(mergedAreas)
                }

            }
        }
        return result.sortedBy { it.name }
    }

    private fun selectCountries(areas: List<AreaDto>): List<Area> {
        val countries = mutableListOf<Area>()
        areas.forEach {
            if (it.parentId == null && it.id != ELSE_AREAS_ID) countries.add(it.toDomain())
        }
        areas.forEach {
            if (it.id == ELSE_AREAS_ID) countries.add(it.toDomain())
        }
        return countries
    }

    private fun changeParentId(areas: List<Area>, parentId: String?): List<Area> {
        return if (parentId != null) {
            areas.map {
                Area(
                    it.id,
                    parentId,
                    it.name
                )
            }
        } else {
            areas
        }
    }

    private fun AreaDto.toDomain(): Area {
        return Area(
            this.id,
            this.parentId,
            this.name
        )
    }

    companion object {
        private const val ELSE_AREAS_ID = "1001"
        private const val NO_CONNECTION = "NO_CONNECTION"
        private const val BAD_REQUEST = "BAD_REQUEST"
        private const val CONNECT_ERR = 300
        private const val SUCCESSFUL_REQUEST = 200
    }
}
