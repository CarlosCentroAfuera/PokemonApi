package com.carlostena.pokemonapi

import com.google.gson.Gson

data class Pokemon(var nombre: String, var nivel: Int) {

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}