package com.nullsolutions.mlxapp.api

import com.google.gson.annotations.SerializedName


data class ApiRequest(
    @SerializedName("call") var call: String? = null,
    @SerializedName("reqdata") var reqdata: ArrayList<String> = arrayListOf()
)

data class RegistrationResponse(
    @SerializedName("msg") var msg: String? = null,
    @SerializedName("status") var status: Boolean? = null
)

data class LoginResponse(
    @SerializedName("data") var data: LoginData? = LoginData(),
    @SerializedName("msg") var msg: String? = null,
    @SerializedName("status") var status: Boolean? = null
)

data class LoginData(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("coin") var coin: String? = null
)

data class GetBalanceResponse(
    @SerializedName("msg") var msg: String? = null,
    @SerializedName("coin") var coin: String? = null,
    @SerializedName("status") var status: Boolean? = null
)

data class UpdateResponse(
    @SerializedName("msg") var msg: String? = null,
    @SerializedName("status") var status: Boolean? = null
)

data class ReferralResponse(
    @SerializedName("code") var code: String? = null,
    @SerializedName("status") var status: Boolean? = null
)

data class WithdrawResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("data") var data: ArrayList<WithdrawData> = arrayListOf()
)

data class WithdrawData(
    @SerializedName("id") var id: String? = null,
    @SerializedName("userid") var userid: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("update_at") var updateAt: String? = null,
    @SerializedName("process_by") var processBy: String? = null,
    @SerializedName("status") var status: String? = null
)


data class LogResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("data") var data: ArrayList<LogData> = arrayListOf()
)

data class LogData(
    @SerializedName("scandata") var scandata: String? = null,
    @SerializedName("scanat") var scanat: String? = null
)

data class ForgetResponse(
    @SerializedName("data") var data: ForgetData? = ForgetData(),
    @SerializedName("msg") var msg: String? = null,
    @SerializedName("status") var status: Boolean? = null
)

data class ForgetData(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("coin") var coin: String? = null
)