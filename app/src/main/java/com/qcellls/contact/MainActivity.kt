package com.qcellls.contact

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.qcellls.contact.composable.MainScreen
import com.qcellls.contact.ui.theme.ContactTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ContactTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RequestContactsPermissionScreen()
                }
            }
        }
    }
}

@Composable
fun RequestContactsPermissionScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    var hasContactsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            hasContactsPermission = true
        } else {
            if (shouldShowPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {
                showPermissionDeniedDialog = true
            } else {
                openAppSettings(context)
            }
        }
    }

    if (hasContactsPermission) {
        MainScreen()
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("연락처를 가져오기 위해 연락처 접근 권한이 필요합니다.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }) {
                Text("설정하기")
            }
        }
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            title = { Text("권한 요청") },
            text = { Text("정상적인 실행을 위해 요청을 수락해주세요!") },
            confirmButton = {
                Button(onClick = {
                    showPermissionDeniedDialog = false
                }
                ) {
                    Text("확인")
                }
            }
        )
    }
}

fun shouldShowPermissionRationale(activity: Activity?, permission: String): Boolean {
    return if (activity is ComponentActivity) {
        activity.shouldShowRequestPermissionRationale(permission)
    } else false
}

fun openAppSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}