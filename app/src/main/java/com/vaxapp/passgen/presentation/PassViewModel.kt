package com.vaxapp.passgen.presentation

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vaxapp.passgen.domain.usecases.AddPassword
import com.vaxapp.passgen.domain.usecases.CreatePasswordUseCase
import com.vaxapp.passgen.domain.usecases.GetPasswords
import com.vaxapp.passgen.domain.usecases.UpdatePassword
import com.vaxapp.passgen.presentation.PassGenState.Success
import com.vaxapp.passgen.presentation.model.Password
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class PassViewModel @Inject constructor(
    private val createPasswordUseCase: CreatePasswordUseCase,
    private val addPassword: AddPassword,
    private val updatePassword: UpdatePassword,
    getPasswords: GetPasswords,
) : ViewModel() {

    private val passwordLength = 12 //config by user

    val passGenState: StateFlow<PassGenState> =
        getPasswords.invoke().map(::Success).catch { Error(it) }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), PassGenState.Loading
        )

    private val _showToast = MutableStateFlow(false)
    val showToast = _showToast.asStateFlow()

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
            val copy = password.copy(visible = !password.visible)
            Log.d("PassViewModel", "will set password to: " + copy.visible)
            updatePassword.invoke(copy)
            if (copy.visible) {
                delay(30000)
                //revert back to invisible after 30s
                copy.visible = false
                Log.d("PassViewModel", "will set password back to: " + password.visible)
                updatePassword.invoke(copy)
            }
        }
    }
}
