package com.qcellls.contact.composable

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qcellls.contact.model.Contact
import com.qcellls.contact.ui.theme.ContactTheme
import com.qcellls.contact.ui.theme.Purple40
import com.qcellls.contact.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel: ContactViewModel = hiltViewModel()
    val contactList by viewModel.contacts.observeAsState(emptyList())
    val searchQuery by viewModel.searchQuery.observeAsState("")
    val showOnlyEmployee by viewModel.showOnlyEmployee.observeAsState(false)
    val error by viewModel.error.observeAsState("")

    var showDialog by remember { mutableStateOf(false) }
    var selectedEmployee by remember { mutableStateOf(Contact("", "")) }
    var showProgressBar by remember { mutableStateOf(false) }

    val pullToRefreshState = rememberPullToRefreshState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        showProgressBar = true
        viewModel.getContact {
            showProgressBar = false
        }
    }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.getContact {
                pullToRefreshState.endRefresh()
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
            .padding(15.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize())
        {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
            ) {
                SearchBar(searchQuery) {
                    viewModel.updateSearchQuery(it)
                }

                ShowOnlyEmployeeCheckBox(showOnlyEmployee) {
                    viewModel.updateShowOnlyEmployeeToggle(it)
                }
            }

            if (error.isNotEmpty())
                ErrorScreen(error) {
                    showProgressBar = true
                    viewModel.getContact {
                        showProgressBar = false
                    }
                }
            else
                ContactList(contactList) { employee ->
                    selectedEmployee = employee
                    showDialog = true
                }
        }


        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullToRefreshState,
        )
    }

    if (showProgressBar) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .pointerInput(Unit) {}
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }

    if (showDialog) {
        ConfirmDialog(selectedEmployee) { isAccept ->
            if (isAccept) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${selectedEmployee.contact.replace("-", "")}")
                }
                context.startActivity(intent)
            }
            showDialog = false
        }
    }
}

@Composable
fun SearchBar(searchQuery: String, onValueChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onValueChange,
            placeholder = { Text(text = "이름 또는 전화번호로 검색", fontSize = 12.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple40,
                unfocusedBorderColor = Purple40,
            ),
            singleLine = true,
        )
    }
}

@Composable
fun ShowOnlyEmployeeCheckBox(showOnlyEmployee: Boolean, onClick: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
fun ContactList(employeeList: List<Contact>, onClick: (Contact) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(employeeList) { employee ->
            ContactItem(employee) {
                onClick(employee)
            }
        }
    }
}

@Composable
fun ContactItem(employee: Contact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onClick()
            }
    ) {
        Text(text = employee.name)
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = employee.contact)
    }
}

@Composable
fun ConfirmDialog(employee: Contact, isAccept: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { isAccept(false) },
        title = {
            Text(text = "전화를 거시겠어요?")
        },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = employee.name)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = employee.contact)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                isAccept(true)
            }) {
                Text(text = "네", color = Purple40, fontSize = 14.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = { isAccept(false) }) {
                Text(
                    text = "아니요",
                    color = Purple40,
                    fontSize = 14.sp
                )
            }
        }
    )
}

@Composable
fun ErrorScreen(errorMsg: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = errorMsg)
            Button(onClick = {
                onClick()
            }) {
                Text("새로고침")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeItemPreview() {
    ContactTheme {
        ContactItem(Contact("고태희", "010-6277-5750")) {}
    }
}