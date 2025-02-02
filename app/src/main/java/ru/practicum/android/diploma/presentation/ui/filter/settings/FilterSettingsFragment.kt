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

class FilterSettingsFragment : Fragment() {

    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilterSettingsViewModel by viewModels()

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

        navigationListeners()
    }

    private fun navigationListeners() {

        with(binding) {

            toolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            textViewArea.setOnClickListener {
                findNavController().navigate(R.id.action_filterSettingsFragment_to_selectWorkplaceFragment)
            }
            textViewIndustry.setOnClickListener {
                findNavController().navigate(R.id.action_filterSettingsFragment_to_selectIndustryFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FilterSettingsFragment()
    }
}
