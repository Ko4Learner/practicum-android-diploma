package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.practicum.android.diploma.domain.models.FilterParameters
import ru.practicum.android.diploma.presentation.ui.favourites.FavouritesViewModel
import ru.practicum.android.diploma.presentation.ui.search.SearchViewModel
import ru.practicum.android.diploma.presentation.ui.vacancy.VacancyViewModel
import ru.practicum.android.diploma.presentation.ui.filter.settings.FilterSettingsViewModel
import ru.practicum.android.diploma.presentation.ui.filter.industry.SelectIndustryViewModel
import ru.practicum.android.diploma.presentation.ui.filter.workplace.SelectWorkplaceViewModel
import ru.practicum.android.diploma.presentation.ui.filter.workplace.country.SelectCountryViewModel
import ru.practicum.android.diploma.presentation.ui.filter.workplace.region.SelectRegionViewModel

val viewModelModule = module {
    viewModelOf(::FilterSettingsViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::VacancyViewModel)
    viewModelOf(::FavouritesViewModel)
    viewModelOf(::SelectIndustryViewModel)
    viewModelOf(::SelectWorkplaceViewModel)
    viewModelOf(::SelectCountryViewModel)
    viewModelOf(::SelectRegionViewModel)
    single { FilterParameters() }
}

