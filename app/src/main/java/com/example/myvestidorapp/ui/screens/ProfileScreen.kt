package com.example.myvestidorapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myvestidorapp.MainViewModel
import com.example.myvestidorapp.ui.components.CameraOutline
import com.example.myvestidorapp.ui.components.CoutureBackground
import com.example.myvestidorapp.ui.components.PrimaryCoutureButton
import com.example.myvestidorapp.ui.components.dashedRoundedBorder
import com.example.myvestidorapp.ui.theme.RoseIcon
import com.example.myvestidorapp.ui.theme.RoseOutline

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onContinue: () -> Unit,
) {
    val userPhotoUri by viewModel.userPhotoUri.collectAsState()
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? -> uri?.let(viewModel::setUserPhoto) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CoutureBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Configura tu Perfil",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Sube una foto de perfil.\nRecomendamos que sea de pie, de\nfrente y en una postura natural.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))

        val previewShape = RoundedCornerShape(28.dp)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(previewShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                .dashedRoundedBorder(RoseOutline.copy(alpha = 0.8f), 28.dp, 1.5.dp, 5.dp)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center,
        ) {
            if (userPhotoUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context).data(userPhotoUri).build(),
                    ),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(22.dp)),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CameraOutline(modifier = Modifier.size(58.dp), color = RoseIcon)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "TOCA PARA AGREGAR FOTO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = RoseIcon,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        PrimaryCoutureButton(
            text = if (userPhotoUri == null) "SELECCIONAR FOTO" else "CAMBIAR FOTO",
            onClick = { imagePickerLauncher.launch("image/*") },
        )
        Spacer(Modifier.height(10.dp))
        PrimaryCoutureButton(
            text = "CONTINUAR",
            onClick = onContinue,
            enabled = userPhotoUri != null,
        )
        if (userPhotoUri == null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Debes agregar una foto para continuar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }
    }
}
