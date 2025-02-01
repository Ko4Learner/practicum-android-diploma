package ru.practicum.android.diploma.domain.models

data class Vacancy(
    val id: String,
    val name: String,
    val logoUrl90: String?,
    val area: String,
    val salary: Salary?,
    val employerName: String,
    val employment: String?,
    val experience: String?,
    val description: String?,
    val keySkills: List<String>?,
    val alternateUrl: String,
    val isFavorite: Boolean = false
)
