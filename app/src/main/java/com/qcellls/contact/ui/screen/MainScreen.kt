package com.qcellls.contact.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qcellls.contact.data.model.Contact
import com.qcellls.contact.ui.viewmodel.ContactViewModel

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel: ContactViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxSize()
    )
    {
        RefreshButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.refresh()
            }
        )
        ShowOnlyEmployeeCheckBox(
            modifier = Modifier.fillMaxWidth(),
            showOnlyEmployee = uiState.showOnlyEmployee,
            onClick = {
                viewModel.updateShowOnlyEmployeeToggle(it)
            }
        )

        ContactList(
            modifier = Modifier.fillMaxSize(),
            contactList = uiState.contacts,
            onClick = { contact ->
                dialNumber(context, contact.phoneNumber)
            }
        )
    }
}

@Composable
fun RefreshButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onClick) {
            Text(text = "새로고침")
        }
    }
}

@Composable
fun ShowOnlyEmployeeCheckBox(
    showOnlyEmployee: Boolean,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(text = "직원만 보기")
        Checkbox(
            checked = showOnlyEmployee,
            onCheckedChange = onClick
        )
    }
}


@Composable
fun ContactList(
    contactList: List<Contact>,
    onClick: (Contact) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(contactList) { contact ->
            ContactItem(
                contact = contact,
                onClick = {
                    onClick(contact)
                }
            )
        }
    }
}

@Composable
fun ContactItem(contact: Contact, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onClick()
            }
    ) {
        Text(text = contact.name)
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = contact.phoneNumber)
    }
}

fun dialNumber(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:${phoneNumber.replace("-", "")}")
    }
    context.startActivity(intent)
}