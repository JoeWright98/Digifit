package com.example.digigit.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.os.Build
import android.os.SystemClock
import me.dm7.barcodescanner.core.CameraUtils
import me.dm7.barcodescanner.zxing.ZXingScannerView
import kotlin.concurrent.thread

fun ZXingScannerView.startCameraForAllDevices(context: Context){
    this.configCameraForAllDevices(context)

    /*
     * Without any parameters defined in startCamera (),
     * say, the idCamera parameter, the camera ID 0
     * will be the one used, ie the rear camera
     * (rear-facing) of the device. The front camera
     * (front-facing) is ID 1.
     * */
    this.startCamera() /* Da API ZXingScannerView */

    /*
     * To know about allocated resources - via
     * isCameraStarted ()
     * */
    this.setTag(this.id, true)
}

/*
 * Method with some possible initial settings
 * of applying the camera interface. Every
 * Settings in the following method are optional and have
 * their default values.
 * */
private fun ZXingScannerView.configCameraForAllDevices(context: Context){
    /*
     * Only works if the camera is not
     * active.
     * */
    //this.setBackgroundColor(Color.TRANSPARENT)

    this.setBorderColor(Color.MAGENTA) /* Edge color */
    this.setLaserColor(Color.CYAN) /* Barcode Alignment Axis Color */
    //this.setMaskColor(Color.BLUE) /* Color of everything else outside the code reading quadrant. */

    /*
     * Without auto focus, the reading power of the
     * Barcode is weak especially on devices that do not have a camera of
     * high-quality.
     * */
    this.setAutoFocus(true)

    this.rotation = 0.0F

    /*
     * To set the codes that can be read - for example
     * Standard all supported codes are subject to
     * be read.
     * */
    //this.setFormats( listOf(BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX) )

    /*
     * Creates a device brand verification code,
     * when used on non-HUAWEI appliances,
     * becauses huaweis decreases the accuracy of code scanning.
     * */
    val brand = Build.MANUFACTURER
    if( brand.equals("HUAWEI", true) ){
        this.setAspectTolerance(0.5F)
    }
}

fun ZXingScannerView.stopCameraForAllDevices(){
    /*
     * stop the camera on onPause () of the activity or fragment.
     * */
    this.stopCamera()
    this.releaseForAllDevices()

    /*
     * To learn about released resources - via
     * isCameraStarted ()

     * */
    this.setTag(this.id, false)
}

private fun ZXingScannerView.releaseForAllDevices(){
    /*
     * The following algorithm is required because if
     * on some devices, the features of
     * camera will not be released and its invocation
     * in an upcoming activity / snippet not
     * will work.
     * */
    val camera = CameraUtils.getCameraInstance()
    if( camera != null ){
        (camera as Camera).release()
    }
}

/*
 * As there is no way to know if the method
 * startCamera () has already been invoked, the method below with
 * View code setTag () support will do it for us.
 * */
fun ZXingScannerView.isCameraStarted(): Boolean{
    val startData = this.getTag(this.id)
    val startStatus = (startData ?: false) as Boolean
    return startStatus
}

/*
 * Since some devices do not have flash light, this checks whether the device does
 * */
fun ZXingScannerView.isFlashSupported(context: Context) =
    context
        .packageManager
        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

/*
 * Flash activation and deactivation can only be
 * occur if this hardware is supported.
 * */
fun ZXingScannerView.enableFlash(
    context: Context,
    status: Boolean) {

    if( this.isFlashSupported(context) ){
        this.flash = status
    }
}

/*
 * For methods where it is safe to invoke them only
 * After camera is working on screen.
 * */
fun ZXingScannerView.threadCallWhenCameraIsWorking(callback: ()->Unit){
    thread {
        while( !this.isShown() ){
            SystemClock.sleep(1000) /*
1 second was the ideal time not to stop camera operation.*/
        }

        callback()
    }
}