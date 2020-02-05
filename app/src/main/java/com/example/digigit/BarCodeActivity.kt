package com.example.digigit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import com.example.digigit.util.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_barcode.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


class BarCodeActivity : AppCompatActivity(), ZXingScannerView.ResultHandler,
EasyPermissions.PermissionCallbacks {
    val REQUEST_CODE_CAMERA = 182 /* Random integer */
    val REQUEST_CODE_FULLSCREEN = 184 /* Random integer */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        askCameraPermission()
        lastResultVerification()
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
    override fun handleResult(result: Result?) {
        /*
         * Default protection clause - If the result is
         * null, clears the screen. If there is a last data read,
         * present a message and finish the process
         * of the handleResult () method.
         * */
        if( result == null ){
            unrecognizedCode(this, { clearContent() })
            return
        }

        processBarcodeResult(
            result.text,
            result.barcodeFormat.name)
    }

    private fun processBarcodeResult(
        text: String,
        barcodeFormatName: String ){

        /*
         * The following code is essential for the ringtone
         * not invoked for the same bar code
         * read in a row.
         * */
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
        Database.saveResult(this, result)

        /* Modifying UI. */
        tv_content.text = result.text
        processBarcodeType(true, result.barcodeFormat.name)
        processButtonOpen(result)

        z_xing_scanner.resumeCameraPreview(this)
    }

    private fun processBarcodeType(status: Boolean = false, barcode: String = ""){
        tv_bar_code_type.text = getString(R.string.barcode_format) + barcode
        tv_bar_code_type.visibility = if(status) View.VISIBLE else View.GONE
    }

    /*
     * Verification of content type read in code
     * bar for correct work with the action button.
     * */
    private fun processButtonOpen(result: Result){
        when{
            URLUtil.isValidUrl(result.text) ->
                setButtonOpenAction(resources.getString(R.string.open_url)) {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(result.text)
                    startActivity(i)
                }
            Patterns.EMAIL_ADDRESS.matcher(result.text).matches() ->
                setButtonOpenAction( getString(R.string.open_email) ) {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse("mailto:?body=${result.text}")
                    startActivity(i)
                }
            Patterns.PHONE.matcher(result.text).matches() ->
                setButtonOpenAction( getString(R.string.open_call) ) {
                    val i = Intent(Intent.ACTION_DIAL)
                    i.data = Uri.parse("tel:${result.text}")
                    startActivity(i)
                }
            else -> setButtonOpenAction(status = false)
        }
    }

    /*
     * Method of setting status and content of
     * action trigger button if the content of the
     * Code bar is: email, url or phone.
     * */
    private fun setButtonOpenAction(
        label: String = "",
        status: Boolean = true,
        callbackClick:()->Unit = {} ){

        bt_open.text = label
        bt_open.visibility = if(status) View.VISIBLE else View.GONE
        bt_open.setOnClickListener { callbackClick() }
    }


    /* *** Click Listener Algorithms *** */

    /*
     * Method to clean the user interface.
     * */
    fun clearContent(view: View? = null){
        tv_content.text = getString(R.string.nothing_read)
        processBarcodeType(false)
        setButtonOpenAction(status = false)
        Database.saveResult(this)
    }

    /*
     * Opens activity that allows camera use
     * On all device.
     *
    fun openFullscreen(view: View){
        /*
         * Default protection Clause - No Permission
         * Camera: Does not open activity.
         * */
        if( !EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA) ){
            return
        }

        unlockCamera()

        /*
         * The following line of code makes sure that
         * there is no risk of an exception if
         * the user opens the app and already
         * Fullscreen screen mode is already enabled.
         * */
        val value = if(ib_flashlight.tag == null) false else (ib_flashlight.tag as Boolean)

        val i = Intent(this, FullBarCodeActivity::class.java)
        i.putExtra(Database.KEY_IS_LIGHTENED, value)
        startActivityForResult(i, REQUEST_CODE_FULLSCREEN)
    }
    */

    /*
     * Since the lock is not maintained, the method below is
     * required for user to see unlock
     * occurring if the camera is locked.
     *
    private fun unlockCamera(){
        ib_lock.tag = true
        lockUnlock()
    }

     */

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

    /*
     * Function responsible for changing operating status of the
     * bar code interpretation algorithm
     * read, including change of presentation icon,
     * to the user of the status of the algorithm for interpreting
     * code. Note that the light and flash button should not work
     * if CameraPreview is stopped, stopped.
     *
    fun lockUnlock(view: View? = null){
        /*
         *Using the Button Tag Property to Save the
         * current value of code reading lock, so no
         * we need to work with a new variable
         * instance only to keep this value.
         * */
        val value = if(ib_lock.tag == null)
            true
        else
            !(ib_lock.tag as Boolean)
        ib_lock.tag = value /* Always the inverse of the input value. */

        if( value ){
            /*
             * To function must be invoked before the
             * stopCameraPreview ().
             * */
            turnOffFlashlight()

            /*
             * Stop barcode scanning with
             * the camera
             * */
            z_xing_scanner.stopCameraPreview()
            ib_lock.setImageResource(R.drawable.ic_lock_white_24dp)
            ib_flashlight.isEnabled = false
        }
        else{
            /*
             * Resume barcode scanning with
             * the camera.
             * */
            z_xing_scanner.resumeCameraPreview(this)
            ib_lock.setImageResource(R.drawable.ic_lock_open_white_24dp)
            ib_flashlight.isEnabled = true
        }
    }
    */

    /*
     * Necessary method as it makes no sense to let the light
     * flash on when screen is no longer reading
     * codes, is locked. Method only invoked when
     * screen lock occurs.
     * */
    private fun turnOffFlashlight(){
        ib_flashlight.tag = true
        flashLight()
    }
}