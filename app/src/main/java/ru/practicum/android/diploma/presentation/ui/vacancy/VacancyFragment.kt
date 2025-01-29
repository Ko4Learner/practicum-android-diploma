package ru.practicum.android.diploma.presentation.ui.vacancy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.Vacancy

class VacancyFragment : Fragment() {
    private var vacancyId: String? = null
    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VacancyViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vacancyId = it.getString(KEY_VACANCY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacancyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vacancyId = arguments?.getString(KEY_VACANCY)
        if (vacancyId != null) {
            viewModel.prepareVacancy(vacancyId)
        } else {
            handleStatus(VacancyStatus.NOT_FOUND)
        }

        binding.vacancyToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.status.observe(viewLifecycleOwner) { status ->
            handleStatus(status)
        }

        viewModel.vacancy.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showContent(resource.data)
                }

                is Resource.Error -> {
                    handleStatus(
                        if (resource.message?.contains("404") == true) {
                            VacancyStatus.NOT_FOUND
                        } else {
                            VacancyStatus.SERVER_ERROR
                        }
                    )
                }
            }
        }
    }

    private fun handleStatus(status: VacancyStatus) {
        when (status) {
            VacancyStatus.LOADING -> showLoading()
            VacancyStatus.SUCCESS -> {}
            VacancyStatus.NOT_FOUND -> showNotFound()
            VacancyStatus.SERVER_ERROR -> showServerError()
        }
    }

    private fun showLoading() {
        binding.vacancyProgressBar.visibility = View.VISIBLE
        binding.vacancyContent.visibility = View.VISIBLE
    }

    private fun showContent(vacancy: Vacancy) {
        binding.vacancyProgressBar.visibility = View.GONE
        binding.vacancyServerErrorImage.visibility = View.GONE
        binding.vacancyServerErrorText.visibility = View.GONE

        binding.vacancyContent.visibility = View.VISIBLE

        binding.vacancyTitle.text = vacancy.name

        /*Glide.with(this)
            .load(vacancy.employer.logoUrls?.url90)
            .transform(RoundedCorners(R.dimen.radius_10))
            .placeholder(R.drawable.image_placeholder)
            .into(binding.vacancyImage)*/

        binding.vacancyEmploymentText.text = vacancy.employment
        binding.vacancyCompanyAddress.text = vacancy.area
        binding.vacancySalary.text = vacancy.salary?.let {
            "${it.from ?: "N/A"} - ${it.to ?: "N/A"} ${it.currency}"
        } ?: "Зарплата не указана"
        showKeySkills(vacancy)
    }

    private fun showNotFound() {
        binding.vacancyProgressBar.visibility = View.GONE
        binding.vacancyContent.visibility = View.GONE
        binding.vacancyNotFoundImage.visibility = View.VISIBLE
        binding.vacancyNotFoundText.visibility = View.VISIBLE
    }

    private fun showServerError() {
        binding.vacancyProgressBar.visibility = View.GONE
        binding.vacancyContent.visibility = View.GONE
        binding.vacancyServerErrorImage.visibility = View.VISIBLE
        binding.vacancyServerErrorText.visibility = View.VISIBLE
    }

    private fun showKeySkills(vacancy: Vacancy) {
        if (vacancy.name.isEmpty()) {
            binding.vacancyKeySkillsText.visibility = View.GONE
            binding.vacancyKeySkillsTitle.visibility = View.GONE
        } else {
            /*val textFormated = vacancy.keySkills.joinToString(separator = "\n") { itemKey ->
                "• ${itemKey?.name?.replace(",", ",\n")}"
            }
            binding.vacancyKeySkillsText.text = textFormated*/
            binding.vacancyKeySkillsText.visibility = View.VISIBLE
            binding.vacancyKeySkillsTitle.visibility = View.VISIBLE
        }
    }

    companion object {
        fun newInstance() = VacancyFragment()
        private const val KEY_VACANCY = "KEY_VACANCY"
    }
}
