package ru.practicum.android.diploma.presentation.ui.filter.workplace.region

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSelectRegionBinding
import ru.practicum.android.diploma.presentation.adapter.AreaAdapter
import ru.practicum.android.diploma.presentation.ui.search.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import ru.practicum.android.diploma.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
class SelectRegionFragment : Fragment() {

    companion object {
        fun newInstance() = SelectRegionFragment()
    }

    private val viewModel: SelectRegionViewModel by viewModel()
    private var _binding: FragmentSelectRegionBinding? = null
    private val binding get() = _binding!!
    private val searchRegionAdapter = AreaAdapter()
    private var onClickRegion: (String) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchRegion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.clearButton.setImageResource(R.drawable.del_search_string_icon)
                } else {
                    binding.clearButton.setImageResource(R.drawable.search_icon_search_fragment)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.searchDebounce(s.toString())
            }
        })

        onClickRegion = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { region ->
/*            val bundle = bundleOf(KEY_VACANCY to region)
            findNavController().navigate(R.id.action_selectRegionFragment_to_selectWorkplaceFragment, bundle)*/
            Toast.makeText(requireContext(),region,Toast.LENGTH_LONG).show()
        }

        searchRegionAdapter.onItemClick = { region -> onClickRegion(region) }
        binding.apply {
            clearButton.setOnClickListener { searchRegion.setText(getString(R.string.empty_string)) }
            regionRecyclerView.adapter = searchRegionAdapter
            regionRecyclerView.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        viewModel.getScreenState().observe(viewLifecycleOwner) { state ->
            renderScreen(state)
        }
        viewModel.initLoadAreas()
    }

    private fun renderScreen(state: SearchRegionScreenState) {
        when (state) {
            is SearchRegionScreenState.Error -> {
                binding.apply {
                    textPlaceholder.isVisible = true
                    textPlaceholder.text = resources.getString(R.string.could_not_get_list)
                    imagePlaceholder.isVisible = true
                    imagePlaceholder.setImageResource(R.drawable.list_error)
                    regionRecyclerView.isVisible = false
                }
            }

            is SearchRegionScreenState.NoAreas -> {
                binding.apply {
                    textPlaceholder.isVisible = true
                    textPlaceholder.text = resources.getString(R.string.no_such_region)
                    imagePlaceholder.isVisible = true
                    imagePlaceholder.setImageResource(R.drawable.no_vacancies)
                    regionRecyclerView.isVisible = false
                }
            }

            is SearchRegionScreenState.ShowAreas -> {
                binding.apply {
                    textPlaceholder.isVisible = false
                    imagePlaceholder.isVisible = false
                    searchRegionAdapter.updateItems(state.areas)
                    regionRecyclerView.isVisible = true

                }
            }

        }
    }
}

