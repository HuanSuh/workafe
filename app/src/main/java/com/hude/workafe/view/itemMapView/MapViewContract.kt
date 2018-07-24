package com.hude.workafe.view.itemMapView

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.hude.workafe.model.CafeData
import com.hude.workafe.model.CafeItem
import com.hude.workafe.view.BasePresenter
import com.hude.workafe.view.BaseView
import com.hude.workafe.view.component.cafeitem.CafeItemDetailView
import com.hude.workafe.view.component.cafeitem.CafeItemViewPager
import com.hude.workafe.view.component.map.VuiGoogleMap

/**
 * Created by huansuh on 2018. 7. 12..
 */
interface MapViewContract {

    interface View : BaseView<Presenter>, GoogleMap.OnMarkerClickListener,
            CafeItemViewPager.OnPageSelectedListener, CafeItemViewPager.OnItemClickListener,
            CafeItemDetailView.OnCloseCallback {
        fun setBannerView(bannerView: CafeItemDetailView?)
        fun setBannerItem(cafeItem: CafeItem?)
        fun setMap(map: GoogleMap)
        fun addMarker(markerOptions: MarkerOptions, pos: Int): Marker?

        fun showNothingDialog(@StringRes msg: Int)
        fun hideNothingDialog()
    }

    interface Presenter : BasePresenter, OnMapReadyCallback,
            VuiGoogleMap.VuiMapControlClickedListener, VuiGoogleMap.OnLocationResultListener {
        var willSortItemsCallback : WillSortItemsCallback?

        fun initViewPager(pager: CafeItemViewPager?)
        fun setBannerView(bannerView: CafeItemDetailView?)
        fun setWKMap(vuiGoogleMap: VuiGoogleMap?)
        fun initMap(activity: AppCompatActivity)
        fun initCameraPosition()
        fun setCafeData(cafeData: CafeData)
        fun setCafeList(cafeList: List<CafeItem>)
        fun addMarkers()

        fun selectMarkerItem(item: CafeItem)
        fun selectPagerItem(position: Int)
        fun checkContainsEdges(): Boolean

        fun getMyLocation(context: Context)
        fun requestLocation(context: Context): Task<Void>?
    }

    interface WillSortItemsCallback {
        fun sortItemsByLocation(latLng: LatLng)
    }
}