package com.example.digigit.ui.slideshow

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.digigit.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_slideshow.*

class ProfileFragment : Fragment() {

    private lateinit var slideshowViewModel: ProfileViewModel
    lateinit var alertDialog:AlertDialog
    lateinit var storageReference: StorageReference

    companion object{
        private const val PICK_IMAGE_CODE = 1000
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        slideshowViewModel.text.observe(this, Observer {
            textView.text = it
            //alertDialog = SpotDialog.Builder().setContext(this).build();
            storageReference = FirebaseStorage.getInstance().getReference("image_upload")//.filename
            btn_upload.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_CODE)
            }
        })

        return root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_CODE){
            //alertDialog.show()
            val uploadTask = storageReference!!.putFile(data!!.data!!)
            val task = uploadTask.continueWithTask {
                    task ->
                if (!task.isSuccessful) {
                    Toast.makeText(activity?.applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
                storageReference!!.downloadUrl
            }.addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    val downloadUri = task.result
                    val url = downloadUri!!.toString().substring(0, downloadUri.toString().indexOf("&token"))
                    Log.d("DIRECTLINK",url)
                    //alertDialog.dismiss()
                    Picasso.get().load(url).into(image_view)

                }
            }
        }

    }
}