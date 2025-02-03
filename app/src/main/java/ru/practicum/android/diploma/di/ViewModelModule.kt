package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.practicum.android.diploma.domain.models.FilterParameters
import ru.practicum.android.diploma.presentation.ui.favourites.FavouritesViewModel
import ru.practicum.android.diploma.presentation.ui.search.SearchViewModel
import ru.practicum.android.diploma.presentation.ui.vacancy.VacancyViewModel
import ru.practicum.android.diploma.presentation.ui.filter.settings.FilterSettingsViewModel

val viewModelModule = module {

    viewModelOf(::FilterSettingsViewModel)

    viewModelOf(::SearchViewModel)

    viewModelOf(::VacancyViewModel)

    viewModelOf(::FavouritesViewModel)

    single { FilterParameters() }
}

