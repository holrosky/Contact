package com.qcellls.contact.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qcellls.contact.model.Contact
import com.qcellls.contact.repository.ContactRepository
import com.qcellls.contact.util.toFilteredContactsBySearchQuery
import com.qcellls.contact.util.toFilteredOnlyEmployeeContacts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ContactViewModel @Inject constructor(
    private val repository: ContactRepository
) : ViewModel() {

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: LiveData<String> = _searchQuery.asLiveData()

    private val _showOnlyEmployee = MutableLiveData(false)
    val showOnlyEmployee: LiveData<Boolean> = _showOnlyEmployee

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    private var _originalContacts: List<Contact> = listOf()
    private var _filterJob: Job? = null
    private var _fetchContactJob: Job? = null

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .collect {
                    applyFilter()
                }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun updateShowOnlyEmployeeToggle(isChecked: Boolean) {
        _showOnlyEmployee.value = isChecked
        applyFilter()
    }

    // Business logic
    fun getContact(onComplete: () -> Unit) {
        _fetchContactJob?.cancel()

        _fetchContactJob = viewModelScope.launch {
            try {
                _originalContacts = repository.getContact()
                applyFilter()
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "연락처 로드 중 오류 발생: ${e.message}"
            }
            onComplete()
        }
    }

    // Business logic
    private fun applyFilter() {
        _filterJob?.cancel()

        _filterJob = viewModelScope.launch {
            _contacts.value =
                _originalContacts.toFilteredOnlyEmployeeContacts(_showOnlyEmployee.value!!)
                    .toFilteredContactsBySearchQuery(_searchQuery.value)
        }
    }
}


