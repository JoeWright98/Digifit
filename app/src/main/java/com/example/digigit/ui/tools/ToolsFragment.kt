package com.example.digigit.ui.tools

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.digigit.Achievement
import com.example.digigit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.collection.LLRBNode
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.achieve_row.view.*
import kotlinx.android.synthetic.main.fragment_tools.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class ToolsFragment : Fragment() {

    private lateinit var toolsViewModel: ToolsViewModel
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolsViewModel =
            ViewModelProviders.of(this).get(ToolsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tools, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        toolsViewModel.text.observe(this, Observer {
            textView.text = it
        })


        fetchAchievements()
        return root
    }
    private fun fetchAchievements(){
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/achievements/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){

                    val achievementAdapter = GroupAdapter<ViewHolder>()
                    p0.children.forEach{
                        Log.d("Username", it.toString())
                        val achievement = it.getValue(Achievement::class.java)
                        if (achievement != null) {
                            achievementAdapter.add(AchievementItem(achievement))
                        }
                    }
                    rc_achievements.adapter = achievementAdapter

                }

            override fun onCancelled(p0: DatabaseError) {

            }

        })


//        fetchAchievement1()
//        fetchAchievement2()
//        fetchAchievement3()
    }



    class AchievementItem(val achievement: Achievement): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.achieve_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.achievement_name.text = achievement.achievementName
           // viewHolder.itemView.achievement_points.text = achievement.points.toString()
            if (achievement!!.completed == "no") {
                viewHolder.itemView.iv_completed.setImageResource(R.drawable.ic_clear_black_24dp)
            }else{
                viewHolder.itemView.iv_completed.setImageResource(R.drawable.ic_done_black_24dp)
            }
        }
    }
}