package ru.practicum.android.diploma.presentation.ui.filter.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterSettingsFragment : Fragment() {

    private val viewModel: FilterSettingsViewModel by viewModel()
    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()

        binding.apply {
            salaryField.addTextChangedListener(
                onTextChanged = { charSequence, _, _, _ ->
                    binding.iconSearchField.isVisible = !charSequence.isNullOrEmpty()
                    viewModel.changeExpectedSalary(charSequence.toString())
                }
            )
            salaryField.setOnFocusChangeListener { _, b ->
                helperTextViewSalary.setTextColor(
                    if (b) {
                        AppCompatResources.getColorStateList(requireContext(), R.color.blue)
                    } else {
                        AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.DarkGraylightGray
                        )
                    }
                )
            }
        }
        viewModel.observeFilter().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.observeApplyButtonLiveData().observe(viewLifecycleOwner) {
            binding.enterFilter.isVisible = it
        }
        viewModel.observeResetButtonLiveData().observe(viewLifecycleOwner) {
            binding.resetFilter.isVisible = it
        }
    }

    private fun render(parameters: FilterParameters) {
        binding.apply {
            textViewArea.text = renderArea(parameters)
            if (!textViewArea.text.isNullOrEmpty()) {
                iconArea.setImageResource(R.drawable.del_search_string_icon_24dp)
                helperTextViewArea.isVisible = true
            }
            if (parameters.industry != null) {
                textViewIndustry.text = parameters.industry!!.name
                iconIndustry.setImageResource(R.drawable.del_search_string_icon_24dp)
                helperTextViewIndustry.isVisible = true
            } else {
                textViewIndustry.text = EMPTY_TEXT
            }
            if (parameters.expectedSalary != null) {
                salaryField.setText(parameters.expectedSalary.toString())
            } else {
                salaryField.setText(EMPTY_TEXT)
            }
            if (parameters.onlyWithSalary != null) {
                checkboxSalary.isChecked = parameters.onlyWithSalary!!
            }
        }
    }

    private fun renderArea(parameters: FilterParameters): String {
        return if (parameters.country == null && parameters.area != null) {
            parameters.area!!.name
        } else if (parameters.country != null && parameters.area == null) {
            parameters.country!!.name
        } else if (parameters.country == null) {
            EMPTY_TEXT
        } else {
            resources.getString(
                R.string.item_filter_area,
                parameters.country!!.name,
                parameters.area!!.name
            )
        }
    }

    private fun setupListeners() {
        setupIconAreaAndIndustryListeners()
        setupButtonListeners()
        binding.apply {
            toolbar.setOnClickListener {
                findNavController().popBackStack()
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
                viewModel.changeOnlyWithSalary()
            }
        }
    }

    private fun setupButtonListeners() {
        binding.apply {
            enterFilter.setOnClickListener {
                viewModel.saveFilterParameters()
            }
            resetFilter.setOnClickListener {
                viewModel.resetFilterParameters()
                helperTextViewArea.visibility = View.GONE
                iconArea.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.arrow_forward
                    )
                )
                helperTextViewIndustry.visibility = View.GONE
                iconIndustry.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.arrow_forward
                    )
                )
            }
        }
    }

    private fun setupIconAreaAndIndustryListeners() {
        binding.apply {
            iconArea.setOnClickListener {
                if (textViewArea.text.isNotEmpty()) {
                    textViewArea.text = EMPTY_TEXT
                    viewModel.clearArea()
                    helperTextViewArea.visibility = View.GONE
                    iconArea.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.arrow_forward
                        )
                    )
                } else {
                    findNavController().navigate(R.id.action_filterSettingsFragment_to_selectWorkplaceFragment)
                }
            }
            iconIndustry.setOnClickListener {
                if (textViewIndustry.text.isNotEmpty()) {
                    textViewIndustry.text = EMPTY_TEXT
                    viewModel.clearIndustry()
                    helperTextViewIndustry.visibility = View.GONE
                    iconIndustry.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.arrow_forward
                        )
                    )
                } else {
                    findNavController().navigate(R.id.action_filterSettingsFragment_to_selectIndustryFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.equalsFilters()
        viewModel.checkEmptyFilter()
    }

    companion object {
        const val EMPTY_TEXT = ""
    }
}
