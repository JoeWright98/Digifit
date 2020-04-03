package com.example.digigit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.digigit.model.User

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
import kotlin.math.roundToInt


class RegisterActivity: AppCompatActivity() {
    internal lateinit var myEmail:String
    internal lateinit var myPassword:String
    internal lateinit var etEmail: EditText
    internal lateinit var etPassword: EditText
    internal lateinit var etName:EditText
    internal lateinit var etAge:EditText
    internal lateinit var etWeight:EditText
    internal lateinit var etHeight:EditText
    internal lateinit var myName:String
    internal  var myAge:Int = 0
    internal  var myHeight:Int = 0
    internal  var myWeight:Int = 0
    internal lateinit var register: Button
    internal lateinit var myAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    internal lateinit var rgActivity:RadioGroup
    internal lateinit var rgGender:RadioGroup
    internal lateinit var rgGoal:RadioGroup
    internal lateinit var myGoal:String
    internal lateinit var myActivity:String
    internal lateinit var myGender:String
    internal  var myDailyProteinGoal:Int = 0
    internal  var myDailyCarbsGoal:Int = 0
    internal  var myDailyFatGoal:Int = 0
    internal  var myDailyFatConsumed:Int = 0
    internal  var myDailyProteinConsumed:Int = 0
    internal  var myDailyCarbsConsumed:Int = 0
    internal var myREE: Double = 0.0
    internal var myTDEE: Int = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        register = findViewById(R.id.regButton)
        etEmail = findViewById(R.id.myEmail)
        etPassword = findViewById(R.id.myPassword)
        etName = findViewById(R.id.name)
        etWeight = findViewById(R.id.weight)
        etAge = findViewById(R.id.age)
        etHeight = findViewById(R.id.height)
        rgActivity = findViewById(R.id.activity)
        rgGender = findViewById(R.id.gender)
        rgGoal = findViewById(R.id.goalweight)
        myAuth = FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()

        register.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {

                myName = etName.getText().toString().trim()
                myWeight = etWeight.getText().toString().toInt()
                myAge = etAge.getText().toString().toInt()
                myHeight = etHeight.getText().toString().toInt()
                myEmail = etEmail.getText().toString().trim()
                myPassword = etPassword.getText().toString().trim()
                var activityId: Int = rgActivity.checkedRadioButtonId
                if (activityId!=-1){ // If any radio button checked from radio group
                    // Get the instance of radio button using id
                    val radio:RadioButton = findViewById(activityId)
                    myActivity = radio.text.toString()
                }
                var genderId: Int = rgGender.checkedRadioButtonId
                if (genderId!=-1){ // If any radio button checked from radio group
                    // Get the instance of radio button using id
                    val radio:RadioButton = findViewById(genderId)
                    myGender = radio.text.toString()
                }
                if (myGender == "Male"){
                    myREE = (10 * myWeight) + (6.25 * myHeight ) - (5 * myAge) + 5
                }else{
                    myREE = (10 * myWeight) + (6.25 * myHeight ) - (5 * myAge) - 161
                }
                if (myActivity == "Light activity"){
                    myTDEE = (myREE * 1.375).roundToInt()
                }else if(myActivity == "Moderate activity"){
                    myTDEE = (myREE * 1.55).roundToInt()
                }else{
                    myTDEE = (myREE * 1.725 ).roundToInt()
                }
                var goalId: Int = rgGoal.checkedRadioButtonId
                if (goalId!=-1){ // If any radio button checked from radio group
                    // Get the instance of radio button using id
                    val radio:RadioButton = findViewById(goalId)
                    myGoal = radio.text.toString()
                }
                if (myGoal == "Gain Weight"){
                    myTDEE = myTDEE + (myTDEE * 0.2).roundToInt()
                }else{
                    myTDEE = myTDEE - (myTDEE * 0.2).roundToInt()
                }
                myDailyProteinGoal = (0.8 * myWeight).roundToInt()
                myDailyFatGoal = (myTDEE / 4) / 4
                myDailyCarbsGoal = ((myTDEE - (myDailyProteinGoal * 4)) - (myDailyFatGoal * 9) ) / 4



                CreateUser()


            }
        })

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)

        // Firestore
        firestore = Firebase.firestore
    }

     fun addUser() {
         val uid = FirebaseAuth.getInstance().uid?:""
         val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
         val rtuser = RTUser(uid,myName,myWeight,myAge,myHeight, myDailyProteinConsumed,myDailyFatConsumed, myDailyCarbsConsumed,
             myDailyProteinGoal, myDailyFatGoal, myDailyCarbsGoal, myActivity,myGender, myTDEE)
         ref.setValue(rtuser)



    }

    private fun CreateUser() {
        myAuth!!.createUserWithEmailAndPassword(myEmail, myPassword).addOnCompleteListener{


                if (it.isSuccessful)
                {
                    addUser()

                    //firestore.collection("Users").add(user)
                    val homeIntent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(homeIntent)
                    finish()
                }
                else
                {
                    Toast.makeText(this@RegisterActivity, "Error is " + it.getException(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

class RTUser(var uid:String, var name: String,var weight: Int, var age: Int, var height: Int,
             var daily_protein_consumed:Int, var daily_fat_consumed:Int, var daily_carbs_consumed:Int,
             var daily_protein_goal:Int, var daily_fat_goal:Int, var daily_carbs_goal:Int,
             var activity:String, var gender:String,  var tdee:Int){
    constructor():this("","",0,0,0,0,0,0,0,
        0,0,"","",0)
}