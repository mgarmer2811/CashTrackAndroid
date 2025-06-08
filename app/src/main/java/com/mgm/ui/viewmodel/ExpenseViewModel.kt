package com.mgm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgm.data.model.Category
import com.mgm.data.model.Expense
import com.mgm.data.repository.AuthRepository
import com.mgm.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExpenseViewModel(
    private val expenseRepository: ExpenseRepository = ExpenseRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _selectedMonth = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
    val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow(getDefaultCategories())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        loadExpenses()
    }

    private fun getDefaultCategories(): List<Category> {
        return listOf(
            Category("VIVIENDA", "🏠", "#FF6B6B"),
            Category("ALIMENTACIÓN", "🍕", "#4ECDC4"),
            Category("TRANSPORTE", "🚗", "#45B7D1"),
            Category("OCIO", "🎬", "#96CEB4"),
            Category("SALUD", "🏥", "#FFEAA7"),
            Category("EDUCACIÓN", "📚", "#DDA0DD"),
            Category("ROPA", "👕", "#98D8C8"),
            Category("OTROS", "💼", "#F7DC6F")
        )
    }

    fun loadExpenses() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch

            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = expenseRepository.getExpenses(userId, _selectedMonth.value)

            if (result.isSuccess) {
                val allExpenses = result.getOrNull() ?: emptyList()
                _expenses.value = if (_selectedCategory.value == "Todas") {
                    allExpenses
                } else {
                    allExpenses.filter { it.category == _selectedCategory.value }
                }
                _uiState.value = _uiState.value.copy(isLoading = false)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun addExpense(amount: Double, category: String, description: String, date: String) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch

            val expense = Expense(
                user_id = userId,
                amount = amount,
                category = category,
            )

            val result = expenseRepository.addExpense(expense)

            if (result.isSuccess) {
                loadExpenses() // Reload expenses after adding
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun setSelectedMonth(month: String) {
        _selectedMonth.value = month
        loadExpenses()
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
        loadExpenses()
    }

    fun getTotalExpenses(): Double {
        return _expenses.value.sumOf { it.amount }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class ExpenseUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)