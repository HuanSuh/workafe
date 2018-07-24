package com.hude.workafe.view.headerControlView

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.NonNull
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.hude.workafe.R
import com.hude.workafe.model.CafeFilter
import com.hude.workafe.utils.Constants
import com.polyak.iconswitch.IconSwitch
import kotlinx.android.synthetic.main.header_control.view.*

/**
 * Created by huansuh on 2018. 7. 12..
 */

class HeaderControlView : RelativeLayout, HeaderControlViewContract.View, View.OnClickListener {
    private var mPresenter: HeaderControlViewContract.Presenter? = null
    private var filter: CafeFilter? = null

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
        View.inflate(context, R.layout.header_control, this)
    }

    override fun setPresenter(@NonNull presenter: HeaderControlViewContract.Presenter) {
        mPresenter = presenter
        mPresenter?.initSwitch(header_control_switch)
        header_control_switch.setCheckedChangeListener(this)
        header_control_filter.setOnClickListener(this)
    }

    override fun onCheckChanged(current: IconSwitch.Checked?) {
        current?.let {
            val param = Bundle()
            param.putString("selected", if(it.ordinal == SWITCH_MAP) "map" else "list")
            FirebaseAnalytics.getInstance(context).logEvent(Constants.EVENT_SWITCH_TOGGLED, param)
            when(it.ordinal) {
                SWITCH_MAP -> header_control.setBackgroundColor(resources.getColor(android.R.color.transparent))
                SWITCH_LIST -> header_control.setBackgroundColor(resources.getColor(android.R.color.white))
            }
        }
        mPresenter?.onCheckChanged(current)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.header_control_filter -> {
                showFilterDialog()
            }
        }
    }

    override fun showFilterDialog() {
        val editingFilter = CafeFilter(filter?.booleanArray)
        AlertDialog.Builder(context)
                .setMultiChoiceItems(CafeFilter.titleList, editingFilter.booleanArray, { _, which, isChecked ->
                    editingFilter.booleanArray[which] = isChecked
                })
                .setPositiveButton(R.string.adjust, { _: DialogInterface, _: Int ->
                    if(editingFilter.isEnabled()) {
                        header_control_filter.setColorFilter(resources.getColor(R.color.colorActivate))
                    } else {
                        header_control_filter.setColorFilter(resources.getColor(R.color.colorDeactivate))
                    }
                    filter = CafeFilter(editingFilter.booleanArray)
                    mPresenter?.onFilterSetListener?.onFilterSet(editingFilter.withArray())
                })
                .setNegativeButton(R.string.cancel, null)
                .setOnDismissListener {
                    val param = Bundle()
                    param.putString("view_mode", if(header_control_switch.checked.ordinal == SWITCH_MAP) "map" else "list")
                    param.putBoolean("filter_adjust", filter?.isEnabled() ?: false)
                    FirebaseAnalytics.getInstance(context).logEvent(Constants.EVENT_FILTER_DIALOG, param)
                }
                .setTitle(R.string.adjust_filter)
                .show()
    }
}