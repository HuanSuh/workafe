package com.hude.workafe.view.component.cafeitem

import android.content.Context
import android.text.util.Linkify
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.hude.workafe.R
import com.hude.workafe.model.CafeItem
import com.hude.workafe.utils.Utils
import kotlinx.android.synthetic.main.item_cafe_detail.view.*
import java.util.regex.Pattern

/**
 * Created by huansuh on 2018. 7. 13..
 */
class CafeItemDetailView : RelativeLayout {
    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, item: CafeItem) : super(context) {
        init()
        setItem(item)
    }
    constructor(context: Context?, item: CafeItem, isBanner: Boolean) : super(context) {
        init()
        setItem(item)
        setIsBanner(isBanner)
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
        isFocusable = true
        isClickable = true
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        View.inflate(context, R.layout.item_cafe_detail, this)
        setBackgroundColor(resources.getColor(android.R.color.transparent))
        cafe_btn_close.setOnClickListener {
            onCloseCallback?.onDetailClosing()
        }
    }

    public fun setIsBanner(isBanner: Boolean) {
        if(isBanner) {
            val paddingTop = Utils.dpToPx(context, 40)
            item_cafe_detail.setPadding(item_cafe_detail.paddingLeft, paddingTop, item_cafe_detail.paddingRight, item_cafe_detail.paddingBottom)
            item_cafe_detail.setBackgroundResource(R.drawable.xml_card_background)
            val mlp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val margin = Utils.dpToPx(context, 5)
            mlp.setMargins(margin, 0, margin, margin)
            item_cafe_detail.layoutParams = mlp
        } else {
            val mlp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mlp.addRule(RelativeLayout.BELOW, R.id.item_cafe_contents)
            mlp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            cafe_btn_close.layoutParams = mlp
        }
    }

    public fun setItem(item: CafeItem) {
        cafe_text_name.text = item.name
        cafe_text_address.text = item.address
        val pattern = Pattern.compile(".*", Pattern.DOTALL)
        Linkify.addLinks(cafe_text_address, pattern, "geo:0,0?q=")

        cafe_group_phone.visibility = if(item.phone != null) View.VISIBLE else View.GONE
        cafe_text_phone.text = item.phone
        Linkify.addLinks(cafe_text_phone, Linkify.PHONE_NUMBERS)

        val openTimeMsg = item.getOpenTimeMessage()
        cafe_group_time.visibility = if(openTimeMsg != null) View.VISIBLE else View.GONE
        cafe_text_time.text = openTimeMsg

        cafe_img_thumbnail.visibility = if(item.imgUrl != null) View.VISIBLE else View.GONE
        Glide.with(this).load(item.imgUrl).into(cafe_img_thumbnail)


        if(item.phone == null && item.openTimes == null && item.imgUrl == null) {
            cafe_layout_divider.visibility = View.GONE
        } else {
            cafe_layout_divider.visibility = View.VISIBLE
        }
    }

    private var onCloseCallback: CafeItemDetailView.OnCloseCallback? = null
    public interface OnCloseCallback {
        fun onDetailClosing()
    }
    public fun setOnCloseCallback(callback: OnCloseCallback) {
        this.onCloseCallback = callback
    }

    fun refreshBannerItem(item: CafeItem) {
        setItem(item)
        val showAnimation = AlphaAnimation(0f, 1f)
        showAnimation.interpolator = AccelerateInterpolator()
        showAnimation.duration = 350
        item_cafe_contents.startAnimation(showAnimation)
    }
}