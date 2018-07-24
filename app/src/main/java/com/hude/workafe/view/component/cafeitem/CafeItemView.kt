package com.hude.workafe.view.component.cafeitem

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.hude.workafe.R
import com.hude.workafe.model.CafeItem
import com.hude.workafe.model.options.CafeSize
import com.hude.workafe.model.options.Plug
import kotlinx.android.synthetic.main.item_cafe.view.*

/**
 * Created by huansuh on 2018. 7. 13..
 */
class CafeItemView : RelativeLayout {
    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, item: CafeItem) : super(context) {
        init()
        setItem(item)
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
        View.inflate(context, R.layout.item_cafe, this)
    }

    public fun setInnerBackgroundResource(@DrawableRes res: Int) {
        item_cafe.setBackgroundResource(res)
    }

    public fun setItem(item: CafeItem) {
        cafe_text_name.text = item.name
        cafe_text_address.text = item.address

        val dist = item.distance
        if(dist == null) {
            cafe_text_distance.visibility = View.GONE
        } else {
            if(dist > 1000) {
                cafe_text_distance.text = String.format("%.1fkm", dist / 1000)
            } else {
                cafe_text_distance.text = String.format("%.0fm", dist)
            }
            cafe_text_distance.visibility = View.VISIBLE
        }

        cafe_layout_info.visibility = View.VISIBLE
        if(item.size == null || item.size == Plug.UNKNOWN) {
            cafe_img_size.visibility = View.GONE
            cafe_text_size.visibility = View.GONE
        } else {
            cafe_img_size.visibility = View.VISIBLE
            cafe_text_size.visibility = View.VISIBLE
            cafe_text_size.text = item.size?.msg
        }

        if(item.plug == null || item.plug == Plug.UNKNOWN) {
            cafe_img_plug.visibility = View.GONE
            cafe_text_plug.visibility = View.GONE
        } else {
            cafe_img_plug.visibility = View.VISIBLE
            cafe_text_plug.visibility = View.VISIBLE
            cafe_text_plug.text = item.plug?.msg
        }
        if(item.openTimes == null || item.openTimes?.count()?:0 <= 0) {
            cafe_img_open.visibility = View.GONE
            cafe_text_open.visibility = View.GONE
        } else {
            cafe_img_open.visibility = View.VISIBLE
            cafe_text_open.visibility = View.VISIBLE
            if(item.isOpen()) {
                cafe_text_open.text = context.getString(R.string.opened)
                cafe_text_open.setTextColor(resources.getColor(R.color.colorPrimary))
            } else {
                cafe_text_open.text = context.getString(R.string.closed)
                cafe_text_open.setTextColor(resources.getColor(R.color.alert_red))
            }
        }
        cafe_img_wifi.visibility = if(item.hasWifi) View.VISIBLE else View.GONE
        cafe_img_smoke.visibility = if(item.hasSmoking) View.VISIBLE else View.GONE
        cafe_img_parking.visibility = if(item.hasParking) View.VISIBLE else View.GONE
        cafe_img_toilet.visibility = if(item.hasToilet) View.VISIBLE else View.GONE
    }
}