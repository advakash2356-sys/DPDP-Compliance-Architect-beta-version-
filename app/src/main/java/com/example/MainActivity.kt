package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AuthState
import com.example.ui.ComplianceViewModel
import com.example.ui.screens.AccessDeniedScreen
import com.example.ui.screens.MainComplianceScreen
import com.example.ui.screens.PrivateLoginScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val viewModel = ViewModelProvider(this)[ComplianceViewModel::class.java]
    
    setContent {
      val textSize by viewModel.textSizeSetting.collectAsState()
      val authState by viewModel.authState.collectAsState()

      MyApplicationTheme(textSizeSetting = textSize) {
        when (val state = authState) {
          is AuthState.Unauthenticated, is AuthState.Authenticating -> {
            PrivateLoginScreen(
              viewModel = viewModel,
              modifier = Modifier.fillMaxSize()
            )
          }
          is AuthState.AccessDenied -> {
            AccessDeniedScreen(
              attemptedEmail = state.attemptedEmail,
              reasonMessage = state.reason,
              viewModel = viewModel,
              modifier = Modifier.fillMaxSize()
            )
          }
          is AuthState.Authorized -> {
            MainComplianceScreen(
              viewModel = viewModel,
              modifier = Modifier.fillMaxSize()
            )
          }
        }
      }
    }
  }
}