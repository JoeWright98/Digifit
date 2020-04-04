package com.example.digigit.ui.diary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
//import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.digigit.BarCodeActivity
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
import com.example.digigit.R
import com.example.digigit.RTUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_diary.*

class DiaryFragment : Fragment() {

    private lateinit var galleryViewModel: DiaryViewModel

    internal lateinit var uid:String
    internal lateinit var tvDailyCalorieGoal: TextView
    internal lateinit var tvDailyCalorieLeft: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(DiaryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_diary, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(this, Observer {
            textView.text = it
            add_btn.onClick {
                val intent = Intent(getActivity(), BarCodeActivity::class.java)
                startActivity(intent)
            }
        })

        tvDailyCalorieGoal = root.findViewById(R.id.dailyCalorie)
        tvDailyCalorieLeft = root.findViewById(R.id.dailyCalorieLeft)
        getUserDailyCalorieGoal()
        return root
    }
    private fun getUserDailyCalorieGoal(){
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/details")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){

                val user = p0.getValue(RTUser::class.java)
                var calorieLeft = user!!.tdee -user.dailyCaloriesConsumed
                tvDailyCalorieGoal.setText("Daily Calorie Goal:  ${user!!.tdee}")
                tvDailyCalorieLeft.setText("Daily Calories Left: $calorieLeft")

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    fun View.onClick(clickListener: (View) -> Unit) {
        setOnClickListener(clickListener)
    }






}

