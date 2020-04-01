package com.example.digigit.model

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class User (
    var name: String? = null,
    var weight: String? = null,
    var age: String? = null,
    var height: String? = null

){

    companion object {

        const val FIELD_NAME = "name"
        const val FIELD_WEIGHT =  "weight"
        const val FIELD_AGE = "age"
        const val FIELD_HEIGHT = "height"

    }



}