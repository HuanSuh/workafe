package com.hude.workafe.model

/**
 * Created by huansuh on 2018. 7. 14..
 */
class CafeFilter(var opened: Boolean, var wifi: Boolean, var smoking: Boolean) {
    var booleanArray : BooleanArray = BooleanArray(3)

    constructor() : this(false, false, false)
    constructor(booleanArray: BooleanArray?) : this() {
        booleanArray?.let {
            if(it.count() != 3) return
            this.booleanArray = it.clone()
            opened = it[0]
            wifi = it[1]
            smoking = it[2]
        }
    }

    fun withArray(): CafeFilter {
        return CafeFilter(booleanArray[0], booleanArray[1], booleanArray[2])
    }

    fun isEnabled(): Boolean {
        val arr = booleanArray
        for (enabled in arr) {
            if(enabled) {
                return true
            }
        }
        return false
    }

    companion object {
        val titleList = arrayOf<CharSequence>("영업 중", "무선 인터넷", "흡연실")
    }
}