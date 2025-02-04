package ru.practicum.android.diploma.presentation.ui.filter.workplace.country

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.practicum.android.diploma.databinding.FragmentSelectCountryBinding
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.presentation.adapter.AreaAdapter
import ru.practicum.android.diploma.presentation.ui.favourites.FavouritesFragment.Companion.CLICK_DEBOUNCE_DELAY
import ru.practicum.android.diploma.util.debounce

class SelectCountryFragment : Fragment() {

    private val viewModel: SelectCountryViewModel by viewModel()
    private var _binding: FragmentSelectCountryBinding? = null
    private val binding get() = _binding!!
    private var adapter = AreaAdapter()
    private var onClickCountry: (String) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickCountry = debounce<String>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { country ->
            viewModel.saveCountry(country)
            findNavController().navigateUp()
        }

        adapter.onItemClick = { country -> onClickCountry(country) }

        setupRecyclerView()

        viewModel.getCountry()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.ToolbarCountry.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.CountryRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SelectCountryFragment.adapter
        }
    }

    private fun render(state: CountryState) {
        when (state) {
            is CountryState.Error -> {
                showError(state.errorMessage)
            }

            is CountryState.Loading -> {
                showLoad()
            }

            is CountryState.Success -> {
                showContent(state.countries)
            }
        }
    }

    private fun showContent(countries: List<Area>) {
        binding.progressBar.isVisible = false
        binding.textPlaceholder.isVisible = false
        binding.imagePlaceholder.isVisible = false
        binding.CountryRecyclerView.isVisible = true
        adapter.updateItems(countries)
    }

    private fun showLoad() {
        binding.CountryRecyclerView.isVisible = false
        binding.textPlaceholder.isVisible = false
        binding.imagePlaceholder.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showError(errorMessage: String) {
        binding.CountryRecyclerView.isVisible = false
        binding.textPlaceholder.text = errorMessage
        binding.textPlaceholder.isVisible = true
        binding.imagePlaceholder.isVisible = true
        binding.progressBar.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SelectCountryFragment()
    }
}
