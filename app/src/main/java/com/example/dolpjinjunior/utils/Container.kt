package com.example.dolpjinjunior.utils

class Container(
    private val containerId: String,
    private val containerSize: Double,
    private val containerType: String,
    private val containerDamageLv: String,
    private val containerDateStart: String,
    private val containerDateEnd: String,
    private val containerDateFinish: String,
    private val containerFixedStatus: Boolean,
    private val containerLateDay: Int?
) {

    fun getContainerId() : String {
        return containerId
    }

    fun getContainerSize() : Double {
        return containerSize
    }

    fun getContainerType() : String {
        return containerType
    }

    fun getContainerDamageLv() : String {
        return containerDamageLv
    }

    fun getContainerDateStart() : String {
        return containerDateStart
    }

    fun getContainerDateEnd() : String {
        return containerDateEnd
    }

    fun getContainerDateFinish() : String {
        return containerDateFinish
    }

    fun getContainerFixedStatus() : Boolean {
        return containerFixedStatus
    }

    fun getContainerLateDay() : Int? {
        return containerLateDay
    }

}