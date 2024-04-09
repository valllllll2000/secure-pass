package com.vaxapp.passgen.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.vaxapp.passgen.MainActivity
import com.vaxapp.passgen.ui.theme.PassGenTheme
import java.util.concurrent.Executor

class LoginActivity : FragmentActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.e("LoginActivity", "Auth error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d("LoginActivity", "Auth success: ${result.authenticationType}")
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.e("LoginActivity", "Auth failed")
            }
        })
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login to access saved passwords")
            .setSubtitle("Login using your biometric credential")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
        setContent {
            PassGenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Login {
                        biometricPrompt.authenticate(promptInfo)
                    }
                }
            }
        }
    }
}

@Composable
fun Login(onLoginClicked: () -> Unit = {}) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Button(onClick = { onLoginClicked() }, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = "Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    PassGenTheme {
        Login()
    }
}