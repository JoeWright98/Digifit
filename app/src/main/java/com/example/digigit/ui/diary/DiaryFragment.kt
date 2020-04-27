package com.example.digigit.ui.diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
//import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.digigit.BarCodeActivity
import com.example.digigit.Meal
import com.example.digigit.R
import com.example.digigit.RTUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.food_row.*
import kotlinx.android.synthetic.main.food_row.view.*
import kotlinx.android.synthetic.main.fragment_diary.*

class DiaryFragment : Fragment() {

    private lateinit var galleryViewModel: DiaryViewModel
    internal lateinit var pName:String

    internal lateinit var uid:String
    internal lateinit var tvDailyCalorieGoal: TextView
    internal lateinit var tvDailyProteinGoal: TextView
    internal lateinit var tvDailyCarbsGoal: TextView
    internal lateinit var tvDailyFatGoal: TextView
    internal lateinit var tvTest:TextView



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
        tvDailyProteinGoal = root.findViewById(R.id.dailyProteinGoal)
        tvDailyCarbsGoal = root.findViewById(R.id.dailyCarbGoal)
        tvDailyFatGoal = root.findViewById(R.id.dailyFatGoal)
       // tvTest = root.findViewById(R.id.testName)
        //tvDailyCalorieLeft = root.findViewById(R.id.dailyCalorieLeft)
        getUserDailyGoals()
        //getFoodName()
        getUserFoods()

        return root
    }
    private fun getUserDailyGoals(){
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){

                val user = p0.getValue(RTUser::class.java)
                var calorieLeft = user!!.tdee -user.dailyCaloriesConsumed
                var proteinLeft = user.daily_protein_goal -user.daily_protein_consumed
                var carbsLeft = user.daily_carbs_goal -user.daily_carbs_consumed
                var fatLeft = user.daily_fat_goal -user.daily_fat_consumed
                tvDailyCalorieGoal.setText("Daily Calorie Goal:  ${user!!.tdee} - ${user.dailyCaloriesConsumed} =  $calorieLeft")
                tvDailyProteinGoal.setText("Daily Protein Goal:  ${user.daily_protein_goal} - ${user.daily_protein_consumed} = $proteinLeft")
                tvDailyCarbsGoal.setText("Daily Carbs Goal:  ${user.daily_carbs_goal} - ${user.daily_carbs_consumed} = $carbsLeft")
                tvDailyFatGoal.setText("Daily Fat Goal:  ${user.daily_fat_goal} - ${user.daily_fat_consumed} =  $fatLeft")

                //tvDailyCalorieLeft.setText("Daily Calories Left: $calorieLeft")

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    fun getUserFoods(){
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/foodData/$uid/")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){

                val diaryAdapter = GroupAdapter<ViewHolder>()


                p0.children.forEach{
                    val meal = it.getValue(Meal::class.java)
                    if (meal != null) {
                        Log.d("meal",p0.toString())
                        diaryAdapter.add(MealItem(meal))
                    }

                }
                rcFoodList.adapter = diaryAdapter

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })


    }

    fun View.onClick(clickListener: (View) -> Unit) {
        setOnClickListener(clickListener)
    }

    class MealItem(val meal: Meal): Item<ViewHolder>(){
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.foodName.text = meal.mealName + " Calories: " + meal.calories + "kcal" + " Protein:" + meal.protein + "g"  + " Fat: " +
                    meal.fat + "g" + " Carbs: " + meal.carbs  + "g"
        }

        override fun getLayout(): Int {

            return R.layout.food_row
        }
    }







}

