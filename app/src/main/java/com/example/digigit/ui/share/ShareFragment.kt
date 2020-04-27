package com.example.digigit.ui.share

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.android.synthetic.main.fragment_share.*
import kotlinx.android.synthetic.main.user_row.view.*

class ShareFragment : Fragment() {

    private lateinit var shareViewModel: ShareViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shareViewModel =
            ViewModelProviders.of(this).get(ShareViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_share, container, false)
        val textView: TextView = root.findViewById(R.id.text_share)
        shareViewModel.text.observe(this, Observer {
            textView.text = it
        })
        //rc_leaderboard.layoutManager = linearLayoutManager
        fetchUsers()
        return root
    }
    private fun fetchUsers(){
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users").orderByChild("points")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){

                val userAdapter = GroupAdapter<ViewHolder>()
            p0.children.forEach{
                Log.d("Username", it.toString())
                val rtuser = it.getValue(RTUser::class.java)
                if (rtuser != null) {
                    userAdapter.add(UserItem(rtuser))
                }
            }
            rc_leaderboard.adapter = userAdapter

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }
    class UserItem(val rtuser: RTUser): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.user_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
           viewHolder.itemView.leaderboard_name.text = rtuser.name
            viewHolder.itemView.leaderboard_points.text = rtuser.points.toString()
        }
    }


}

