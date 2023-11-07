package com.agro.dkdlab.network.model

data class UserModel (
    val id:String,
    val name:String,
    val gender:String?,
    val primaryPhone:String,
    val email:String?,
    val type:String?,
//    val vendor:String?,
    val active:Boolean?,
    val message:String?,
    val deviceId:String?,
    val dob:String?,
    val aadhaarNumber:String?,
    val userPhoto:String?,
    val aadhaarPhoto:String?,
    val aadhaarAddress:String?,
    val status:Boolean,
    val profileApproveStatus:Boolean
)