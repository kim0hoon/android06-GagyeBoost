package com.example.gagyeboost.ui

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.nothingEmoji
import com.example.gagyeboost.ui.home.DateItem
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val _selectedCategoryIcon = MutableLiveData("🍚")
    val selectedCategoryIcon: LiveData<String> = _selectedCategoryIcon

    val categoryName = MutableLiveData("")

    private var selectedCategoryId = -1

    private val _income = MutableLiveData<String>()
    val income get() = _income

    private val _expense = MutableLiveData<String>()
    val expense get() = _expense

    private val _result = MutableLiveData<String>()
    val result get() = _result

    val money = MutableLiveData<String>("0")

    private val formatter = DecimalFormat("###,###")

    private val dateFormatter = SimpleDateFormat("yyyy MM dd", Locale.getDefault())
    private val date =
        dateFormatter.format(Date(System.currentTimeMillis())).split(" ").map { it.toInt() }

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    val content = MutableLiveData("")

    fun setSelectedIcon(icon: String) {
        _selectedCategoryIcon.value = icon
    }

    fun addCategory() {
        viewModelScope.launch {
            repository.addCategoryData(
                Category(
                    categoryName = categoryName.value ?: "",
                    emoji = _selectedCategoryIcon.value ?: nothingEmoji
                )
            )
            loadCategoryList()
            selectedCategoryReset()
        }
    }

    fun selectedCategoryReset() {
        categoryName.value = ""
        _selectedCategoryIcon.value = "\uD83C\uDF5A"
        selectedCategoryId = -1
    }

    // 선택한 카테고리를 인자로 UpdateCategory에 표시(카테고리 long click 시 호출)
    fun setCategoryData(category: Category) {
        selectedCategoryId = category.id
        categoryName.value = category.categoryName
        _selectedCategoryIcon.value = category.emoji
    }

    fun updateCategory() {
        viewModelScope.launch {
            repository.updateCategoryData(
                Category(
                    selectedCategoryId,
                    categoryName.value ?: "",
                    selectedCategoryIcon.value ?: nothingEmoji
                )
            )
            loadCategoryList()
            selectedCategoryReset()
        }
    }

    //TODO 데이터 추가 : MoneyType, latitude, longitude, address, content
    fun addAccountBookData() {
        viewModelScope.launch {
            Log.i("TEST","tst:"+content.value.toString())
            repository.addAccountBookData(
                AccountBook(
                    moneyType = 1.toByte(),
                    money = if (money.value != null) money.value!!.toInt() else 0,
                    category = selectedCategoryId,
                    address = "",
                    latitude = 0.0f,
                    longitude = 0.0f,
                    content = content.value ?: "",
                    year = date[0],
                    month = date[1],
                    day = date[2]
                )
            )
            //TODO 달력 데이터 갱신
        }
    }

    fun loadCategoryList() {
        viewModelScope.launch {
            _categoryList.value = repository.loadCategoryList()
            Log.d("TAG", _categoryList.value.toString())
        }
    }

    fun getMonthIncome() {
        viewModelScope.launch {
            repository.getMonthIncome(date[0], date[1])?.let {
                _income.postValue(formatter.format(it) + "원")
            } ?: _income.postValue("0")
        }
    }

    fun getMonthExpense() {
        viewModelScope.launch {
            repository.getMonthExpense(date[0], date[1])?.let {
                _expense.postValue(formatter.format(it) + "원")
            } ?: _expense.postValue("0원")
        }
    }

    fun setTotalMoney() {
        viewModelScope.launch {
            val income = repository.getMonthIncome(date[0], date[1])
            val expense = repository.getMonthExpense(date[0], date[1])

            val result = expense?.let {
                formatter.format(income?.minus(it) ?: 0) + "원"
            } ?: formatter.format(income ?: 0) + "원"

            _result.postValue(result)
        }
    }

    fun getFormattedMoneyText(money: Int) = formatter.format(money) + "원"

    fun afterMoneyTextChanged(e: Editable) {
        if (e.isEmpty()) money.value = "0"
    }
}
