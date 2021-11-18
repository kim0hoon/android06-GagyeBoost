package com.example.gagyeboost.ui.home.category

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.common.IS_EXPENSE_KEY
import com.example.gagyeboost.databinding.FragmentCategoryBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(R.layout.fragment_category) {
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel by sharedViewModel<AddViewModel>()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        initView()
        initClickListeners()
        setObservers()
    }

    private fun initView() {
        categoryAdapter = CategoryAdapter(
            { category -> categoryOnClick(category) },
            { category -> categoryLongClick(category) })

        binding.viewModel = viewModel
        binding.rvCategory.adapter = categoryAdapter

        arguments?.let {
            if (it.getBoolean(IS_EXPENSE_KEY)) {
                binding.tvMoney.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.income
                    )
                )
                viewModel.setCategoryType(INCOME)
            } else {
                binding.tvMoney.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.expense
                    )
                )
                viewModel.setCategoryType(EXPENSE)
            }
        }

        viewModel.loadCategoryList()
        viewModel.content.value = ""

        binding.etHistory.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etHistory, InputMethodManager.SHOW_IMPLICIT)

    }

    private fun categoryOnClick(category: Category): Boolean {
        if (category.id < 0) {
            navController.navigate(R.id.action_categoryFragment_to_addCategoryFragment)
        } else {
            viewModel.setCategoryData(category)
            navController.navigate(R.id.action_categoryFragment_to_selectPositionFragment)
        }
        return true
    }

    private fun categoryLongClick(category: Category): Boolean {
        viewModel.setCategoryData(category)
        navController.navigate(R.id.action_categoryFragment_to_updateCategoryFragment)
        return true
    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    private fun setObservers() {
        viewModel.categoryList.observe(viewLifecycleOwner) {
            val categoryList = it.toMutableList()

            categoryList.add(Category(-1, getString(R.string.add), "➕", viewModel.categoryType))
            categoryAdapter.submitList(categoryList)
        }
    }

    override fun onResume() {
        super.onResume()

        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    override fun onPause() {
        super.onPause()

        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.etHistory.windowToken, 0)
    }
}
