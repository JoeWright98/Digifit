package com.example.digigit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.digigit.ui.diary.DiaryFragment

import com.example.digigit.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_barcode.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

import com.google.gson.GsonBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import java.io.IOException
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class BarCodeActivity : AppCompatActivity(), ZXingScannerView.ResultHandler,
EasyPermissions.PermissionCallbacks {
    val REQUEST_CODE_CAMERA = 182 /* Random integer */
    val REQUEST_CODE_FULLSCREEN = 184 /* Random integer */
    internal lateinit var pName:String
    internal var pProtein:Float = 0F
    internal var pFat:Float = 0F
    internal var pCarbs:Float = 0F
    internal var pEnergy:Float = 0F

    private var textView: TextView? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)



        askCameraPermission()
        //lastResultVerification()
        add.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                addMeal()
                finish()


            }
        })



    }

    private fun addMeal(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/details")
        val mref = FirebaseDatabase.getInstance().getReference("/users/$uid/foodData/$pName")

        val meal = Meal(pName,pProtein, pFat, pCarbs, pEnergy)
        mref.setValue(meal)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot){

                val user = p0.getValue(RTUser::class.java)
                user!!.dailyCaloriesConsumed = user!!.dailyCaloriesConsumed + pEnergy.toInt()
                ref.setValue(user)





            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }
    class Meal(val name:String, val protein:Float, val fat:Float, val carbs:Float, val calories:Float) {
        constructor() : this("", 0F, 0F, 0F, 0F)
    }

    /*
     * If we have any read results
     * saved in SharedPreferences, we use
     * the last result that was on the screen
     * */
    private fun lastResultVerification(){
        val result = Database.getSavedResult(this)

        if( result != null ){


            processBarcodeResult( result.text, result.barcodeFormat.name )
        }
    }

    override fun onResume() {
        super.onResume()
        /*
         * Registering the current activity so it can
         * show the scan results.
         * */
        z_xing_scanner.setResultHandler(this)

        restartCameraIfInactive()
    }

    /*
     * Required method for the camera to restart,
     * if the camera is inactive.
     * */
    private fun restartCameraIfInactive(){
        if( !z_xing_scanner.isCameraStarted()
            && EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA) ){
            startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        z_xing_scanner.stopCameraForAllDevices()
    }

    /*
     * Inherited method used to make it possible to
     * interpret any value coming from the activity and convert it
     * Fullscreen cam.
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if( requestCode == REQUEST_CODE_FULLSCREEN ){
            /*
             * Ensures that the flash light button and the
             * status controlled by it, continue with the
             * correct values.
             * */
            ib_flashlight.tag = !data!!.getBooleanExtra(Database.KEY_IS_LIGHTENED, false)
            flashLight()

            if( resultCode == Activity.RESULT_OK ){


               processBarcodeResult(
                    data.getStringExtra(Database.KEY_NAME),
                    data.getStringExtra(Database.KEY_BARCODE_NAME) )



            }
        }
    }


    /* *** Permission Request Algorithms *** */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        /* Forwarding Results to EasyPermissions */
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this )
    }

    override fun onPermissionsDenied(
        requestCode: Int,
        perms: MutableList<String>) {

        askCameraPermission()
    }

    private fun askCameraPermission(){
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(this, REQUEST_CODE_CAMERA, Manifest.permission.CAMERA)
                .setRationale( getString(R.string.request_permission_description) )
                .setPositiveButtonText( getString(R.string.request_permission_button_ok) )
                .setNegativeButtonText( getString(R.string.request_permission_button_cancel) )
                .build() )
    }

    override fun onPermissionsGranted(
        requestCode: Int,
        perms: MutableList<String>) {

        startCamera()
    }

    private fun startCamera(){
        if( !z_xing_scanner.isFlashSupported(this) ){
            ib_flashlight.visibility = View.GONE
        }

        z_xing_scanner.startCameraForAllDevices(this)
    }


    /* *** Barcode Interpretation Algorithms *** */
    override fun handleResult(result: Result) {
        /*
         * Default protection clause - If the result is
         * null, clears the screen. If there is a last data read,
         * present a message and finish the process
         * of the handleResult () method.
         * */
        if (result == null) {
            unrecognizedCode(this, { clearContent() })
            return
        }

        processBarcodeResult(
            result.text,
            result.barcodeFormat.name

        )

    }

    private fun parseJson(result: Result){
        //retrieving json stuff

        val barcode = result.text

        val url = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"
        val request = okhttp3.Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback{
            override fun onResponse(call: Call, response: okhttp3.Response) {
                val body = response?.body?.string()
                println(body)

                val gson = GsonBuilder().create()

                val entity = gson.fromJson(body, Entity::class.java)

                if(entity.product.product_name == null || entity.product.nutriments.carbohydrates_value == null || entity.product.nutriments.fat_value == null
                    || entity.product.nutriments.proteins_value == null  || entity.product.nutriments.carbohydrates_value == null){
                    unrecognizedCode(applicationContext, { clearContent() })
                    return
                }else {

                    runOnUiThread {
                        product_name_tv.setText("")
                        product_name_tv.append(entity.product.product_name )
                        pName = entity.product.product_name
                        product_protein_tv.setText("")
                        product_protein_tv.append("Protein value: " + entity.product.nutriments.proteins_value )
                        pProtein = entity.product.nutriments.proteins_value
                        product_carbs_tv.setText("")
                        product_carbs_tv.append("Carbohydrate value: " + entity.product.nutriments.carbohydrates_value)
                        pCarbs = entity.product.nutriments.carbohydrates_value
                        product_fat_tv.setText("")
                        product_fat_tv.append("Fat value: " + entity.product.nutriments.fat_value )
                        pFat = entity.product.nutriments.fat_value
                        product_calories_tv.setText("")
                        product_calories_tv.append("Calories: " + entity.product.nutriments.energy_value )
                        pEnergy = entity.product.nutriments.energy_value





                    }
                }




            }
            override fun onFailure(call: Call, e: IOException) {
                handleResult(result)
                println("Failed")
            }

        })



    }
    //class ProductList(val products: List<Product>)
    //class ProductCode(val code: String)
    class Entity(val product: Product)
    class Product( val product_name: String, val nutriments: Nutrient)
    class Nutrient(val proteins_value: Float, val fat_value: Float, val carbohydrates_value: Float, val energy_value: Float)


    private fun processBarcodeResult(
        text: String,
        barcodeFormatName: String ){



        /*
         * The following code is essential for the ringtone
         * not invoked for the same bar code
         * read in a row.
         *

         */
        val resultSaved = Database.getSavedResult(this)
        if( resultSaved == null || !resultSaved.text.equals(text, true) ){
            notification(this)
        }

        val result = Result(
            text,
            text.toByteArray(), /* Just to have something */
            arrayOf(), /* Just to have something*/
            BarcodeFormat.valueOf(barcodeFormatName))







        /* Saving the last read result.*/
        //Database.saveResult(this, result)



        /* Modifying UI. */
        tv_content.text = result.text
        parseJson(result)
        processBarcodeType(true, result.barcodeFormat.name)
        //processButtonOpen(result)







        z_xing_scanner.resumeCameraPreview(this)
    }

    private fun processBarcodeType(status: Boolean = false, barcode: String = ""){
        tv_bar_code_type.text = getString(R.string.barcode_format) + barcode
        tv_bar_code_type.visibility = if(status) View.VISIBLE else View.GONE
    }






    /* *** Click Listener Algorithms *** */

    /*
     * Method to clean the user interface.
     * */
    fun clearContent(view: View? = null){
        tv_content.text = getString(R.string.nothing_read)
        processBarcodeType(false)
        Database.saveResult(this)
    }


    /*
     * Turns on or off the flash light on your phone if it is
     * Available on device.
     * */
    fun flashLight(view: View? = null){
        /*
         * Using the Button property tag to save the
         * Current value of flash light status.
         * */
        val value = if(ib_flashlight.tag == null)
            true
        else
            !(ib_flashlight.tag as Boolean)
        ib_flashlight.tag = value /* Always the inverse of the input value. */

        if(value){
            z_xing_scanner.enableFlash(this, true)
            ib_flashlight.setImageResource(R.drawable.ic_flashlight_white_24dp)
        }
        else{
            z_xing_scanner.enableFlash(this, false)
            ib_flashlight.setImageResource(R.drawable.ic_flashlight_off_white_24dp)
        }
    }


    private fun turnOffFlashlight(){
        ib_flashlight.tag = true
        flashLight()
    }

}