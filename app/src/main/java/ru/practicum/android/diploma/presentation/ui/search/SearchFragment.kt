package ru.practicum.android.diploma.presentation.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.presentation.adapter.VacancyAdapter
import ru.practicum.android.diploma.util.debounce

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchAdapter = VacancyAdapter()
    private var onClickVacancy: (Vacancy) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickVacancy = debounce<Vacancy>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { vacancy ->
            val bundle = bundleOf(KEY_VACANCY to vacancy.id)
            findNavController().navigate(R.id.action_searchFragment_to_vacancyFragment, bundle)
        }

        searchAdapter.onItemClick = { vacancy -> onClickVacancy(vacancy) }

        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.iconSearchField.setImageResource(R.drawable.del_search_string_icon)
                } else {
                    binding.iconSearchField.setImageResource(R.drawable.search_icon_search_fragment)
                    renderStartScreen()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.searchDebounce(s?.toString() ?: "")
            }
        })
        binding.apply {
            recyclerView.adapter = searchAdapter
            recyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            iconFilter.setOnClickListener {
                findNavController().navigate(R.id.action_searchFragment_to_filterSettingsFragment)
            }
        }
        viewModel.getScreenState().observe(viewLifecycleOwner) { state ->
            clearScreen()
            renderScreen(state)
        }
        viewModel.getTypeFilterIcon().observe(viewLifecycleOwner) {
            binding.iconFilter.setImageDrawable(
                if (it) {
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.filter_icon_search_fragment
                    )
                } else {
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.no_empty_filter_icon
                    )
                }
            )
        }
        viewModel.getScreenToast().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SingleState.PagingErrServer -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_occurred),
                        Toast.LENGTH_LONG
                    ).show()
                    binding.progressBar.isVisible = false
                    viewModel.resetScreenToast()
                }

                is SingleState.PagingErrInternet -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.check_connect),
                        Toast.LENGTH_LONG
                    ).show()
                    binding.progressBar.isVisible = false
                    viewModel.resetScreenToast()
                }

                is SingleState.NoActions -> {}
            }
        }
        binding.iconSearchField.setOnClickListener { binding.searchField.setText(getString(R.string.empty_string)) }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val pos =
                        (binding.recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    val itemsCount = searchAdapter.itemCount
                    if (pos >= itemsCount - 1) {
                        viewModel.onLastItemReached()
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkFilterParameters()
    }

    private fun renderScreen(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.ServerError -> {
                renderServerError()
            }

            is SearchScreenState.Loading -> {
                renderLoading()
            }

            is SearchScreenState.ShowVacancies -> {
                showVacancies(state.page.vacancies, state.page.found)
            }

            is SearchScreenState.PagingSuccess -> {
                renderPagingSuccess()
            }

            is SearchScreenState.StartScreen -> {
                renderStartScreen()
            }

            is SearchScreenState.EmptyScreen -> {
                renderEmptyScreen()
            }

            is SearchScreenState.NoVacancies -> {
                renderNoVacancies()
            }

            is SearchScreenState.InternetConnError -> {
                renderInternetConnError()
            }

            SearchScreenState.NoActions -> {}
        }
    }

    private fun renderInternetConnError() {
        clearScreen()
        binding.apply {
            errorMessageAndImg.setText(R.string.do_not_have_internet)
            errorMessageAndImg.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                R.drawable.no_internet,
                0,
                0
            )
            errorMessageAndImg.isVisible = true
        }
    }

    private fun renderNoVacancies() {
        clearScreen()
        binding.apply {
            errorMessageAndImg.setText(R.string.could_not_get_job_list)
            errorMessageAndImg.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                R.drawable.no_vacancies,
                0,
                0
            )
            errorMessageAndImg.isVisible = true
            searchResult.setText(R.string.No_such_vacancies)
            searchResult.isVisible = true
        }
    }

    private fun renderEmptyScreen() {
        clearScreen()
    }

    private fun renderStartScreen() {
        clearScreen()
        binding.startScreenImg.isVisible = true
    }

    private fun renderPagingSuccess() {
        clearScreen()
        binding.recyclerView.isVisible = true
        binding.progressBar.isVisible = true
    }

    private fun showVacancies(vacancies: List<Vacancy>, numberVac: Int) {
        clearScreen()
        val stringResult = getMessage(numberVac)
        binding.searchResult.text = stringResult
        binding.searchResult.isVisible = true
        binding.recyclerView.isVisible = true
        searchAdapter.updateItems(vacancies)
    }

    private fun renderLoading() {
        clearScreen()
        binding.progressBar.isVisible = true
    }

    private fun renderServerError() {
        clearScreen()
        binding.apply {
            errorMessageAndImg.setText(R.string.server_error)
            errorMessageAndImg.setCompoundDrawablesWithIntrinsicBounds(
                0,
                R.drawable.server_err,
                0,
                0
            )
            errorMessageAndImg.isVisible = true
        }
    }

    private fun clearScreen() {
        binding.apply {
            progressBar.isVisible = false
            startScreenImg.isVisible = false
            errorMessageAndImg.isVisible = false
            searchResult.isVisible = false
            recyclerView.isVisible = false
        }
    }

    private fun getMessage(num: Int): String {
        val i = num % NUMBER_100
        val i1 = i % NUMBER_10
        val i2 = i / NUMBER_10
        val range = listOf(NUMBER_2, NUMBER_3, NUMBER_4)
        val out = if (i1 == NUMBER_1 && i2 != NUMBER_1) {
            "Найдена $num вакансия"
        } else if (i1 in range && i2 != NUMBER_1) {
            "Найдено $num вакансии"
        } else {
            "Найдено $num вакансий"
        }
        return out
    }

    companion object {
        const val KEY_VACANCY = "KEY_VACANCY"
        const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val NUMBER_1 = 1
        private const val NUMBER_2 = 2
        private const val NUMBER_3 = 3
        private const val NUMBER_4 = 4
        private const val NUMBER_10 = 10
        private const val NUMBER_100 = 100
    }
}
