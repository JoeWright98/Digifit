package com.example.digigit.model

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class User (
    var name: String? = null,
    var weight: Int? = null,
    var age: String? = null,
    var height: String? = null,
    var daily_protein_consumed:String? = null,
    var daily_fat_consumed:String? = null,
    var daily_carbs_consumed:String? = null,
    var daily_protein_goal:String? = null,
    var daily_fat_goal:String? = null,
    var daily_carbs_goal:String? = null,
    var activity:String? = null,
    var gender:String? = null,
    var ree:Int? = null,
    var tdee:Int? = null



){

    companion object {

        const val FIELD_NAME = "mealName"
        const val FIELD_WEIGHT =  "weight"
        const val FIELD_AGE = "age"
        const val FIELD_DAILY_PROTEIN_CONSUMED = "daily protein consumed"
        const val FIELD_DAILY_FAT_CONSUMED = "daily fat consumed"
        const val FIELD_DAILY_CARBS_CONSUMED = "daily carbs consumed"
        const val FIELD_DAILY_PROTEIN_GOAL = "daily protein goal"
        const val FIELD_DAILY_FAT_GOAL = "daily fat goal"
        const val FIELD_DAILY_CARBS_GOAL = "daily carbs goal"
        const val FIELD_REE = "ree"
        const val FIELD_TDEE = "tdee"



    }



}