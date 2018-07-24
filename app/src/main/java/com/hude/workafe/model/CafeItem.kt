package com.hude.workafe.model

import com.google.android.gms.maps.model.LatLng
import com.hude.workafe.model.options.CafeSize
import com.hude.workafe.model.options.OpenTime
import com.hude.workafe.model.options.Plug
import com.hude.workafe.utils.Utils
import java.io.Serializable
import java.util.*

/**
 * Created by huansuh on 2018. 7. 12..
 */
class CafeItem : Serializable {
    var id: Long = -1L
    var name: String? = null
    var address: String? = null
    var phone: String? = null
    var latLng: LatLng? = null
    var plug: Plug? = null
    var size: CafeSize? = null
    var openTimes: List<OpenTime>? = null
    var imgUrl: String? = null
    var hasWifi : Boolean = false
    var hasSmoking: Boolean = false
    var hasParking: Boolean = false
    var hasToilet: Boolean = false
    var distance: Double? = null

    constructor() : super()

    constructor(id: Long, name: String?, address: String?, phone: String?, latLng: LatLng?, plug: Plug?, size: CafeSize?,
                openTime: List<OpenTime>?, imgUrl: String?,
                hasWifi: Boolean, hasSmoking: Boolean, hasParking: Boolean, hasToilet: Boolean, distance: Double?) {
        this.id = id
        this.name = name
        this.address = address
        this.phone = phone
        this.latLng = latLng
        this.plug = plug
        this.size = size
        this.openTimes = openTime
        this.imgUrl = imgUrl
        this.hasWifi = hasWifi
        this.hasSmoking = hasSmoking
        this.hasParking = hasParking
        this.hasToilet = hasToilet
        this.distance = distance
    }

    override fun toString(): String {
        return "CafeItem{" +
                "id=" + id +
                "name='" + name + '\''.toString() +
                "address='" + address + '\''.toString() +
                "phone='" + phone + '\''.toString() +
                ", latLng=" + latLng +
                ", plug=" + plug +
                ", size=" + size +
                ", openTimes=" + openTimes +
                ", hasWifi=" + hasWifi +
                ", hasSmoking=" + hasSmoking +
                ", hasParking=" + hasParking +
                ", hasToilet=" + hasToilet +
                ", distance=" + distance +
                '}'.toString()
    }

    public fun getOpenTimeMessage() : String? {
        if(openTimes == null) return null
        var msg = ""
        openTimes?.let {
            val sorted = it.sortedBy { time -> time.dayOfWeek }
            for(time in sorted) {
                msg += time.msg()
            }
        }
        return if(Utils.isEmptyString(msg)) null else msg
    }

    public fun isOpen() : Boolean {
        val now = Calendar.getInstance()
        var openTime : OpenTime? = null
        val day = if(now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 8 else now.get(Calendar.DAY_OF_WEEK)
        openTimes?.let {
            for(time in it) {
                if(time.dayOfWeek.code == day) {
                    openTime = time
                    break
                }
            }
        }

        var isOpen = false
        openTime?.let {
            if(it.isOpen) {
                val opens = it.openTime?.split(":")
                val closes = it.closeTime?.split(":")

                val openHour = opens?.get(0)?.toInt() ?: 24
                val openMin  = opens?.get(1)?.toInt() ?: 0
                val closeHour = closes?.get(0)?.toInt() ?: 0
                val closeMin  = closes?.get(1)?.toInt() ?: 0

                var afterOpen = false
                val hour = now.get(Calendar.HOUR_OF_DAY)
                val min = now.get(Calendar.MINUTE)
                if(openHour < hour || (openHour == hour && openMin <= min)) {
                    afterOpen = true
                }
                if(afterOpen && (closeHour > hour || (closeHour == hour && closeMin > min))) {
                    isOpen = true
                }
            }
        }
        return isOpen
    }
}
/*
* 위치, 이름, (콘센트-넉넉함,부족함,없음), 흡연실, 오픈시간 + 규모
* 주소, 와이파이
* */