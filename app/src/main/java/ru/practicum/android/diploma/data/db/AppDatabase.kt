package ru.practicum.android.diploma.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.practicum.android.diploma.data.db.dao.FavoriteVacanciesDao
import ru.practicum.android.diploma.data.db.entity.FavoriteVacancyEntity

@Database(
    version = 2,
    entities = [
        FavoriteVacancyEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getFavoriteVacancyDao(): FavoriteVacanciesDao

}
