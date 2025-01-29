package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.ui.vacancy.VacancyViewModel
import ru.practicum.android.diploma.presentation.ui.favourites.FavouritesViewModel
import ru.practicum.android.diploma.presentation.ui.search.SearchViewModel

val viewModelModule = module {

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        VacancyViewModel(get())
    }

    viewModel {
        FavouritesViewModel(get())
    }
}
