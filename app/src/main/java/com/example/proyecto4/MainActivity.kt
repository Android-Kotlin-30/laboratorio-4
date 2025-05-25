package com.example.proyecto4

// -------------------------------------------------------------
// “Enviar mi ubicación por WhatsApp”
// -------------------------------------------------------------

/* ---------- LIBRERÍAS / IMPORTS ---------- */
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.isGranted
import androidx.core.net.toUri


/* ---------- CONSTANTES ---------- */
private const val DEFAULT_MSG = "Estoy perdido, por favor encuéntrenme"

/* ---------- HELPERS ---------- */
private fun buildWhatsAppUri(phone: String, text: String) =
    "https://wa.me/${phone.trim()}?text=${Uri.encode(text)}".toUri()

/* ---------- UI ---------- */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SendLocationScreen(latitude: Double, longitude: Double) {
    val ctx = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var phone by remember { mutableStateOf("+507") }
    var message by remember { mutableStateOf(DEFAULT_MSG) }

    // Permiso de ubicación
    val locationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    if (!locationPermission.status.isGranted) {
        LaunchedEffect(Unit) { locationPermission.launchPermissionRequest() }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHost) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enviar mi ubicación", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Lat.: ${"%.5f".format(latitude)}  Lon.: ${"%.5f".format(longitude)}",
                fontSize = 14.sp
            )

            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                singleLine = true,
                label = { Text("Número (+507…)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Mensaje") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (phone.isBlank()) {
                        scope.launch { snackbarHost.showSnackbar("Ingrese un número") }
                        return@Button
                    }
                    val text =
                        "$message\nMi ubicación: https://maps.google.com/?q=$latitude,$longitude"
                    val uri = buildWhatsAppUri(phone, text)
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, uri))
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
            ) {
                Text("Enviar a WhatsApp")
            }
        }
    }
}

/* ---------- MAIN ACTIVITY ---------- */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Demo de coordenadas; cámbialas por las reales de tu servicio
        setContent {
            MaterialTheme {
                SendLocationScreen(latitude = 9.32756, longitude = -79.58518)
            }
        }
    }
}

/* ---------- PREVIEW ---------- */
@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    MaterialTheme {
        SendLocationScreen(latitude = 9.32756, longitude = -79.58518)
    }
}
