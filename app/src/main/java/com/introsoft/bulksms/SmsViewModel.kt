package com.introsoft.bulksms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SmsViewModel : ViewModel() {

    private val _recipientNames = mutableMapOf<String, String>()

    private val _isSending = MutableLiveData(false)
    val isSending: LiveData<Boolean> = _isSending

    private val _sentCount = MutableLiveData(0)
    val sentCount: LiveData<Int> = _sentCount

    private val _failedCount = MutableLiveData(0)
    val failedCount: LiveData<Int> = _failedCount

    private val _deliveredCount = MutableLiveData(0)
    val deliveredCount: LiveData<Int> = _deliveredCount

    private val _totalCount = MutableLiveData(0)
    val totalCount: LiveData<Int> = _totalCount

    private val _selectedSubscriptionId = MutableLiveData<Int?>(null)
    val selectedSubscriptionId: LiveData<Int?> = _selectedSubscriptionId

    val failedNumbers = LinkedHashSet<String>()

    fun setSelectedSubscriptionId(id: Int?) {
        _selectedSubscriptionId.value = id
    }

    fun setSending(sending: Boolean) {
        _isSending.value = sending
    }

    fun resetProgress(total: Int) {
        _sentCount.value = 0
        _failedCount.value = 0
        _deliveredCount.value = 0
        _totalCount.value = total
        failedNumbers.clear()
    }

    fun incrementSent() {
        _sentCount.value = (_sentCount.value ?: 0) + 1
    }

    fun incrementFailed() {
        _failedCount.value = (_failedCount.value ?: 0) + 1
    }

    fun incrementDelivered() {
        _deliveredCount.value = (_deliveredCount.value ?: 0) + 1
    }

    fun addRecipientNames(names: Map<String, String>) {
        _recipientNames.putAll(names)
    }

    fun getNameForPhone(phone: String): String? {
        return _recipientNames[phone]
    }
}