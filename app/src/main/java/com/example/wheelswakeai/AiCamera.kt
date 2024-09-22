package com.example.wheelswakeai
/*
    Author: Marco De Luca
    Email: mirco.delux@gmail.com
 */

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wheelswakeai.ml.SleepEngine
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import me.ibrahimsn.lib.Speedometer
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask
import kotlin.properties.Delegates


class AiCameraActivity : AppCompatActivity() {
    private var initialSpeed:Float = 0f
    private var speedInitialized:Boolean = false
    private var globalSpeed:Float = 0f
    lateinit var globalAiLabel:String
    private lateinit var labels:List<String>
    private var colors = listOf<Int>(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)
    private val paint = Paint()
    private lateinit var imageProcessor: ImageProcessor
    lateinit var bitmap:Bitmap
    lateinit var imageView: ImageView
    lateinit var cameraDevice: CameraDevice
    lateinit var handler: Handler
    private lateinit var cameraManager: CameraManager
    lateinit var textureView: TextureView
    private lateinit var model:SleepEngine
    private var isSounding = false
    private lateinit var tripMap: MutableMap<String, Any>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aicamera)
        get_permission()
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDate = currentDateTime.format(formatter)
        tripMap = mutableMapOf("startDate" to formattedDate, "sleeping" to false, "yawning" to false, "endDate" to "", "speed" to 0f)
        labels = FileUtil.loadLabels(this, "labels.txt")
        imageProcessor = ImageProcessor.Builder().build() //.add(ResizeWithCropOrPadOp(448, 448))
        model = SleepEngine.newInstance(this)
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        imageView = findViewById(R.id.imageView)
        textureView = findViewById(R.id.textureView)
        initializeSpeedometer()
        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
            if(!isGPSEnabled(this)){
                Toast.makeText(this, "Pls activate GPS", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        textureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                open_camera()
            }
            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {}

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                //Cleanup
                handler.removeCallbacksAndMessages(null)
                cameraDevice.close()
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = textureView.bitmap!!
                val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                imageView.setImageBitmap(mutable)
                imageProcessing()
            }
        }
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

    }

    override fun onDestroy() {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDate = currentDateTime.format(formatter)
        tripMap["endDate"] = formattedDate
        tripMap["speed"] = (globalSpeed+initialSpeed)/2f
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.email!!)
            .collection("trips")
            .document().set(tripMap)
        super.onDestroy()
        model.close()
        stopSound()
    }

    @SuppressLint("MissingPermission")
    fun open_camera(){
        cameraManager.openCamera(cameraManager.cameraIdList[1], object:CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                val surfaceTexture = textureView.surfaceTexture
                val surface = Surface(surfaceTexture)

                val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object: CameraCaptureSession.StateCallback(){
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(), null, null)
                    }
                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                    }
                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {

            }

            override fun onError(p0: CameraDevice, p1: Int) {

            }
        }, handler)
    }

    private fun get_permission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            get_permission()
        }
    }



    fun imageProcessing(){
        bitmap = textureView.bitmap!!
        var image = TensorImage.fromBitmap(bitmap)
        image = imageProcessor.process(image)

        val outputs = model.process(image)
        val locations = outputs.locationAsTensorBuffer.floatArray
        val classes = outputs.categoryAsTensorBuffer.floatArray
        val scores = outputs.scoreAsTensorBuffer.floatArray

        val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutable)

        val h = mutable.height
        val w = mutable.width
        paint.textSize = h/25f
        //paint.strokeWidth = h/85f
        var x = 0
        scores.forEachIndexed { index, fl ->
            x = index
            x *= 4
            if(fl > 0.5){
                paint.color = colors[index]
                val labelRes = labels[classes[index].toInt()]
                this.globalAiLabel=labelRes
                if(labelRes=="yawning"){
                    high = false
                    tripMap["yawning"] = true
                    if (!isSounding){
                        startSound(high)
                    }
                }else if (labelRes=="microsleep" ){
                    high = true
                    tripMap["sleeping"] = true
                    if (!isSounding){
                        startSound(high)
                    }
                }else{
                    stopSound()
                }
                paint.style = Paint.Style.STROKE
                canvas.drawRect(RectF(locations[x+1] *w, locations[x] *h, locations[x+3] *w, locations[x+2] *h), paint)
                paint.style = Paint.Style.FILL

                canvas.drawText(labels[classes[index].toInt()] +" "+fl.toString(),100.85F,h-100.85F, /*locations.get(x+1)*w, locations.get(x)*h,*/ paint)
            }
        }

        imageView.setImageBitmap(mutable)
    }
    fun startSound(high:Boolean){
        val interval = if (high) 500L else 1000L  // Shorter interval for high frequency
        isSounding=true
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
            }
        }, 0, interval)
    }
    fun stopSound(){
        isSounding=false
        timer?.cancel()
        timer = null
    }
    // Observable variable
    private var high: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            stopSound()
            startSound(high)
        }
    }
    private var timer: Timer? = null
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)

    private fun initializeSpeedometer() {
        val speedometer = findViewById<Speedometer>(R.id.speedometer)
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val speed = location.speed * 3.6f // converting from m/s to km/h
                speedometer.setSpeed(speed.toInt(), 1000L)
                if(!speedInitialized){
                    initialSpeed=speed
                }
                globalSpeed=speed
                if (speed >= 130){
                    startSound(true)
                }
                else if(speed<130 && globalAiLabel=="neutral"){
                    try {
                        stopSound()
                    }catch (e:Exception){
                        print(e)
                    }
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
