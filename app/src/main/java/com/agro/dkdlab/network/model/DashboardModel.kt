package com.agro.dkdlab.network.model

data class DashboardModel (
    val villageCode: String?,
    var villageName: String?,
    val completedCount: Int,
    var pendingCount: Int,
    var surveyCount: Int,
    var totalKhasra: Int,
    var totalVillage: String?
)
