package ru.practicum.android.diploma.data.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.data.db.AppDatabase
import ru.practicum.android.diploma.data.db.entity.FavoriteVacancyEntity
import ru.practicum.android.diploma.domain.api.FavouritesRepository
import ru.practicum.android.diploma.domain.models.Salary
import ru.practicum.android.diploma.domain.models.Vacancy

open class FavouritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val gson: Gson
) : FavouritesRepository {

    override suspend fun insertVacancy(vacancy: Vacancy) {
        val vacancyEntity = vacancy.toEntity()
        appDatabase.getFavoriteVacancyDao().saveVacancy(vacancyEntity)
    }

    override suspend fun deleteVacancy(vacancy: Vacancy) {
        val vacancyEntity = vacancy.toEntity()
        appDatabase.getFavoriteVacancyDao().deleteVacancy(vacancyEntity)
    }

    override fun getVacancies(): Flow<List<Vacancy>> {
        return appDatabase.getFavoriteVacancyDao().getVacancies()
            .map { vacancyEntity -> vacancyEntity.map { it.toDomain() } }
    }

    override suspend fun isVacancyFavorite(vacancyId: String): Boolean {
        val favoriteId = appDatabase.getFavoriteVacancyDao().getVacancies().first()
        return favoriteId.any { it.id == vacancyId }
    }

    private fun FavoriteVacancyEntity.toDomain(): Vacancy {
        return Vacancy(
            id = this.id,
            name = this.name,
            logoUrl90 = this.logoUrl,
            area = this.area,
            salary = this.salary?.let { gson.fromJson(it, object : TypeToken<Salary>() {}.type) },
            employerName = this.employerName,
            description = this.description,
            alternateUrl = this.alternateUrl,
            employment = this.employment,
            experience = this.experience,
            isFavorite = true,
            keySkills = this.keySkills?.let { gson.fromJson(it, object : TypeToken<List<String>>() {}.type) }
        )
    }

    private fun Vacancy.toEntity(): FavoriteVacancyEntity {
        return FavoriteVacancyEntity(
            id = this.id,
            name = this.name,
            logoUrl = this.logoUrl90,
            area = this.area,
            salary = this.salary?.let { gson.toJson(it) },
            employerName = this.employerName,
            description = this.description,
            alternateUrl = this.alternateUrl,
            employment = this.employment,
            experience = this.experience,
            keySkills = this.keySkills?.let { gson.toJson(it) }
        )
    }
}
