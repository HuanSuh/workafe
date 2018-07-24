package com.hude.workafe.view.component.map

import android.Manifest
import android.app.FragmentManager
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.hude.workafe.R
import com.hude.workafe.utils.Config
import kotlinx.android.synthetic.main.custom_map.view.*
import java.util.*
import com.google.android.gms.maps.model.CameraPosition
import com.hude.workafe.utils.Constants


/**
 * Created by huansuh on 2018. 7. 14..
 */
class VuiGoogleMap: RelativeLayout, OnMapReadyCallback, View.OnClickListener {

    private lateinit var map: GoogleMap
    private lateinit var activity: AppCompatActivity
    private var myLocationEnabled = false

    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.custom_map, this)
        map_btn_compass.setOnClickListener(this)
        map_btn_location.setOnClickListener(this)
    }

    public fun isMyLocationEnabled() : Boolean {
        return myLocationEnabled
    }

    public fun initMap(fragmentManager: FragmentManager?): Boolean {
        var initialized = false
        fragmentManager?.let {
            val mapFragment = it.findFragmentById(R.id.map) as MapFragment?
            if(mapFragment != null) {
                mapFragment.getMapAsync(this)
                initialized = true
            }
        }
        return initialized
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.map_btn_compass -> {
                refreshBearing().let {
                    vuiMapControlClickedListener?.compassButtonClicked(it)
                }
            }
            R.id.map_btn_location -> {
                myLocationEnabled = !myLocationEnabled
                vuiMapControlClickedListener?.locationButtonClicked(myLocationEnabled)
                if(myLocationEnabled) {
                    if(checkPermissionForLocation()) {
                        getMyLocation()
                    }
                    map_btn_location.setColorFilter(context.resources.getColor(R.color.colorActivate))
                } else {
                    map.isMyLocationEnabled = false
                    map_btn_location.setColorFilter(context.resources.getColor(R.color.colorDeactivate))
                }
            }
        }
    }

    fun setActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    private fun checkPermissionForLocation(): Boolean {
        val permissionList = listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val requestPermission = ArrayList<String>()
        for (permission in permissionList) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermission.add(permission)
            }
        }
        if(permissionList.size == requestPermission.size) {
            requestPermissions(activity, requestPermission.toTypedArray(), Constants.REQUEST_PERMISSIONS_LOCATION)
            return false
        }
        return true
    }


    override fun onMapReady(map: GoogleMap?) {
        this.map = map!!
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isCompassEnabled = false
        onMapReadyCallback?.onMapReady(map)
    }
    private var onMapReadyCallback: OnMapReadyCallback? = null
    public fun setOnMapCallback(callback: OnMapReadyCallback) {
        this.onMapReadyCallback = callback
    }

    private fun refreshBearing() : CameraPosition {
        if(map.cameraPosition.bearing == 0f) return map.cameraPosition
        val cameraPos = CameraPosition
                .builder(map.cameraPosition)
                .bearing(0f)
                .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), Config.ANIMATE_CAMERA_DELAY, null)
        return cameraPos
    }

    public fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true

            val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val locationResult = mFusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    val latLng = LatLng(location.latitude, location.longitude)
                    onLocationResultListener?.onLocationResultSuccess(latLng)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM), Config.ANIMATE_CAMERA_DELAY, null)
                } else {
                    onLocationResultListener?.onLocationResultFail(context)
                }
            }
        }
    }
    var onLocationResultListener: OnLocationResultListener? = null
    public interface OnLocationResultListener {
        fun onLocationResultSuccess(latLng: LatLng)
        fun onLocationResultFail(context: Context)
    }

    public fun setCompassButtonVisible(visible: Boolean) {
        map_btn_compass.visibility = if(visible) View.VISIBLE else View.GONE
    }

    private var vuiMapControlClickedListener : VuiMapControlClickedListener? = null
    public interface VuiMapControlClickedListener {
        fun compassButtonClicked(cameraPosition: CameraPosition)
        fun locationButtonClicked(isMyLocationEnabled: Boolean)
    }
    public fun setVuiMapControlClickedListener(listener: VuiMapControlClickedListener) {
        this.vuiMapControlClickedListener = listener
    }

}