package com.vaxapp.passgen

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


internal class PassViewModel(
    private val useCase: PasswordUseCase = PasswordUseCase()
) : ViewModel() {

    private val passwordLength = 12 //config by user
    private var _passGenState = MutableStateFlow(PassGenState(mutableListOf()))
    val passGenState = _passGenState.asStateFlow()

    fun addPassword() {
        val passwords = _passGenState.value.passwords
        _passGenState.value =
            PassGenState(passwords, loading = true, showToast = _passGenState.value.showToast)
        viewModelScope.launch {
            passwords.add(0, useCase.generatePassword(passwordLength))
            _passGenState.value =
                PassGenState(passwords, loading = false, showToast = _passGenState.value.showToast)
        }
    }

    fun onCardTapped(password: String, application: Context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            _passGenState.value = PassGenState(
                _passGenState.value.passwords,
                loading = passGenState.value.loading,
                showToast = true
            )
        }

        val clipboard =
            application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText(
            "pass", password
        ).apply {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2)
                description.extras = PersistableBundle().apply {
                    putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                }
        }
        clipboard?.setPrimaryClip(clip)
    }

    fun onToastShown() {
        _passGenState.value = PassGenState(
            _passGenState.value.passwords,
            loading = passGenState.value.loading,
            showToast = false
        )
    }
}

internal data class PassGenState(
    val passwords: MutableList<String>,
    val loading: Boolean = false,
    val showToast: Boolean = false
)
