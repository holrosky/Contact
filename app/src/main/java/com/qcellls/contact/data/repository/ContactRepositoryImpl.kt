package com.qcellls.contact.data.repository

import android.content.Context
import android.provider.ContactsContract
import com.qcellls.contact.data.model.Contact
import com.qcellls.contact.ui.repository.ContactRepository
import com.qcellls.contact.ui.util.isPhoneNumber
import com.qcellls.contact.ui.util.toPhoneNumberFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val context: Context
) : ContactRepository {
    override suspend fun getAllContact(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()

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

                    if (contact.isPhoneNumber())
                        contacts.add(Contact(name, contact.toPhoneNumberFormat()))
                }

        }
        contacts
    }


    override suspend fun getEmployeeContact(): List<Contact> = withContext(Dispatchers.IO) {
        listOf(
            Contact("test", "010-2359-2261"),
            Contact("test", "010-9136-8442"),
            Contact("test", "010-5838-2086"),
            Contact("test", "010-6613-5462"),
            Contact("test", "010-3719-9418"),
            Contact("test", "010-5128-7640"),
            Contact("test", "010-3264-7168"),
            Contact("test", "010-9775-7981"),
            Contact("test", "010-9656-5460"),
            Contact("test", "010-4364-0905"),
            Contact("test", "010-5144-1667")
        )
    }
}