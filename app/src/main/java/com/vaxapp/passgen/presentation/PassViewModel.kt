package com.vaxapp.passgen.presentation

import android.app.Application
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vaxapp.passgen.presentation.PassGenState.Success
import com.vaxapp.passgen.presentation.model.Password
import com.vaxapp.passgen.repository.DatabaseRepository
import com.vaxapp.passgen.usecases.AddPassword
import com.vaxapp.passgen.usecases.CreatePasswordUseCase
import com.vaxapp.passgen.usecases.GetPasswords
import com.vaxapp.passgen.usecases.UpdatePassword
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


internal class PassViewModel(
    private val createPasswordUseCase: CreatePasswordUseCase = CreatePasswordUseCase(),
    application: Application
) : AndroidViewModel(application) {

    private val passwordLength = 12 //config by user

    private val addPassword: AddPassword
    private val updatePassword: UpdatePassword
    private val getPasswords: GetPasswords

    val passGenState: StateFlow<PassGenState>
    private val _showToast = MutableStateFlow(false)
    val showToast = _showToast.asStateFlow()

    init {
        val repository = DatabaseRepository(application)
        addPassword = AddPassword(repository)
        getPasswords = GetPasswords(repository)
        updatePassword = UpdatePassword(repository)

        passGenState = getPasswords.invoke().map(::Success).catch { Error(it) }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), PassGenState.Loading
        )
    }

    fun addPassword() {
        viewModelScope.launch {
            val password =
                Password(System.currentTimeMillis(), createPasswordUseCase.invoke(passwordLength))
            addPassword.invoke(password)
        }
    }

    fun onCardTapped(password: Password, application: Context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            _showToast.value = true
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
        _showToast.value = false
    }

    fun updatePasswordVisibility(password: Password) {
        viewModelScope.launch {
            password.visible = !password.visible
            updatePassword.invoke(password)
            if (password.visible) {
                delay(30000)
                //revert back to invisible after 30s
                password.visible = !password.visible
                updatePassword.invoke(password)
            }
        }
    }
}

