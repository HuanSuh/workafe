package com.hude.workafe.view

import android.support.annotation.NonNull

/**
 * Created by huansuh on 2018. 7. 12..
 */
interface BaseView<T> {

    abstract fun setPresenter(@NonNull presenter: T)

}