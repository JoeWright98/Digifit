package com.example.digigit

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.digigit.util.*
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_barcode.*
import kotlinx.android.synthetic.main.activity_barcode.ib_flashlight
import kotlinx.android.synthetic.main.activity_barcode.z_xing_scanner
import kotlinx.android.synthetic.main.activity_fullscreen.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class FullBarCodeActivity : AppCompatActivity(),
    ZXingScannerView.ResultHandler {

    val KEY_IS_LOCKED = "is_locked"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Fullscreen mode request algorithm*/
        requestWindowFeature( Window.FEATURE_NO_TITLE )
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN )

        setContentView(R.layout.activity_fullscreen)

        /*
         * The code below is to ensure that ib_lock.tag
         * will always have a Boolean value after onCreate ().
         * */
        /*
        ib_lock.tag = if(ib_lock.tag == null) false else (ib_lock.tag as Boolean)
        if( savedInstanceState != null ){
            ib_lock.tag = savedInstanceState.getBoolean(KEY_IS_LOCKED)
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_LOCKED, ib_lock.tag as Boolean)
    }

    override fun onResume() {
        super.onResume()
        z_xing_scanner.setResultHandler(this)
        startCamera()

        /*
         * To maintain flash light status
         * camera as active / non-active,
         * status from previous activity.
         * */
        ib_flashlight.tag = false /* Guaranteeing an initial value. */
        if( intent != null ){
            ib_flashlight.tag = !intent.getBooleanExtra(Database.KEY_IS_LIGHTENED, false)
            flashLight()
        }

        /*
         * Required to maintain lock status
         * camera on / off when available
         * activity reconstruction.
         * */
        /*
        if(ib_lock.tag as Boolean){
            ib_lock.tag = !(ib_lock.tag as Boolean)
            lockUnlock()
        }*/

        z_xing_scanner.threadCallWhenCameraIsWorking{
            runOnUiThread {
                /*
                 * If the line of code below is not present,
                 * In some versions of Android this activity is also
                 * will be locked in portrait screen due to use
                 * this lock in AndroidManifest.xml, even if the lock
                 * is set for the main activity only.
                 * Another, the invocation of the line below must occur.
                 * after the camera is already operating
                 * on the screen, otherwise there is the possibility of
                 * Camera does not work on some devices.
                 * */
                setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        z_xing_scanner.stopCameraForAllDevices()
    }

    private fun startCamera(){
        if( !z_xing_scanner.isFlashSupported(this) ){
            ib_flashlight.visibility = View.GONE
        }

        z_xing_scanner.startCameraForAllDevices(this)
    }

    /*
     * For the exit activity bin of the
     * problem domain is respected.
     * */
    /*override fun onBackPressed() {
        closeFullscreen()
    }*/


    /* *** Code Bar Interpretation Algorithms*** */
    override fun handleResult( result: Result? ) {
        /*
         * Default protection clause - If the result is
         * null, present a message and finish processing
         * of the handleResult () method.
         * */
        if( result == null ){
            unrecognizedCode(this)
            return
        }

        proccessBarcodeResult( result )
    }

    fun proccessBarcodeResult( result: Result){
        val text = result.text
        val barcodeName = result.barcodeFormat.name

        val i = Intent()
        i.putExtra( Database.KEY_NAME, text )
        i.putExtra( Database.KEY_BARCODE_NAME, barcodeName )
        finish( i, Activity.RESULT_OK )
    }

    /*
     * Always return to the main activity with isLightened
     * being part of the response content.
     * */
    fun finish(intent: Intent, resultAction: Int) {
        intent.putExtra(Database.KEY_IS_LIGHTENED, ib_flashlight.tag as Boolean)
        setResult( resultAction, intent )
        finish()
    }


    /* *** Click Listener Algorithms *** */

    /*
     *To return to non-fullscreen mode before any
     * bar code is interpreted.
     * */
    /*fun closeFullscreen(view: View? = null){
        unlockCamera()
        finish( Intent(), Activity.RESULT_CANCELED )
    }*/

    /*
     * Since the lock is not maintained, the method below is
     * required for user to see unlock
     * occurring, if locking, the camera.
     *
    private fun unlockCamera(){
        ib_lock.tag = true
        lockUnlock()
    }*/

    /*
     * Turns your phone's flash light on and off if
     * available.
     * */
    fun flashLight(view: View? = null){
        ib_flashlight.tag = !(ib_flashlight.tag as Boolean)

        /*
         * The line of code below is required as if there is
         * an activity reconstruction the value obtained for
         * ib_flashlight.tag comes from intent in memory, so the
         * intent must be at the current value.
         * */
        intent.putExtra(Database.KEY_IS_LIGHTENED, ib_flashlight.tag as Boolean)

        if(ib_flashlight.tag as Boolean){
            z_xing_scanner.enableFlash(this, true)
            ib_flashlight.setImageResource(R.drawable.ic_flashlight_white_24dp)
        }
        else{
            z_xing_scanner.enableFlash(this, false)
            ib_flashlight.setImageResource(R.drawable.ic_flashlight_off_white_24dp)
        }
    }

    /*
     * Role responsible for changing health status
     * bar code interpretation algorithm
     * read, including change of presentation icon,
     * to the user of the status of the algorithm for interpreting
     * code.
     * */
    fun lockUnlock(view: View? = null){
        ib_lock.tag = !(ib_lock.tag as Boolean)

        if(ib_lock.tag as Boolean){
            /*
             * To function must be invoked before the
             * stopCameraPreview ().
             * */
            turnOffFlashlight()

            z_xing_scanner.stopCameraPreview()
            ib_lock.setImageResource(R.drawable.ic_lock_white_24dp)
            ib_flashlight.isEnabled = false
        }
        else{
            z_xing_scanner.resumeCameraPreview(this)
            ib_lock.setImageResource(R.drawable.ic_lock_open_white_24dp)
            ib_flashlight.isEnabled = true
        }
    }

    /*
     * Method required as it makes no sense to leave the light
     * flash on when screen is no longer reading
     * codes, is locked. Method invoked when lock
     * screen occurs.
     * */
    private fun turnOffFlashlight(){
        ib_flashlight.tag = true
        flashLight()
    }
}