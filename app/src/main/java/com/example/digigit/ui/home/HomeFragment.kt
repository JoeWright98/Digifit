package com.example.digigit.ui.home

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.digigit.LoginScreenActivity
import com.example.digigit.R
import com.example.digigit.RTUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    internal lateinit var tvWelcome: TextView
    internal lateinit var myAuth:FirebaseAuth
    internal lateinit var uid:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        myAuth = FirebaseAuth.getInstance()


        tvWelcome = root.findViewById(R.id.welcome)

        getUserName()

        return root

    }
    private fun getUserName(){
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/details")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){

                val user = p0.getValue(RTUser::class.java)
                if (myAuth.currentUser != null){
                    val usersName = user!!.name ?:""

                    tvWelcome.text = "Welcome $usersName"

                }else {
                    tvWelcome.setText("")
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }
}