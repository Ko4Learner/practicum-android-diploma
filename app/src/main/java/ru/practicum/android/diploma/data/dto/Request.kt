package ru.practicum.android.diploma.data.dto

sealed interface Request {
    data object Industries : Request
    data object AreasRequest : Request
    data object CountriesRequest : Request
    data class AreasByIdRequest(val id: String) : Request
    data class VacancyRequest(val id: String) : Request
    data class VacanciesRequest(val options: Map<String, String>) : Request
}
