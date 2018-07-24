package com.hude.workafe.view.itemListView

import android.content.Context
import android.widget.ListView
import com.hude.workafe.model.CafeItem

/**
 * Created by huansuh on 2018. 7. 13..
 */
class ListViewPresenter: ListViewContract.Presenter {

    private var mListView : ListView? = null

    private var cafeList : List<CafeItem> = ArrayList()
    private var mAdapter: ItemListView.CafeListAdapter? = null

    override fun start() {
    }

    override fun initList(context: Context, list: ListView?) {
        this.mListView = list
        mListView?.let {
            mAdapter = ItemListView.CafeListAdapter(context)
            it.adapter = mAdapter
        }
    }

    override fun setCafeList(cafeList: List<CafeItem>) {
        this.cafeList = cafeList
        mAdapter?.setList(cafeList)
    }
}