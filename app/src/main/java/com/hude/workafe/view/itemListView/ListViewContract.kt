package com.hude.workafe.view.itemListView

import android.content.Context
import android.widget.AdapterView
import android.widget.ListView
import com.hude.workafe.model.CafeItem
import com.hude.workafe.view.BasePresenter
import com.hude.workafe.view.BaseView

/**
 * Created by huansuh on 2018. 7. 12..
 */
interface ListViewContract {

    interface View : BaseView<Presenter>, AdapterView.OnItemClickListener {
        fun showDetailDialog(item: CafeItem)
    }

    interface Presenter : BasePresenter {
        fun setCafeList(cafeList: List<CafeItem>)
        fun initList(context: Context, list: ListView?)

    }
}