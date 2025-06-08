package com.mgm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val id: String? = null,
    val user_id: String,
    val amount: Double,
    val category: String,
    val created_at: String? = null
)