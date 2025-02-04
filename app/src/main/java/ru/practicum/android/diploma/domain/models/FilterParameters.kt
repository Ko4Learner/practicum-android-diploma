package ru.practicum.android.diploma.domain.models

class FilterParameters(
    var country: Country? = null,
    var area: Area? = null,
    var industry: Industry? = null,
    var expectedSalary: Int? = null,
    var onlyWithSalary: Boolean? = null
) {
    companion object {
        private const val INDUSTRY = "industry"
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
            result[AREA_QUERY] = area!!.id!!
        } else {
            if (country != null) {
                result[AREA_QUERY] = country!!.id
            }
        }
        if (expectedSalary != null && expectedSalary != 0) {
            result[SALARY_QUERY] =
                expectedSalary.toString()
        }
        if (onlyWithSalary != null) {
            result[ONLY_WITH_SALARY_QUERY] = onlyWithSalary.toString()
        }
        if (industry != null) {
            result[INDUSTRY] = industry!!.id
        }
        return result
    }

    fun copy(
        country: Country? = this.country,
        area: Area? = this.area,
        industry: Industry? = this.industry,
        expectedSalary: Int? = this.expectedSalary,
        onlyWithSalary: Boolean? = this.onlyWithSalary
    ) = FilterParameters(country, area, industry, expectedSalary, onlyWithSalary)

    override fun hashCode(): Int {
        var result = country?.hashCode() ?: 0
        result = 31 * result + (area?.hashCode() ?: 0)
        result = 31 * result + (industry?.hashCode() ?: 0)
        result = 31 * result + (expectedSalary ?: 0)
        result = 31 * result + (onlyWithSalary?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilterParameters

        if (country != other.country) return false
        if (area != other.area) return false
        if (industry != other.industry) return false
        if (expectedSalary != other.expectedSalary) return false
        if (onlyWithSalary != other.onlyWithSalary) return false

        return true
    }
}
