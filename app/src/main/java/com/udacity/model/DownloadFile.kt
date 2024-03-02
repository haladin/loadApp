package com.udacity.model

data class DownloadFile (
    val id: Int,
    val url: String,
    val description: String,
    var status: String,
) {
}