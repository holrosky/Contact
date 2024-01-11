package com.qcellls.contact.util

import com.qcellls.contact.model.Contact


fun String.toPhoneNumberFormat(): String {
    val res = this.replace("+82", "0").replace("-", "")
    return res.substring(0, 3) + "-" + res.substring(3, 7) + "-" + res.substring(7)
}

fun List<Contact>.toFilteredContactsBySearchQuery(searchQuery: String) =
    when {
        searchQuery.isNotEmpty() -> this.filter {
            it.name.contains(searchQuery, ignoreCase = true)
                    || it.contact.replace("-", "").contains(searchQuery)
        }

        else ->
            this
    }

fun List<Contact>.toFilteredOnlyEmployeeContacts(showOnlyEmployee: Boolean): List<Contact> {
    val filterNumbers = hashSetOf(
        "010-2359-2261",
        "010-9136-8442",
        "010-5838-2086",
        "010-6613-5462",
        "010-3719-9418",
        "010-5128-7640",
        "010-3264-7168",
        "010-9775-7981",
        "010-9656-5460",
        "010-4364-0905",
        "010-5144-1667"
    )
    return when {
        showOnlyEmployee -> this.filter { employee ->
            employee.contact in filterNumbers
        }

        else ->
            this
    }
}