package com.qcellls.contact.ui.util

fun String.isPhoneNumber(): Boolean {
    val newNumber = this.replace("-", "").replace("+82", "0")
    return newNumber.length == 11 && newNumber.startsWith("010")
}


fun String.toPhoneNumberFormat(): String {
    val res = this.replace("+82", "0").replace("-", "")
    return res.substring(0, 3) + "-" + res.substring(3, 7) + "-" + res.substring(7)
}

