package com.introsoft.bulksms

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.introsoft.bulksms.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.time.Duration.Companion.milliseconds

data class Recipient(val name: String?, val phone: String)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: SmsViewModel by viewModels()

    private var delayMs = DEFAULT_DELAY_MS
    private val smsPermissionRequestCode = SMS_PERMISSION_REQUEST_CODE

    private val csvPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { parseCsv(it) }
    }

    private val contactsPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            showContactPicker()
        } else {
            Toast.makeText(this, getString(R.string.contacts_permission_required), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            binding.bottomContainer.setPadding(
                binding.bottomContainer.paddingLeft,
                binding.bottomContainer.paddingTop,
                binding.bottomContainer.paddingRight,
                if (ime.bottom > 0) ime.bottom else systemBars.bottom,
            )
            insets
        }

        setupUI()
        observeViewModel()
        detectSims()
    }

    private fun detectSims() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) return

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val subscriptionManager = getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList

        if (activeSubscriptions != null && (activeSubscriptions.size > 1)) {
            binding.simSelectionLayout.visibility = View.VISIBLE
            binding.simChipGroup.removeAllViews()

            activeSubscriptions.forEachIndexed { index, info ->
                val chip = Chip(this).apply {
                    id = View.generateViewId()
                    text = getString(R.string.sim_slot_format, index + 1, info.displayName ?: info.carrierName ?: "Unknown")
                    isCheckable = true
                    tag = info.subscriptionId
                }
                binding.simChipGroup.addView(chip)
                
                // Select first one by default if nothing selected
                if (index == 0 && viewModel.selectedSubscriptionId.value == null) {
                    chip.isChecked = true
                    viewModel.setSelectedSubscriptionId(info.subscriptionId)
                } else if (viewModel.selectedSubscriptionId.value == info.subscriptionId) {
                    chip.isChecked = true
                }
            }

            binding.simChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                if (checkedIds.isNotEmpty()) {
                    val selectedChip = group.findViewById<Chip>(checkedIds[0])
                    viewModel.setSelectedSubscriptionId(selectedChip.tag as Int)
                }
            }
        } else {
            binding.simSelectionLayout.visibility = View.GONE
            viewModel.setSelectedSubscriptionId(null)
        }
    }

    private fun setupUI() {
        binding.delaySlider.value = delayMs.toFloat()
        binding.delayLabel.text = getString(R.string.delay_format, delayMs)

        binding.delaySlider.addOnChangeListener { _, value, _ ->
            delayMs = value.toLong()
            binding.delayLabel.text = getString(R.string.delay_format, delayMs)
        }

        binding.importCsvButton.setOnClickListener {
            csvPickerLauncher.launch("text/*")
        }

        binding.selectContactsButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                showContactPicker()
            } else {
                contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }

        binding.sendButton.setOnClickListener { onSendClicked() }
        binding.cancelButton.setOnClickListener {
            viewModel.setSending(sending = false)
            binding.statusText.text = getString(R.string.cancelling)
        }

        binding.messageInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateMessagePreview(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.isSending.observe(this) { isSending ->
            binding.sendButton.isEnabled = !isSending
            binding.cancelButton.visibility = if (isSending) View.VISIBLE else View.GONE
            binding.progressBar.visibility = if (isSending) View.VISIBLE else View.GONE
            binding.importCsvButton.isEnabled = !isSending
            binding.selectContactsButton.isEnabled = !isSending
            binding.messageInput.isEnabled = !isSending
            binding.numbersInput.isEnabled = !isSending
        }

        viewModel.sentCount.observe(this) { updateStatusText() }
        viewModel.failedCount.observe(this) { updateStatusText() }
        viewModel.deliveredCount.observe(this) { updateStatusText() }
        viewModel.totalCount.observe(this) { updateStatusText() }
    }

    private fun updateMessagePreview(message: String) {
        if (message.isEmpty()) {
            binding.previewCard.visibility = View.GONE
        } else {
            binding.previewCard.visibility = View.VISIBLE
            binding.messagePreview.text = getString(
                R.string.preview_label,
                getString(R.string.preview_default_name),
                message
            )
        }
    }

    private fun formatPhoneNumber(phone: String): String {
        val cleaned = phone.replace(PHONE_CLEAN_REGEX, "").trim()
        return if (cleaned.isNotEmpty() && !cleaned.startsWith("0") && !cleaned.startsWith("+")) {
            "0$cleaned"
        } else {
            cleaned
        }
    }

    private fun parseCsv(uri: Uri) {
        lifecycleScope.launch {
            try {
                val recipients = withContext(Dispatchers.IO) {
                    val list = mutableListOf<Recipient>()
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            var line: String? = reader.readLine()
                            while (line != null) {
                                val parts = line.split(CSV_REGEX)
                                if (parts.size >= 2) {
                                    val name = parts[0].trim()
                                    val phone = formatPhoneNumber(parts[1])
                                    if (phone.isNotEmpty()) list.add(Recipient(name, phone))
                                } else if (parts.size == 1) {
                                    val phone = formatPhoneNumber(parts[0])
                                    if (phone.isNotEmpty()) list.add(Recipient(null, phone))
                                }
                                line = reader.readLine()
                            }
                        }
                    }
                    list
                }
                
                if (recipients.isNotEmpty()) {
                    val namesMap = recipients.asSequence()
                    .filter { it.name != null }
                    .associateBy({ it.phone }, { it.name!! })
                viewModel.addRecipientNames(namesMap)
                    
                    val currentText = binding.numbersInput.text.toString()
                    val newNumbers = recipients.map { it.phone }.distinct().joinToString("\n")
                    binding.numbersInput.setText(if (currentText.isEmpty()) newNumbers else "$currentText\n$newNumbers")
                    
                    Toast.makeText(this@MainActivity, getString(R.string.import_success, recipients.size), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, getString(R.string.import_failed, e.message ?: ""), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showContactPicker() {
        lifecycleScope.launch {
            val allContacts = withContext(Dispatchers.IO) {
                val list = mutableListOf<ContactAdapter.SelectableContact>()
                val cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )

                cursor?.use {
                    val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val phoneIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (it.moveToNext()) {
                        val name = it.getString(nameIdx)
                        val rawPhone = it.getString(phoneIdx).replace(PHONE_CLEAN_REGEX, "")
                        val phone = formatPhoneNumber(rawPhone)
                        if (phone.isNotEmpty()) {
                            list.add(ContactAdapter.SelectableContact(name, phone))
                        }
                    }
                }
                list.distinctBy { it.phone }
            }

            if (allContacts.isEmpty()) {
                Toast.makeText(this@MainActivity, getString(R.string.no_contacts_found), Toast.LENGTH_SHORT).show()
                return@launch
            }

            val dialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_contact_picker, null)
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.contactsRecyclerView)
            val searchInput = dialogView.findViewById<EditText>(R.id.contactSearchInput)

            val adapter = ContactAdapter { _ -> }

            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter
            adapter.submitList(allContacts)

            searchInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val filtered = allContacts.filter {
                        it.name.contains(s ?: "", ignoreCase = true) || it.phone.contains(s ?: "", ignoreCase = true)
                    }
                    adapter.submitList(filtered)
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle(R.string.select_contacts_title)
                .setView(dialogView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.select_contacts) { _, _ ->
                    val selected = adapter.getSelectedContacts()
                    if (selected.isNotEmpty()) {
                        val namesMap = selected.associate { it.phone to it.name }
                        viewModel.addRecipientNames(namesMap)

                        val currentText = binding.numbersInput.text.toString()
                        val newNumbers = selected.joinToString("\n") { it.phone }
                        binding.numbersInput.setText(if (currentText.isEmpty()) newNumbers else "$currentText\n$newNumbers")
                        Toast.makeText(this@MainActivity, getString(R.string.import_success, selected.size), Toast.LENGTH_SHORT).show()
                    }
                }
                .show()
        }
    }

    private fun onSendClicked() {
        val rawText = binding.numbersInput.text.toString()
        val allLines = rawText.lines().filter { it.trim().isNotEmpty() }
        val recipients = parseRecipients(rawText)
        val message = binding.messageInput.text.toString().trim()

        if (recipients.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_numbers), Toast.LENGTH_SHORT).show()
            return
        }
        if (message.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_message), Toast.LENGTH_SHORT).show()
            return
        }

        if (allLines.size > recipients.size) {
            val skipped = allLines.size - recipients.size
            Toast.makeText(
                this,
                getString(R.string.recipients_breakdown, allLines.size, skipped, recipients.size),
                Toast.LENGTH_LONG
            ).show()
        }

        if (!hasSmsPermission()) {
            requestSmsPermission()
            return
        }

        confirmAndSend(recipients, message)
    }

    private fun parseRecipients(raw: String): List<Recipient> {
        return raw.lines()
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { line ->
                // Split by first comma or semicolon to separate Name from Number
                val parts = line.split(CSV_REGEX, 2)
                if (parts.size == 2) {
                    val name = parts[0].trim()
                    val phone = formatPhoneNumber(parts[1].trim())
                    if (phone.isNotEmpty()) Recipient(name, phone) else null
                } else {
                    val phone = formatPhoneNumber(parts[0].trim())
                    if (phone.isNotEmpty()) {
                        Recipient(viewModel.getNameForPhone(phone), phone)
                    } else null
                }
            }
            .filterNotNull()
            .distinctBy { it.phone }
            .toList()
    }

    private fun hasSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE),
            smsPermissionRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == smsPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                detectSims()
                onSendClicked()
            } else {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun confirmAndSend(recipients: List<Recipient>, message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_title))
            .setMessage(getString(R.string.confirm_message, recipients.size))
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.send_button) { _, _ -> startSending(recipients, message) }
            .show()
    }

    private fun startSending(recipients: List<Recipient>, message: String) {
        val total = recipients.size
        viewModel.resetProgress(total)
        viewModel.setSending(true)

        binding.progressBar.max = total
        binding.progressBar.progress = 0

        lifecycleScope.launch {
            val smsManager = getSmsManager()

            for (recipient in recipients) {
                if (viewModel.isSending.value != true) break

                val personalizedMessage = if (!recipient.name.isNullOrEmpty()) {
                    getString(R.string.preview_label, recipient.name, message)
                        .replace("Preview: ", "")
                } else {
                    message
                }

                sendSingleSms(smsManager, recipient.phone, personalizedMessage)
                delay(delayMs.milliseconds)
            }
            
            // Wait a bit for final results to trickle in if stopped or finished
            delay(1000.milliseconds)
            if (viewModel.isSending.value == true) {
                // If it wasn't cancelled, check if we're done
                checkFinalStatus(forceFinish = true)
            }
        }
    }

    private fun checkFinalStatus(forceFinish: Boolean = false) {
        val sent = viewModel.sentCount.value ?: 0
        val failed = viewModel.failedCount.value ?: 0
        val total = viewModel.totalCount.value ?: 0

        if (forceFinish || (sent + failed >= total)) {
            viewModel.setSending(sending = false)
            binding.statusText.text = if (viewModel.failedNumbers.isNotEmpty()) {
                getString(R.string.sending_done, sent, failed, viewModel.failedNumbers.joinToString(", "))
            } else {
                getString(R.string.sending_done_success, sent, failed)
            }
        }
    }

    private fun getSmsManager(): SmsManager {
        val subId = viewModel.selectedSubscriptionId.value
        return if (subId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getSystemService(SmsManager::class.java).createForSubscriptionId(subId)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getSmsManagerForSubscriptionId(subId)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
        }
    }

    private fun sendSingleSms(smsManager: SmsManager, number: String, message: String) {
        val parts = try {
            smsManager.divideMessage(message)
        } catch (_: Exception) {
            viewModel.incrementFailed()
            viewModel.failedNumbers.add(number)
            checkFinalStatus()
            return
        }
        
        val numParts = parts.size
        val timestamp = System.currentTimeMillis()
        val randomId = (0..MAX_RANDOM_ID).random()
        val sentAction = "SMS_SENT_${timestamp}_$randomId"
        val deliveryAction = "SMS_DELIVERED_${timestamp}_$randomId"

        val sentReceiver = object : BroadcastReceiver() {
            private var partsResponded = 0
            private var anyPartFailed = false

            override fun onReceive(context: Context?, intent: Intent?) {
                partsResponded++
                if (resultCode != RESULT_OK) {
                    anyPartFailed = true
                }

                if (partsResponded == numParts) {
                    if (anyPartFailed) {
                        viewModel.incrementFailed()
                        viewModel.failedNumbers.add(number)
                        val errorMessage = getSmsErrorMessage(resultCode)
                        Toast.makeText(this@MainActivity, getString(R.string.sms_failed_toast, number, errorMessage), Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.incrementSent()
                        Toast.makeText(this@MainActivity, getString(R.string.sms_sent_toast, number), Toast.LENGTH_SHORT).show()
                    }
                    checkFinalStatus()
                    try {
                        unregisterReceiver(this)
                    } catch (_: Exception) {}
                }
            }
        }

        val deliveryReceiver = object : BroadcastReceiver() {
            private var partsDelivered = 0
            override fun onReceive(context: Context?, intent: Intent?) {
                partsDelivered++
                if (partsDelivered == numParts) {
                    viewModel.incrementDelivered()
                    Toast.makeText(this@MainActivity, getString(R.string.sms_delivered_toast, number), Toast.LENGTH_SHORT).show()
                    try {
                        unregisterReceiver(this)
                    } catch (_: Exception) {}
                }
            }
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_UPDATE_CURRENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(sentReceiver, IntentFilter(sentAction), RECEIVER_NOT_EXPORTED)
            registerReceiver(deliveryReceiver, IntentFilter(deliveryAction), RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(sentReceiver, IntentFilter(sentAction))
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(deliveryReceiver, IntentFilter(deliveryAction))
        }

        val sentIntents = ArrayList<PendingIntent>()
        val deliveryIntents = ArrayList<PendingIntent>()

        for (i in 0 until numParts) {
            sentIntents.add(PendingIntent.getBroadcast(this, i + randomId, Intent(sentAction), flags))
            deliveryIntents.add(PendingIntent.getBroadcast(this, i + randomId, Intent(deliveryAction), flags))
        }

        try {
            if (numParts > 1) {
                smsManager.sendMultipartTextMessage(number, null, parts, sentIntents, deliveryIntents)
            } else {
                smsManager.sendTextMessage(number, null, message, sentIntents[0], deliveryIntents[0])
            }
        } catch (_: Exception) {
            viewModel.incrementFailed()
            viewModel.failedNumbers.add(number)
            checkFinalStatus()
            try {
                unregisterReceiver(sentReceiver)
                unregisterReceiver(deliveryReceiver)
            } catch (_: Exception) {}
        }
    }

    private fun getSmsErrorMessage(resultCode: Int): String {
        return when (resultCode) {
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> getString(R.string.sms_error_generic)
            SmsManager.RESULT_ERROR_RADIO_OFF -> getString(R.string.sms_error_radio_off)
            SmsManager.RESULT_ERROR_NO_SERVICE -> getString(R.string.sms_error_no_service)
            SmsManager.RESULT_ERROR_NULL_PDU -> getString(R.string.sms_error_null_pdu)
            else -> "Error code: $resultCode"
        }
    }

    private fun updateStatusText() {
        val sent = viewModel.sentCount.value ?: 0
        val delivered = viewModel.deliveredCount.value ?: 0
        val failed = viewModel.failedCount.value ?: 0
        val total = viewModel.totalCount.value ?: 0
        
        binding.progressBar.progress = sent + failed
        binding.statusText.text = getString(R.string.status_format, sent, delivered, failed, total - sent - failed)
    }

    companion object {
        private const val DEFAULT_DELAY_MS = 1500L
        private const val SMS_PERMISSION_REQUEST_CODE = 100
        private const val MAX_RANDOM_ID = 1000000
        private val PHONE_CLEAN_REGEX = Regex("[\\s\\-()]")
        private val CSV_REGEX = Regex("[,;]")
    }
}
