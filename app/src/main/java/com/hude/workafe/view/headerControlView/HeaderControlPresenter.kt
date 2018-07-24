package com.hude.workafe.view.headerControlView

import com.google.firebase.analytics.FirebaseAnalytics
import com.hude.workafe.utils.Constants
import com.polyak.iconswitch.IconSwitch

/**
 * Created by huansuh on 2018. 7. 13..
 */
class HeaderControlPresenter : HeaderControlViewContract.Presenter {
    override var checkedChangeListener : IconSwitch.CheckedChangeListener? = null
    override var onFilterSetListener: HeaderControlViewContract.OnFilterSetListener? = null

    override fun start() {

    }

    override fun initSwitch(switch: IconSwitch?) {
        switch?.setCheckedChangeListener(this)
    }

    override fun onCheckChanged(current: IconSwitch.Checked?) {
        checkedChangeListener?.onCheckChanged(current)
    }
}