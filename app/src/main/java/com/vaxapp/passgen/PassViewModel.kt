package com.vaxapp.passgen

import android.app.Application
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


internal class PassViewModel(
    private val useCase: PasswordUseCase = PasswordUseCase(),
    application: Application
) : AndroidViewModel(application) {

    private val passwordLength = 12 //config by user
    private val clipboard: ClipboardManager?
    private var _passGenState = MutableStateFlow(PassGenState(mutableListOf("123"), false))
    val passGenState = _passGenState.asStateFlow()

    init {
        clipboard =
            application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    }

    fun addPassword() {
        val passwords = _passGenState.value.passwords
        _passGenState.value = PassGenState(passwords, true)
        viewModelScope.launch {
            // delay(2000)
            passwords.add(useCase.generatePassword(passwordLength))
            _passGenState.value = PassGenState(passwords, false)
        }
    }

    fun addToClipBoard(text: String) {
        val clip = ClipData.newPlainText(
            "pass", text
        ).apply {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2)
                description.extras = PersistableBundle().apply {
                    putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                }
        }

        clipboard?.setPrimaryClip(clip)
    }
}

internal data class PassGenState(val passwords: MutableList<String>, val loading: Boolean)
