package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.data.impl.FavouritesRepositoryImpl
import ru.practicum.android.diploma.data.impl.FilterParametersRepositoryImpl
import ru.practicum.android.diploma.data.impl.FilterRequestRepositoryImpl
import ru.practicum.android.diploma.data.impl.VacancyRepositoryImpl
import ru.practicum.android.diploma.domain.api.FavouritesRepository
import ru.practicum.android.diploma.domain.api.FilterParametersRepository
import ru.practicum.android.diploma.domain.api.FilterRequestRepository
import ru.practicum.android.diploma.domain.api.VacancyRepository

val repositoryModule = module {

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }

    single<VacancyRepository> {
        VacancyRepositoryImpl(get(), get(), androidContext(), get())
    }

    single<FilterParametersRepository> {
        FilterParametersRepositoryImpl(get(), get())
    }

    single<FilterRequestRepository> {
        FilterRequestRepositoryImpl(get())
    }
}
