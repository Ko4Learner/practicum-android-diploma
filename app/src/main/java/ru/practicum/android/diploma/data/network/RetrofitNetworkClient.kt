package ru.practicum.android.diploma.data.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.practicum.android.diploma.data.dto.AreasResponse
import ru.practicum.android.diploma.data.dto.IndustriesResponse
import ru.practicum.android.diploma.data.dto.Request
import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.VacancyResponse
import ru.practicum.android.diploma.util.isConnected

class RetrofitNetworkClient(
    private val vacancyService: VacancyApi,
    private val context: Context,
) : NetworkClient {
    companion object {
        private const val RETROFIT_LOG = "RETROFIT_LOG"
        private const val SUCCESSFUL_REQUEST = 200
        private const val BAD_REQUEST = 400
        private const val CONNECT_ERR = 300
    }

    override suspend fun doRequest(dto: Request): Response {
        if (!isConnected(context)) {
            return Response().apply { resultCode = CONNECT_ERR }
        } else {
            return withContext(Dispatchers.IO) {
                try {
                    val resp = when (dto) {
                        is Request.VacanciesRequest -> vacancyService.searchVacancy(dto.options)

                        is Request.VacancyRequest -> VacancyResponse(vacancyService.getVacancy(dto.id))

                        is Request.CountriesRequest -> AreasResponse(vacancyService.getCountries())

                        is Request.AreasByIdRequest -> vacancyService.getAreasById(dto.id)

                        is Request.AreasRequest -> AreasResponse(vacancyService.getAreas())

                        is Request.Industries -> IndustriesResponse(vacancyService.getIndustries())
                    }
                    resp.apply { resultCode = SUCCESSFUL_REQUEST }
                } catch (e: HttpException) {
                    Log.d(RETROFIT_LOG, "${e.message}")
                    Response().apply { resultCode = BAD_REQUEST }
                }
            }

        }
    }
}
