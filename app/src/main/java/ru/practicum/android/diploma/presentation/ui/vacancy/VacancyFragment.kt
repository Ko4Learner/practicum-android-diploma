package ru.practicum.android.diploma.presentation.ui.vacancy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.Salary
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.presentation.adapter.getCurrencySymbol
import java.text.NumberFormat
import java.util.Locale

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
            handleState(VacancyState.NotFound)
        }

        binding.vacancyToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            handleState(state)
        }
    }

    private fun handleState(state: VacancyState) {
        when (state) {
            is VacancyState.Loading -> showLoading()
            is VacancyState.Content -> showContent(state.vacancy)
            is VacancyState.NotFound -> showNotFound()
            is VacancyState.ServerError -> showServerError()
            is VacancyState.NoInternet -> showNoInternet()
        }
    }

    private fun showLoading() {
        binding.vacancyProgressBar.visibility = View.VISIBLE
        binding.vacancyContent.visibility = View.GONE
    }

    private fun showContent(vacancy: Vacancy) {
        hideError()
        binding.vacancyProgressBar.visibility = View.GONE
        binding.vacancyServerErrorImage.visibility = View.GONE
        binding.vacancyServerErrorText.visibility = View.GONE
        binding.vacancyTitle.text = vacancy.name
        binding.vacancySalary.text = getVacancySalaryText(vacancy.salary)

        Glide.with(this)
            .load(vacancy.logoUrl90)
            .placeholder(R.drawable.image_placeholder)
            .into(binding.vacancyImage)

        binding.vacancyCompanyTitle.text = vacancy.employerName
        binding.vacancyCompanyAddress.text = vacancy.area

        val employmentText = getString(R.string.employment, vacancy.employment)
        binding.vacancyEmploymentText.text = employmentText
        binding.vacancyExperienceText.text = vacancy.experience

        binding.vacancyJobDescriptionText.text = vacancy.description?.let {
            HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }

        binding.vacancyShare.setOnClickListener {
            shareVacancy(vacancy.alternateUrl)
        }
        setupFavoriteButton()
        showKeySkills(vacancy)
        binding.vacancyContent.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.vacancyNotFoundImage.visibility = View.GONE
        binding.vacancyNotFoundText.visibility = View.GONE
        binding.vacancyServerErrorImage.visibility = View.GONE
        binding.vacancyServerErrorText.visibility = View.GONE
        binding.vacancyNoInternetImage.visibility = View.GONE
        binding.vacancyNoInternetText.visibility = View.GONE
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

    private fun showNoInternet() {
        binding.vacancyProgressBar.visibility = View.GONE
        binding.vacancyContent.visibility = View.GONE
        binding.vacancyNoInternetImage.visibility = View.VISIBLE
        binding.vacancyNoInternetText.visibility = View.VISIBLE
    }

    private fun showKeySkills(vacancy: Vacancy) {
        if (vacancy.keySkills.isNullOrEmpty()) {
            binding.vacancyKeySkillsText.visibility = View.GONE
            binding.vacancyKeySkillsTitle.visibility = View.GONE
        } else {
            val textFormated = vacancy.keySkills.joinToString(separator = "\n") { itemKey ->
                "• ${itemKey.replace(",", ",\n")}"
            }
            binding.vacancyKeySkillsText.text = textFormated
            binding.vacancyKeySkillsText.visibility = View.VISIBLE
            binding.vacancyKeySkillsTitle.visibility = View.VISIBLE
        }
    }

    private fun setupFavoriteButton() {
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.vacancyFavorites.setImageResource(
                if (isFavorite) R.drawable.ic_favorites_on else R.drawable.ic_favorites_off
            )
        }

        binding.vacancyFavorites.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun shareVacancy(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_vacancy)))

    }

    private fun getVacancySalaryText(salary: Salary?): String {
        val vacancyText: String
        if (salary == null) {
            vacancyText = getString(R.string.emptySalary)

        } else {
            val currencySymbol = getCurrencySymbol(salary.currency!!)
            val numberFormat = NumberFormat.getInstance(Locale.getDefault())

            if (salary.from == null) {
                vacancyText = getString(
                    R.string.item_vacancy_salary_to,
                    numberFormat.format(salary.to).replace(",", " "),
                    currencySymbol
                )

            } else if (salary.to == null) {
                vacancyText = getString(
                    R.string.item_vacancy_salary_from,
                    numberFormat.format(salary.from).replace(",", " "),
                    currencySymbol
                )

            } else {
                vacancyText = getString(
                    R.string.item_vacancy_salary_from_to,
                    numberFormat.format(salary.from).replace(",", " "),
                    numberFormat.format(salary.to).replace(",", " "),
                    currencySymbol
                )
            }
        }
        return vacancyText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = VacancyFragment()
        private const val KEY_VACANCY = "KEY_VACANCY"
    }
}
