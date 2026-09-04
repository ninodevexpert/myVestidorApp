package com.example.myvestidorapp.ui.screens

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myvestidorapp.MainViewModel
import com.example.myvestidorapp.ui.components.CoutureBackground
import com.example.myvestidorapp.ui.components.PrimaryCoutureButton
import com.example.myvestidorapp.ui.components.dashedRoundedBorder
import com.example.myvestidorapp.ui.theme.CyanSpark
import com.example.myvestidorapp.ui.theme.ElectricPink
import com.example.myvestidorapp.ui.theme.RoseOutline
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditorScreen(viewModel: MainViewModel) {
    val userPhotoUri by viewModel.userPhotoUri.collectAsState()
    val clothingImages by viewModel.clothingImages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedImageUri by viewModel.generatedImageUri.collectAsState()
    val context = LocalContext.current
    val cameraUri = remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null && clothingImages.size < 3) viewModel.addClothingImage(uri)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) cameraUri.value?.let(viewModel::addClothingImage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CoutureBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = "MiVestidor",
            color = ElectricPink,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(36.dp))
        Text(
            text = "Vestidor Virtual",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "Selecciona hasta 3 prendas de vestir para\ngenerar tu look",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(34.dp))

        userPhotoUri?.let { photo ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tu foto:", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(18.dp))
                Image(
                    painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(photo).build()),
                    contentDescription = "Tu foto",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .border(1.dp, RoseOutline, RoundedCornerShape(9.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(Modifier.height(34.dp))
        }

        Text(
            text = "Prendas seleccionadas (${clothingImages.size}/3)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(clothingImages, key = { it.toString() }) { uri ->
                GarmentCard(uri = uri, context = context, onRemove = { viewModel.removeClothingImage(uri) })
            }
            if (clothingImages.size < 3) {
                item {
                    AddGarmentCard(enabled = !isGenerating) { galleryLauncher.launch("image/*") }
                }
            }
        }

        Spacer(Modifier.height(38.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SecondaryActionButton(
                text = "Cámara",
                modifier = Modifier.weight(1f),
                enabled = clothingImages.size < 3 && !isGenerating,
            ) {
                createImageUri(context).also {
                    cameraUri.value = it
                    cameraLauncher.launch(it)
                }
            }
            SecondaryActionButton(
                text = "Galería",
                modifier = Modifier.weight(1f),
                enabled = clothingImages.size < 3 && !isGenerating,
            ) { galleryLauncher.launch("image/*") }
        }
        Spacer(Modifier.height(14.dp))
        PrimaryCoutureButton(
            text = if (isGenerating) "Generando..." else "Generar Imagen",
            leadingText = if (isGenerating) null else "✦",
            loading = isGenerating,
            enabled = clothingImages.isNotEmpty() && !isGenerating,
            onClick = viewModel::generateImage,
        )

        if (clothingImages.isEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Selecciona al menos una prenda para generar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        generatedImageUri?.let { result ->
            Spacer(Modifier.height(42.dp))
            Text("Resultado:", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.62f))
                    .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(result).build()),
                    contentDescription = "Imagen generada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GarmentCard(uri: Uri, context: Context, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.72f))
            .border(1.5.dp, RoseOutline, RoundedCornerShape(10.dp)),
    ) {
        Image(
            painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(uri).build()),
            contentDescription = "Prenda seleccionada",
            modifier = Modifier.fillMaxSize().padding(5.dp).clip(RoundedCornerShape(7.dp)),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(3.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(Color(0xFFC93845))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Text("×", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AddGarmentCard(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.3f))
            .dashedRoundedBorder(RoseOutline, 10.dp, 1.dp, 4.dp)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("+", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Agregar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    modifier: Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = if (enabled) 0.72f else 0.4f))
            .border(1.5.dp, RoseOutline, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (enabled) CyanSpark else CyanSpark.copy(alpha = 0.4f),
        )
    }
}

private fun createImageUri(context: Context): Uri {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
