package com.hude.workafe.model

import java.io.Serializable

/**
 * Created by huansuh on 2018. 7. 16..
 */
class CafeData(val items: List<CafeItem?>, val minLat: Double, val minLng: Double, val maxLat: Double, val maxLng: Double) : Serializable