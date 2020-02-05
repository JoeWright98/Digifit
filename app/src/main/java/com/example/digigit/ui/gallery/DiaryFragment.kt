package com.example.digigit.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
//import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.digigit.BarCodeActivity
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
import com.example.digigit.R
import kotlinx.android.synthetic.main.fragment_gallery.*

class DiaryFragment : Fragment() {

    private lateinit var galleryViewModel: DiaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(DiaryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(this, Observer {
            textView.text = it
            btn.onClick {
                val intent = Intent(getActivity(), BarCodeActivity::class.java)
                startActivity(intent)
            }
        })


        return root
    }

    fun View.onClick(clickListener: (View) -> Unit) {
        setOnClickListener(clickListener)
    }


}

