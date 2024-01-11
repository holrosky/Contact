package com.qcellls.contact.repository

import android.content.Context
import android.provider.ContactsContract
import com.qcellls.contact.model.Contact
import com.qcellls.contact.util.toPhoneNumberFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val context: Context
) : ContactRepository {
    override suspend fun getContact(): List<Contact> = withContext(Dispatchers.IO) {
        val employees = mutableListOf<Contact>()

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val contactIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (nameIndex != -1 && contactIndex != -1)
                while (it.moveToNext()) {
                    val name = it.getString(nameIndex)
                    val contact = it.getString(contactIndex)
                    employees.add(Contact(name, contact.toPhoneNumberFormat()))
                }

        }
        delay(2000)
        employees
    }
}