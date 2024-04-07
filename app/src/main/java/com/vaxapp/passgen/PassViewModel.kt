package com.vaxapp.passgen

import android.app.Application
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vaxapp.passgen.repository.DatabaseRepository
import com.vaxapp.passgen.usecases.AddPassword
import com.vaxapp.passgen.usecases.CreatePasswordUseCase
import com.vaxapp.passgen.usecases.GetPasswords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


internal class PassViewModel(
    private val createPasswordUseCase: CreatePasswordUseCase = CreatePasswordUseCase(),
    application: Application
) : AndroidViewModel(application) {

    private val passwordLength = 12 //config by user
    private var _passGenState = MutableStateFlow(PassGenState(mutableListOf()))
    val passGenState = _passGenState.asStateFlow()
    private val addPassword: AddPassword
    private val getPasswords: GetPasswords

    init {
        val repository = DatabaseRepository(application)
        addPassword = AddPassword(repository)
        getPasswords = GetPasswords(repository)

        _passGenState.value =
            PassGenState(mutableListOf(), loading = true, showToast = _passGenState.value.showToast)
        viewModelScope.launch {
            val passwords = getPasswords.invoke()
            _passGenState.value =
                PassGenState(
                    passwords.toMutableList(),
                    loading = false,
                    showToast = _passGenState.value.showToast
                )
        }
    }

    fun addPassword() {
        val passwords = _passGenState.value.passwords
        _passGenState.value =
            PassGenState(passwords, loading = true, showToast = _passGenState.value.showToast)
        viewModelScope.launch {
            val password =
                Password(System.currentTimeMillis(), createPasswordUseCase.invoke(passwordLength))
            addPassword.invoke(password)
            _passGenState.value =
                PassGenState(
                    getPasswords.invoke().toMutableList(),
                    loading = false,
                    showToast = _passGenState.value.showToast
                )
        }
    }

    fun onCardTapped(password: Password, application: Context) {
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
            "pass", password.password
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

    fun updatePasswordVisibility(password: Password) {
        password.visible = !password.visible
        val passwords = _passGenState.value.passwords
        _passGenState.value = PassGenState(
            passwords,
            loading = passGenState.value.loading,
            showToast = passGenState.value.showToast
        )
    }
}

internal data class PassGenState(
    val passwords: MutableList<Password>,
    val loading: Boolean = false,
    val showToast: Boolean = false
)
