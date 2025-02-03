package ru.practicum.android.diploma.presentation.ui.filter.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterSettingsFragment : Fragment() {

    private val viewModel: FilterSettingsViewModel by viewModels()
    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        viewModel.observeFilter().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(parameters: FilterParameters) {
        binding.apply {
            textViewArea.text = resources.getString(R.string.item_filter_area, parameters.country, parameters.area)
            textViewIndustry.text = parameters.industry
        }
    }

    private fun setupListener() {
        binding.apply {
            toolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            iconArea.setOnClickListener {
                if (textViewArea.text.isNotEmpty()) {
                    textViewArea.text = EMPTY_TEXT
                } else {
                    findNavController().navigate(R.id.action_filterSettingsFragment_to_selectWorkplaceFragment)
                }
            }
            iconIndustry.setOnClickListener {
                if (textViewIndustry.text.isNotEmpty()) {
                    textViewIndustry.text = EMPTY_TEXT
                } else {
                    findNavController().navigate(R.id.action_filterSettingsFragment_to_selectIndustryFragment)
                }
            }
            textViewArea.setOnClickListener {
                findNavController().navigate(R.id.action_filterSettingsFragment_to_selectWorkplaceFragment)
            }
            textViewIndustry.setOnClickListener {
                findNavController().navigate(R.id.action_filterSettingsFragment_to_selectIndustryFragment)
            }
            iconSearchField.setOnClickListener {
                salaryField.text.clear()
            }
            checkboxSalary.setOnClickListener {
                //change noSalary in filterParameters
            }
            enterFilter.setOnClickListener {
                //update filterParameters in sharedPref
            }
            resetFilter.setOnClickListener {
                //clear filterParameters in sharedPref
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EMPTY_TEXT = ""
        fun newInstance() = FilterSettingsFragment()
    }
}
