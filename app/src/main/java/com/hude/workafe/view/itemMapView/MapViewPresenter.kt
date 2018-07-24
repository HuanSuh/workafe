package com.hude.workafe.view.itemMapView

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.FirebaseDatabase
import com.hude.workafe.R
import com.hude.workafe.model.CafeData
import com.hude.workafe.model.CafeItem
import com.hude.workafe.utils.Config
import com.hude.workafe.utils.Constants
import com.hude.workafe.view.component.cafeitem.CafeItemDetailView
import com.hude.workafe.view.component.cafeitem.CafeItemViewPager
import com.hude.workafe.view.component.map.VuiGoogleMap

/**
 * Created by huansuh on 2018. 7. 13..
 */
class MapViewPresenter(private var mView: ItemMapView) : MapViewContract.Presenter {

    override var willSortItemsCallback: MapViewContract.WillSortItemsCallback? = null

    private var bannerView: CafeItemDetailView? = null
    private var mMap: GoogleMap? = null
    private var vuiGoogleMap: VuiGoogleMap? = null
    private var mPager: CafeItemViewPager? = null

    private var cafeList : List<CafeItem> = ArrayList()
    private var edgePoints: ArrayList<LatLng> = ArrayList()
    private val markerMap = HashMap<Long, Marker?>()

    override fun start() {

    }

    override fun setBannerView(bannerView: CafeItemDetailView?) {
        this.bannerView = bannerView
        mView.setBannerView(bannerView)
    }

    override fun initViewPager(pager: CafeItemViewPager?) {
        this.mPager = pager
    }

    override fun setWKMap(vuiGoogleMap: VuiGoogleMap?) {
        this.vuiGoogleMap = vuiGoogleMap
        this.vuiGoogleMap?.onLocationResultListener = this
    }

    override fun initMap(activity: AppCompatActivity) {
        if(vuiGoogleMap == null) {
            throw Exception("Failed to initialize vuiGoogleMap : vuiGoogleMap == null")
        }
        vuiGoogleMap?.let {
            it.setActivity(activity)
            it.setOnMapCallback(this)
            it.setVuiMapControlClickedListener(this)
            if(!it.initMap(activity.fragmentManager)) {
                if(activity.fragmentManager == null) {
                    throw Exception("Failed to initialize vuiGoogleMap : FragmentManager == null")
                } else {
                    throw  Exception("Failed to initialize vuiGoogleMap : MapFragment == null")
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        this.mMap = map
        mMap?.let {
            mView.setMap(it)
            it.setOnCameraIdleListener {
                val isContainEdge = checkContainsEdges()
                if(cafeList.isEmpty()) {
                    mView.showNothingDialog(R.string.match_none)
                } else {
                    if (isContainEdge) {
                        mView.hideNothingDialog()
                    } else {
                        FirebaseAnalytics.getInstance(mView.context).logEvent(Constants.EVENT_NOT_GANGNAM, null)
                        mView.showNothingDialog(R.string.only_gangnam)
                    }
                }
                vuiGoogleMap?.setCompassButtonVisible(!isContainEdge || it.cameraPosition.bearing != 0f)
            }
            initCameraPosition()
            addMarkers()
        }
    }

    override fun initCameraPosition() {
        mMap?.let {
            val cameraPosition = CameraPosition.Builder()
                    .target(Config.GANGNAM).zoom(Config.DEFAULT_ZOOM).bearing(0f).tilt(0f)
                    .build()
            it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), Config.ANIMATE_CAMERA_DELAY, null)
        }
    }

    override fun compassButtonClicked(cameraPosition: CameraPosition) {
        val isContainsEdges = checkContainsEdges()
        val param = Bundle()
        param.putBoolean("outside_gangnam", !isContainsEdges)
        FirebaseAnalytics.getInstance(mView.context).logEvent(Constants.EVENT_COMPASS, null)
        cameraPosition.target?.let {
            if(!isContainsEdges) {
                initCameraPosition()
            }
        }
    }

    override fun locationButtonClicked(isMyLocationEnabled: Boolean) {
        FirebaseAnalytics.getInstance(mView.context).logEvent(Constants.EVENT_MY_LOCATION, null)
    }
    override fun getMyLocation(context: Context) {
        vuiGoogleMap?.getMyLocation()
    }

    override fun setCafeData(cafeData: CafeData) {
        edgePoints.clear()
        edgePoints.add(LatLng(cafeData.minLat, cafeData.minLng))
        edgePoints.add(LatLng(cafeData.minLat, cafeData.maxLng))
        edgePoints.add(LatLng(cafeData.maxLat, cafeData.maxLng))
        edgePoints.add(LatLng(cafeData.maxLat, cafeData.minLng))
    }

    override fun checkContainsEdges(): Boolean {
        return mMap?.let {
            val bounds = it.projection.visibleRegion.latLngBounds
            for (markers in edgePoints) {
                if (bounds.contains(markers)) {
                    return@let true
                }
            }
            val edgeBound = LatLngBounds(edgePoints[0], edgePoints[2])
            if(edgeBound.contains(it.cameraPosition.target)) {
                return@let true
            }
            return@let false
        } == true
    }

    override fun setCafeList(cafeList: List<CafeItem>) {
        this.cafeList = cafeList
        mPager?.setCafeList(cafeList)
        addMarkers()
        if(cafeList.isEmpty()) {
            mView.showNothingDialog(R.string.match_none)
        } else {
            mView.hideNothingDialog()
        }
    }

    override fun addMarkers() {
        if(mMap == null) return
        mMap?.let {
            it.clear()
            markerMap.clear()
            for ((pos, item: CafeItem) in cafeList.withIndex()) {
                val markerOptions = MarkerOptions()
                item.latLng?.let { markerOptions.position(it) }
                markerOptions.title(item.name)
                markerMap[item.id] = mView.addMarker(markerOptions, pos)
            }
            if (cafeList.isNotEmpty()) {
                markerMap[cafeList[0].id]?.showInfoWindow()
            }
        }
    }

    override fun selectMarkerItem(item: CafeItem) {
        markerMap[item.id]?.showInfoWindow()
        mMap?.animateCamera(CameraUpdateFactory.newLatLng(item.latLng), Config.ANIMATE_CAMERA_DELAY, null)
    }
    override fun selectPagerItem(position: Int) {
        mPager?.setCurrentPage(position)
    }

    override fun onLocationResultSuccess(latLng: LatLng) {
        willSortItemsCallback?.sortItemsByLocation(latLng)
    }
    override fun onLocationResultFail(context: Context) {
        Toast.makeText(context, context.getString(R.string.failed_to_get_location), Toast.LENGTH_SHORT).show()
    }

    override fun requestLocation(context: Context): Task<Void>? {
        logRequestLocation(context)
        return mMap?.cameraPosition?.let {
            return@let FirebaseDatabase.getInstance().getReference(Constants.EVENT_REQUEST_LOCATION).push().setValue(it.target)
        }
    }
    private fun logRequestLocation(context: Context) {
        mMap?.cameraPosition?.target?.let {
            val params = Bundle()
            params.putString("latitude", it.latitude.toString())
            params.putString("longitude", it.longitude.toString())
            FirebaseAnalytics.getInstance(context).logEvent(Constants.EVENT_REQUEST_LOCATION, params)
        }
    }
}