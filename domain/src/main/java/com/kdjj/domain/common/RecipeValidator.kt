package com.kdjj.domain.common

import javax.inject.Inject

internal class RecipeValidator @Inject constructor() {

    fun validateTitle(title: String): Boolean {
        return title.isNotBlank()
    }

    fun validateStuff(stuff: String): Boolean {
        return stuff.isNotBlank()
    }
}