package com.hude.workafe.view.itemMapView

import android.content.Context
import android.support.annotation.NonNull
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hude.workafe.R
import com.hude.workafe.model.CafeItem
import com.hude.workafe.view.component.cafeitem.CafeItemDetailView
import kotlinx.android.synthetic.main.item_map.view.*


/**
 * Created by huansuh on 2018. 7. 13..
 */

class ItemMapView : RelativeLayout, MapViewContract.View {
    private var mPresenter : MapViewContract.Presenter? = null
    private var mMap: GoogleMap? = null
    private var mBannerView: CafeItemDetailView? = null

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
        View.inflate(context, R.layout.item_map, this)
        map_btn_request_location.setOnClickListener({
            requestLocation()
        })
    }

    override fun setPresenter(@NonNull presenter: MapViewContract.Presenter) {
        this.mPresenter = presenter
        presenter.initViewPager(map_pager)
        map_pager.setOnPageSelectedListener(this)
        map_pager.setOnItemClickListener(this)
        presenter.setWKMap(map_wkmap)
        presenter.start()
    }

    override fun setBannerView(bannerView: CafeItemDetailView?) {
        this.mBannerView = bannerView
        mBannerView?.visibility = View.GONE
        mBannerView?.setIsBanner(true)
        mBannerView?.setOnCloseCallback(this)
    }

    override fun setMap(map: GoogleMap) {
        this.mMap = map
        map.setOnMarkerClickListener { marker ->
            onMarkerClick(marker)
        }
    }

    override fun addMarker(markerOptions: MarkerOptions, pos: Int): Marker? {
        mMap?.let {
            val marker = it.addMarker(markerOptions)
            marker.tag = pos
            return marker
        }
        return null
    }

    override fun onPageSelected(position: Int, item: CafeItem) {
        mPresenter?.selectMarkerItem(item)
        mBannerView?.let {
            if(it.visibility == View.VISIBLE) {
                setBannerItem(item)
                it.refreshBannerItem(item)
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        mPresenter?.selectPagerItem(marker?.tag as Int)
        return false
    }

    override fun onItemClick(position: Int, cafeItem: CafeItem?) {
        setBannerItem(cafeItem)
    }
    override fun setBannerItem(cafeItem: CafeItem?) {
        if(cafeItem == null) return

        mBannerView?.let {
            if(it.visibility != View.VISIBLE) {
                it.setItem(cafeItem)
                it.visibility = View.VISIBLE
                val openAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,0f,
                        Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF,  0f)
                openAnimation.interpolator = AccelerateDecelerateInterpolator()
                openAnimation.duration = 400
                it.startAnimation(openAnimation)
            } else {
                it.refreshBannerItem(cafeItem)
            }
        }
    }
    override fun onDetailClosing() {
        mBannerView?.let {
            val closeAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,0f,
                    Animation.RELATIVE_TO_SELF, 0f,Animation.RELATIVE_TO_SELF,  -1f)
            closeAnimation.setAnimationListener(object: Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    it.visibility = View.GONE
                }
            })
            closeAnimation.duration = 250
            it.startAnimation(closeAnimation)
        }
    }

    override fun showNothingDialog(@StringRes msg: Int) {
        map_layout_noitem.visibility = View.VISIBLE
        map_text_noitem.setText(msg)
        map_img_aim.visibility = View.GONE
        map_btn_request_location.visibility = View.GONE
        when(msg) {
            R.string.only_gangnam -> {
                map_pager.visibility = View.GONE
                map_img_aim.visibility = View.VISIBLE
                map_btn_request_location.visibility = View.VISIBLE
            }
        }
    }

    override fun hideNothingDialog() {
        map_layout_noitem.visibility = View.GONE
        map_pager.visibility = View.VISIBLE
        map_img_aim.visibility = View.GONE
        map_btn_request_location.visibility = View.GONE
    }

    private fun requestLocation() {
        this.mPresenter?.requestLocation(context)?.let {
            it.addOnCompleteListener {
                Toast.makeText(context, context.getString(R.string.request_complete), Toast.LENGTH_SHORT).show()
            }
        }
    }
}