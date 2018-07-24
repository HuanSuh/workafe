package com.hude.workafe.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.hude.workafe.R

@SuppressLint("Registered")
open
/**
 * Created by huansuh on 2018. 7. 14..
 */
class VuiActionBarActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActionBar()
    }

    private fun initActionBar() {
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_actionbar)
        supportActionBar?.customView?.let {
            (it.parent as Toolbar).setContentInsetsAbsolute(0, 0)
        }
    }

    protected fun setActionBarLogo(visible: Boolean) {
        val imageView = supportActionBar?.customView?.findViewById<ImageView>(R.id.actionbar_logo)
        val textView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
        if(visible) {
            imageView?.visibility = View.VISIBLE
            textView?.visibility = View.GONE
        } else {
            imageView?.visibility = View.GONE
        }
    }

    protected fun setActionBarTitle(title: String) {
        val imageView = supportActionBar?.customView?.findViewById<ImageView>(R.id.actionbar_logo)
        val textView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
        if(TextUtils.isEmpty(title)) {
            imageView?.visibility = View.VISIBLE
            textView?.visibility = View.GONE
        } else {
            imageView?.visibility = View.GONE
            textView?.visibility = View.VISIBLE
            textView?.text = title
        }
    }

    protected fun setActionBarHomeEnabled(enabled: Boolean) {
        val backButton = supportActionBar?.customView?.findViewById<ImageButton>(R.id.actionbar_back)
        if(enabled) {
            backButton?.visibility = View.VISIBLE
            backButton?.setOnClickListener { onBackPressed() }
        } else {
            backButton?.visibility = View.INVISIBLE
        }
    }
}