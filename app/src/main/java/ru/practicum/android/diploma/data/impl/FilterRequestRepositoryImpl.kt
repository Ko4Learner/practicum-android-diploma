package ru.practicum.android.diploma.data.impl

import android.content.Context
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
import ru.practicum.android.diploma.util.isConnected

class FilterRequestRepositoryImpl(
    private val networkClient: NetworkClient,
    private val context: Context
) : FilterRequestRepository {
    override fun getCountries(): Flow<Resource<List<Area>>> = flow {
        if (isConnected(context)) {
            val response = networkClient.doRequest(Request.CountriesRequest)
            val result: Resource<List<Area>> = if (response.resultCode == SUCCESSFUL_REQUEST) {
                val countries = (response as AreasResponse).areas.map { it.toDomain() }
                Resource.Success(countries)
            } else {
                Resource.Error(BAD_REQUEST)
            }
            emit(result)
        } else {
            emit(Resource.Error(NO_CONNECTION))
        }
    }

    override fun getAreasById(areaId: String): Flow<Resource<List<Area>>> = flow {
        if (isConnected(context)) {
            val response = networkClient.doRequest(Request.AreasByIdRequest(areaId))
            val result: Resource<List<Area>> = if (response.resultCode == SUCCESSFUL_REQUEST) {
                val areas = mergeAreas((response as AreasResponse).areas)
                Resource.Success(areas)
            } else {
                Resource.Error(BAD_REQUEST)
            }
            emit(result)
        } else {
            emit(Resource.Error(NO_CONNECTION))
        }
    }

    override fun getAreas(): Flow<Resource<List<Area>>> = flow {
        if (isConnected(context)) {
            val response = networkClient.doRequest(Request.AreasRequest)
            val result: Resource<List<Area>> = if (response.resultCode == SUCCESSFUL_REQUEST) {
                val areas = mergeAreas((response as AreasResponse).areas)
                Resource.Success(areas)
            } else {
                Resource.Error(BAD_REQUEST)
            }
            emit(result)
        } else {
            emit(Resource.Error(NO_CONNECTION))
        }
    }

    override fun getIndustries(): Flow<Resource<List<Industry>>> = flow {
        if (isConnected(context)) {
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
        } else {
            emit(Resource.Error(NO_CONNECTION))
        }
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
        private const val NO_CONNECTION = "NO_CONNECTION"
        private const val BAD_REQUEST = "BAD_REQUEST"
        private const val SUCCESSFUL_REQUEST = 200
    }
}
