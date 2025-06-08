package com.mgm.data.repository

import com.mgm.data.model.Expense
import com.mgm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class ExpenseRepository {
    private val supabase = SupabaseClient.client

    suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            supabase.from("expenses").insert(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpenses(userId: String, month: String): Result<List<Expense>> {
        return try {
            val expenses = supabase.from("expenses")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("userId", userId)
                        like("date", "$month%")
                    }
                }
                .decodeList<Expense>()
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        return try {
            supabase.from("expenses")
                .delete {
                    filter {
                        eq("id", expenseId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}