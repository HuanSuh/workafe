package com.hude.workafe.model.options

import java.io.Serializable

/**
 * Created by huansuh on 2018. 7. 12..
 */
class OpenTime(val dayOfWeek: DayOfWeek, val isOpen: Boolean, val openTime: String?, val closeTime: String?) : Serializable {

    constructor(dayOfWeek: DayOfWeek): this(dayOfWeek, false, null, null)
    constructor(dayOfWeek: DayOfWeek, openTime: String, closeTime: String): this(dayOfWeek, true, openTime, closeTime)

    public fun msg(): String {
        return "${dayOfWeek.str()} " +
                if(isOpen) {
                    "$openTime~$closeTime"
                } else {
                    "휴무"
                } + "\n"
    }

    enum class DayOfWeek(val code: Int) {
        Mon(2), Tue(3), Wed(4), Thu(5), Fri(6), Sat(7), Sun(8);
        fun str(): String {
            return when(this) {
                Mon -> "월요일"
                Tue -> "화요일"
                Wed -> "수요일"
                Thu -> "목요일"
                Fri -> "금요일"
                Sat -> "토요일"
                Sun -> "일요일"
            }
        }
    }
}