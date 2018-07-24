package com.hude.workafe.view.itemListView

import android.app.AlertDialog
import android.content.Context
import android.support.annotation.NonNull
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.hude.workafe.R
import com.hude.workafe.model.CafeItem
import com.hude.workafe.utils.Constants
import com.hude.workafe.view.component.cafeitem.CafeItemDetailView
import com.hude.workafe.view.component.cafeitem.CafeItemView
import kotlinx.android.synthetic.main.item_list.view.*

/**
 * Created by huansuh on 2018. 7. 13..
 */
class ItemListView : RelativeLayout, ListViewContract.View {

    private var mPresenter: ListViewContract.Presenter? = null

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
        View.inflate(context, R.layout.item_list, this)
    }

    override fun setPresenter(@NonNull presenter: ListViewContract.Presenter) {
        this.mPresenter = presenter
        presenter.initList(context, list_list)
        list_list.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        (parent?.getItemAtPosition(position) as? CafeItem)?.let {
            FirebaseAnalytics.getInstance(context).logEvent(Constants.EVENT_LIST_ITEM_CLICKED, null)
            showDetailDialog(it)
        }
    }

    override fun showDetailDialog(item: CafeItem) {
        val itemView = CafeItemDetailView(context, item, false)

        val builder = AlertDialog.Builder(context)
        val dialog = builder.setView(itemView).show()
        itemView.setOnCloseCallback(object: CafeItemDetailView.OnCloseCallback {
            override fun onDetailClosing() {
                dialog.dismiss()
            }
        })
    }

    class CafeListAdapter(context: Context?) : ArrayAdapter<CafeItem>(context, android.R.layout.simple_list_item_1) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val root = CafeItemView(context, getItem(position))
            root.setInnerBackgroundResource(R.drawable.xml_card_border_background)
            root.setPadding(20, 20, 20, 20)
            return root
        }

        fun setList(cafeList: List<CafeItem>) {
            clear()
            addAll(cafeList)
        }
    }
}