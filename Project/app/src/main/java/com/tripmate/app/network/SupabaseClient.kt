package com.tripmate.app.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.gotrue.Auth

val supabase = createSupabaseClient(
    supabaseUrl = "https://tuhqucbnicfghvizgakn.supabase.co",
    supabaseKey = "sb_publishable_MnJKpEPNBiTeUxhYLsmjvA_zPrL2raW"
) {
    install(Postgrest)
    install(Auth)
}
