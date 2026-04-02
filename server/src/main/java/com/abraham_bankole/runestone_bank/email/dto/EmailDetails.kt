package com.abraham_bankole.runestone_bank.email.dto

data class EmailDetails (
    var recipientEmail: String = "",
    var recipientName: String = "",
    var messageBody: String = "",
    var subject: String = "",
    var attachment: String? = null
)
