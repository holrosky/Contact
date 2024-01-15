package com.qcellls.contact.repository

import com.qcellls.contact.model.Contact

interface ContactRepository {
    suspend fun getContact(): List<Contact>
}