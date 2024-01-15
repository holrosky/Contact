package com.qcellls.contact.ui.repository

import com.qcellls.contact.data.model.Contact

interface ContactRepository {
    suspend fun getAllContact(): List<Contact>
    suspend fun getEmployeeContact(): List<Contact>
}