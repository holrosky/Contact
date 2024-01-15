package com.qcellls.contact.ui.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qcellls.contact.data.model.Contact
import com.qcellls.contact.ui.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val repository: ContactRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(UIState())
    val uiState = _uiState

    private var allContacts = emptyList<Contact>()
    private var employeeContacts = emptyList<Contact>()

    init {
        initData()
    }

    private fun initData() {
        refresh()
    }

    fun updateShowOnlyEmployeeToggle(isChecked: Boolean) {
        _uiState.value = _uiState.value.copy(
            contacts =
            if (isChecked) allContacts.toIntersectionContacts(employeeContacts)
            else allContacts,
            showOnlyEmployee = isChecked
        )
    }

    fun refresh() {
        viewModelScope.launch {
            allContacts = repository.getAllContact()
            employeeContacts = repository.getEmployeeContact()

            _uiState.value = _uiState.value.copy(
                contacts =
                if (_uiState.value.showOnlyEmployee) allContacts.toIntersectionContacts(
                    employeeContacts
                )
                else allContacts
            )
        }
    }
}

fun List<Contact>.toIntersectionContacts(
    comparedContacts: List<Contact>
): List<Contact> {
    val phoneNumberSet = hashSetOf<String>()

    comparedContacts.forEach {
        phoneNumberSet.add(it.phoneNumber)
    }

    return this.filter { contact ->
        contact.phoneNumber in phoneNumberSet
    }
}