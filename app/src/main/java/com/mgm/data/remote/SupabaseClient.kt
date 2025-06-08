package com.mgm.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://ajfrhoegesuwjrjvsdiz.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFqZnJob2VnZXN1d2pyanZzZGl6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDgzNDE4MzksImV4cCI6MjA2MzkxNzgzOX0.4HahLmY0-iGpHjHcTeS5Xm0Zx4xrv_1jKr_Fw_MqRVA"
    ) {
        install(Postgrest)
        install(Auth) {
        }
    }
}