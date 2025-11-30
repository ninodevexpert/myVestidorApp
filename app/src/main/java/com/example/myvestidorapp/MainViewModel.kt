package com.example.myvestidorapp

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myvestidorapp.utils.ImageUtils
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _userPhotoUri = MutableStateFlow<Uri?>(null)
    val userPhotoUri: StateFlow<Uri?> = _userPhotoUri.asStateFlow()
    
    private val _clothingImages = MutableStateFlow<List<Uri>>(emptyList())
    val clothingImages: StateFlow<List<Uri>> = _clothingImages.asStateFlow()
    
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    
    private val _generatedImageUri = MutableStateFlow<Uri?>(null)
    val generatedImageUri: StateFlow<Uri?> = _generatedImageUri.asStateFlow()
    
    fun setUserPhoto(uri: Uri?) {
        _userPhotoUri.value = uri
    }
    
    fun addClothingImage(uri: Uri) {
        val current = _clothingImages.value
        if (current.size < 3) {
            _clothingImages.value = current + uri
        }
    }
    
    fun removeClothingImage(uri: Uri) {
        _clothingImages.value = _clothingImages.value.filter { it != uri }
    }
    
    fun clearClothingImages() {
        _clothingImages.value = emptyList()
    }
    
    fun generateImage() {
        val userUri = _userPhotoUri.value
        val clothingUris = _clothingImages.value

        if (userUri == null || clothingUris.isEmpty()) {
            return
        }
        
        viewModelScope.launch {
            _isGenerating.value = true
            _generatedImageUri.value = null
            
            try {
                val context = getApplication<Application>()

                val userBitmap = withContext(Dispatchers.IO) {
                    ImageUtils.uriToBitmap(context, userUri)?.let {
                        ImageUtils.resizeBitmap(it)
                    }
                }
                
                val clothingBitmaps = withContext(Dispatchers.IO) {
                    clothingUris.mapNotNull { uri ->
                        ImageUtils.uriToBitmap(context, uri)?.let {
                            ImageUtils.resizeBitmap(it)
                        }
                    }
                }
                
                if (userBitmap == null || clothingBitmaps.isEmpty()) {
                    Log.e("MainViewModel", "Error processing input images")
                    _isGenerating.value = false
                    return@launch
                }

                // 2. Inicializar Modelo Gemini 2.5 Flash Image usando Firebase AI (GoogleAI backend)
                val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
                    modelName = "gemini-2.5-flash-image",
                    generationConfig = generationConfig {
                        responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
                    }
                )

                // 3. Construir Prompt Multimodal
                val prompt = content {
                    image(userBitmap)
                    clothingBitmaps.forEach { image(it) }
                    text("Edit the first image (the person) to make them wear the clothes shown in the subsequent images. Maintain the person's pose, face, and background. Output a high-quality photorealistic image.")
                }

                // 4. Generar Contenido
                val response = model.generateContent(prompt)
                
                // 5. Extraer imagen de la respuesta usando la API de ImagePart
                val generatedBitmap = response.candidates.firstOrNull()?.content?.parts
                    ?.filterIsInstance<ImagePart>()
                    ?.firstOrNull()?.image

                if (generatedBitmap != null) {
                    val uri = withContext(Dispatchers.IO) {
                        saveBitmapToTempFile(generatedBitmap)
                    }
                    _generatedImageUri.value = uri
                } else {
                    Log.w("MainViewModel", "No image generated in response")
                }

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error generating image", e)
            } finally {
                _isGenerating.value = false
            }
        }
    }
    
    private fun saveBitmapToTempFile(bitmap: Bitmap): Uri {
        val filename = "generated_${System.currentTimeMillis()}.png"
        val file = File(getApplication<Application>().cacheDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return Uri.fromFile(file)
    }
}
