package com.example.digigit.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.digigit.R

class DiaryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is diary Fragment"
    }
    val text: LiveData<String> = _text

    private var btn: Button? = null

}