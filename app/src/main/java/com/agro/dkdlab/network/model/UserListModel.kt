package com.agro.dkdlab.network.model

data class UserListModel (
    val primaryPhone: String,
    val userName: String,
    val userType: String?,
    val userId: String,
    val dob: String,
    val aadhaarNumber: String,
    val userPhoto: String,
    val aadhaarPhoto: String,
    val aadhaarAddress: String,
    val active: Boolean?,
    val otpVerified: Boolean,
    val vendor: VendorModel?,
    val status : Boolean
)
//