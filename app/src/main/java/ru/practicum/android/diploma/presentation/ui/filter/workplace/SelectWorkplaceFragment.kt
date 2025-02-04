package ru.practicum.android.diploma.presentation.ui.filter.workplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSelectWorkplaceBinding
import ru.practicum.android.diploma.presentation.ui.filter.settings.FilterSettingsFragment.Companion.EMPTY_TEXT

class SelectWorkplaceFragment : Fragment() {
    private var _binding: FragmentSelectWorkplaceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SelectWorkplaceViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectWorkplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()

        binding.ToolbarWorkspace.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupUI() {
        val filterParameters = viewModel.getFilterParameters()

        if (filterParameters.country!=null) {
            binding.textViewCountry.text = filterParameters.country!!.name
            binding.iconCountry.setImageResource(R.drawable.del_search_string_icon_24dp)
        } else {
            binding.iconCountry.setImageResource(R.drawable.arrow_forward)
            binding.textViewCountry.setOnClickListener {
                findNavController().navigate(R.id.action_selectWorkplaceFragment_to_selectCountryFragment)
            }
        }

        if (filterParameters.area!=null) {
            binding.textViewRegion.text = filterParameters.area!!.name
            binding.iconRegion.setImageResource(R.drawable.del_search_string_icon_24dp)
        } else {
            binding.iconRegion.setImageResource(R.drawable.arrow_forward)
            binding.textViewRegion.setOnClickListener {
                findNavController().navigate(R.id.action_selectWorkplaceFragment_to_selectRegionFragment)
            }
        }

        buttonProcessing()

        binding.textViewCountry.setOnClickListener {
            findNavController().navigate(R.id.action_selectWorkplaceFragment_to_selectCountryFragment)
        }

        binding.textViewRegion.setOnClickListener {
            findNavController().navigate(R.id.action_selectWorkplaceFragment_to_selectRegionFragment)
        }

        updateSaveButtonVisibility()

        binding.saveButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun buttonProcessing() {
        binding.iconCountry.setOnClickListener {
            if (binding.textViewCountry.text.isNotEmpty()) {
                binding.textViewCountry.text = EMPTY_TEXT
                binding.iconCountry.setImageResource(R.drawable.arrow_forward)
                updateSaveButtonVisibility()
            } else {
                findNavController().navigate(R.id.action_selectWorkplaceFragment_to_selectCountryFragment)
            }
        }

        binding.iconRegion.setOnClickListener {
            if (binding.textViewRegion.text.isNotEmpty()) {
                binding.textViewRegion.text = EMPTY_TEXT
            } else {
                findNavController().navigate(R.id.action_selectWorkplaceFragment_to_selectRegionFragment)
            }
        }
    }

    private fun updateSaveButtonVisibility() {
        binding.saveButton.visibility = if (
            !binding.textViewCountry.text.isNullOrEmpty() ||
            !binding.textViewRegion.text.isNullOrEmpty()
        ) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
