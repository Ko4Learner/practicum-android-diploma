package ru.practicum.android.diploma.presentation.ui.filter.industry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSelectIndustryBinding
import ru.practicum.android.diploma.presentation.adapter.IndustryAdapter

class SelectIndustryFragment : Fragment() {
    private var _binding: FragmentSelectIndustryBinding? = null
    private val binding: FragmentSelectIndustryBinding get() = _binding!!
    private val viewModel: SelectIndustryViewModel by viewModel()
    private val adapter = IndustryAdapter { oldPosition, isSame ->
        binding.chooseIndustry.isVisible = isSame
        if (oldPosition != null) {
            binding.recyclerView.adapter?.notifyItemChanged(oldPosition)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectIndustryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getIndustries()

        viewModel.getIndustryStateLiveData().observe(viewLifecycleOwner) {
            show(it)
        }

        binding.chooseIndustry.setOnClickListener {
            viewModel.chooseIndustry(
                (binding.recyclerView.adapter as IndustryAdapter).getSelectedItemId()
            )
            findNavController().navigateUp()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.iconSearchField.setOnClickListener { binding.searchField.setText(getString(R.string.empty_string)) }

        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    binding.iconSearchField.setImageResource(R.drawable.del_search_string_icon)
                } else {
                    binding.iconSearchField.setImageResource(R.drawable.search_icon_search_fragment)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.searchDebounce(s.toString())
            }
        })
    }

    private fun show(state: IndustryContentState) {
        hideAll()
        when (state) {
            is IndustryContentState.Content -> {
                adapter.industries = state.data
                binding.recyclerView.adapter = adapter
            }

            is IndustryContentState.Error -> {
                binding.errorMessagePlaceholder.visibility = View.VISIBLE
            }

            is IndustryContentState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun hideAll() {
        binding.progressBar.visibility = View.GONE
        binding.errorMessagePlaceholder.visibility = View.GONE
        binding.chooseIndustry.visibility = View.GONE
        if (binding.recyclerView.adapter != null) {
            (binding.recyclerView.adapter as IndustryAdapter).clear()
            binding.recyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
