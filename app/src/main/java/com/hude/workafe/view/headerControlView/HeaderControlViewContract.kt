package com.hude.workafe.view.headerControlView

import com.hude.workafe.model.CafeFilter
import com.hude.workafe.view.BasePresenter
import com.hude.workafe.view.BaseView
import com.polyak.iconswitch.IconSwitch

/**
 * Created by huansuh on 2018. 7. 12..
 */
const val SWITCH_MAP = 0
const val SWITCH_LIST = 1
interface HeaderControlViewContract {

    interface View : BaseView<Presenter>, IconSwitch.CheckedChangeListener {
        fun showFilterDialog()
    }

    interface Presenter : BasePresenter, IconSwitch.CheckedChangeListener {
        var checkedChangeListener : IconSwitch.CheckedChangeListener?
        var onFilterSetListener: OnFilterSetListener?

        fun initSwitch(switch: IconSwitch?)
    }

    interface OnFilterSetListener {
        fun onFilterSet(filter: CafeFilter)
    }
}