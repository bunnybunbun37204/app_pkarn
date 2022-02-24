package com.example.dolpjinjunior.utils

class Container {

    private val containerId : String
    private val containerSize : Double
    private val containerType : String
    private val containerDateStart : String
    private val containerDateEnd : String
    private val containerDateFinish : String
    private val containerFixedStatus : Boolean
    private val containerLateDay : Int

    constructor(containerId : String,
                containerSize : Double,
                containerType : String,
                containerDateStart : String,
                containerDateEnd : String,
                containerDateFinish : String,
                containerFixedStatus : Boolean,
                containerLateDay : Int) {
        this.containerId = containerId
        this.containerSize = containerSize
        this.containerType = containerType
        this.containerDateStart = containerDateStart
        this.containerDateEnd = containerDateEnd
        this.containerDateFinish = containerDateFinish
        this.containerFixedStatus = containerFixedStatus
        this.containerLateDay = containerLateDay
    }

    fun getContainerId() : String {
        return containerId
    }

    fun getContainerSize() : Double {
        return containerSize
    }

    fun getContainerType() : String {
        return containerType
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

    fun getContainerLateDay() : Int {
        return containerLateDay
    }

}