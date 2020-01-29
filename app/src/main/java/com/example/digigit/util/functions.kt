package com.example.digigit.util

import android.content.Context
import android.media.RingtoneManager
import android.widget.Toast
import com.example.digigit.R

fun unrecognizedCode(context: Context, callbackClear: ()->Unit = {} ){
    Toast
        .makeText(
            context,
            context.getString(R.string.unrecognized_code),
            Toast.LENGTH_SHORT )
        .show()

    callbackClear()
}

/*
 * To make it possible to beep immediately
 * after reading some bar code.
 * */
fun notification(context: Context){
    try {
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), notification)
        ringtone.play()
    }
    catch(e: Exception) { }
}