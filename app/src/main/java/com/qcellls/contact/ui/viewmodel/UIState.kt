package com.qcellls.contact.ui.viewmodel

import com.qcellls.contact.data.model.Contact

data class UIState(
    val contacts: List<Contact> = emptyList(),
    val showOnlyEmployee: Boolean = false,
)