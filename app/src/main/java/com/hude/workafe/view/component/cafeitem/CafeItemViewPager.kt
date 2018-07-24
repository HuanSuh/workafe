package com.hude.workafe.view.component.cafeitem

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.SparseArray
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.firebase.analytics.FirebaseAnalytics
import com.hude.workafe.R
import com.hude.workafe.model.CafeItem
import com.hude.workafe.utils.Constants
import com.hude.workafe.utils.Utils


/**
 * Created by huansuh on 2018. 7. 13..
 */
const val PAGE_MARGIN = 35
class CafeItemViewPager : ViewPager {

    constructor(context: Context) : super(context) {
        _init()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        _init()
    }

    private fun _init() {
        initViewConfig()
//        initPager()
    }

    private fun initViewConfig() {
        pageMargin = Utils.dpToPx(context, PAGE_MARGIN) * -1
    }

    private fun initPager() {
        offscreenPageLimit = 2
        adapter = CafeItemViewPagerAdapter(context)
        addOnPageChangeListener(object: OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                callOnPageSelected(position)
            }
        })
        val tapGestureDetector = GestureDetector(context, TapGestureListener())
        setOnTouchListener { _, event ->
            tapGestureDetector.onTouchEvent(event)
            false
        }
    }

    fun setCafeList(cafeList: List<CafeItem>) {
        initPager()
        (adapter as CafeItemViewPagerAdapter).setCafeList(cafeList)
    }
    fun setCurrentPage(item: Int) {
        setCurrentItem(item, true)
    }

    fun callOnPageSelected(position: Int) {
        if(onPageSelectedListener == null && adapter != null) return
        val item = (adapter as CafeItemViewPagerAdapter).getItemAtPos(position)
        if(item != null) {
            FirebaseAnalytics.getInstance(context).logEvent(Constants.EVENT_PAGER_ITEM_MOVED, null)
            onPageSelectedListener?.onPageSelected(position, item)
        }
    }

    private var onPageSelectedListener: CafeItemViewPager.OnPageSelectedListener? = null
    interface OnPageSelectedListener {
        fun onPageSelected(position: Int, item: CafeItem)
    }
    fun setOnPageSelectedListener(listener: OnPageSelectedListener) {
        this.onPageSelectedListener = listener
    }

    private var mOnItemClickListener: CafeItemViewPager.OnItemClickListener? = null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int, cafeItem: CafeItem?)
    }

    private inner class TapGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            FirebaseAnalytics.getInstance(context).logEvent(Constants.EVENT_PAGER_ITEM_CLICKED, null)
            mOnItemClickListener?.onItemClick(currentItem, (adapter as CafeItemViewPagerAdapter).getItemAtPos(currentItem))
            return true
        }
    }

    class CafeItemViewPagerAdapter(private val context: Context) : PagerAdapter() {
        private val views = SparseArray<CafeItemView>()
        private val marginH = Utils.dpToPx(context, PAGE_MARGIN - 12)
        private var cafeList: List<CafeItem>? = null

        fun setCafeList(cafeList: List<CafeItem>) {
            this.cafeList = cafeList
            views.clear()
            notifyDataSetChanged()
        }

        fun getItemAtPos(pos: Int): CafeItem? {
            if(cafeList != null && 0 <= pos && pos < cafeList!!.size) {
                return cafeList!![pos]
            }
            return null
        }
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val root = CafeItemView(context)
            root.setItem(cafeList!![position])
            root.getChildAt(0).elevation = 5f
            root.setInnerBackgroundResource(R.drawable.xml_card_background)
            root.setPadding(marginH, 0, marginH, 0)
            val mlp = MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            mlp.bottomMargin = Utils.dpToPx(context, 4)
            root.layoutParams = mlp
            container.addView(root)
            views.put(position, root)
            return root
        }
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            var view: View? = `object` as View
            container.removeView(view)
            views.remove(position)
            view = null    // free memory
        }

        override fun notifyDataSetChanged() {
            var key: Int
            for (pos in 0 until views.size()) {
                key = views.keyAt(pos)
                val view = views.get(key)
                view.setItem(cafeList!![pos])
            }
            super.notifyDataSetChanged()
        }
        override fun getCount(): Int {
            return cafeList?.size ?: 0
        }

    }
}