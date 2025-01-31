package ru.practicum.android.diploma.domain.models

import android.util.Log

data class FilterParameters(
    var country: String? = null,
    var area: String? = null,
    var professionalRole: String? = null,
    var expectedSalary: Int? = null,
    var onlyWithSalary: Boolean? = null
) {
    companion object {
        private const val ONLY_WITH_SALARY_QUERY = "only_with_salary"
        private const val SALARY_QUERY = "salary"
        private const val AREA_QUERY = "area"
        private const val TEXT_QUERY = "text"
        private const val PAGE_QUERY = "page"
    }

    fun makeRequest(text: String, page: Int): Map<String, String> {
        val result = mutableMapOf<String, String>()
        result[TEXT_QUERY] = text
        result[PAGE_QUERY] = page.toString()
        if (area != null) {
            result[AREA_QUERY] = area!!
        } else if (country != null) result[AREA_QUERY] = country!!
        if (expectedSalary != null && expectedSalary != 0) result[SALARY_QUERY] = expectedSalary.toString()
        if (onlyWithSalary != null) result[ONLY_WITH_SALARY_QUERY] = onlyWithSalary.toString()
        Log.d("RETROFIT_LOG", "$result")
        return result
    }
}
