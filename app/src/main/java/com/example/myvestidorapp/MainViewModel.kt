package com.example.myvestidorapp

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
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
        if (_userPhotoUri.value == null || _clothingImages.value.isEmpty()) {
            return
        }
        
        viewModelScope.launch {
            _isGenerating.value = true
            // TODO: Aquí se integrará la API de Gemini Nano Banana en el futuro
            // Por ahora solo simulamos el proceso
            kotlinx.coroutines.delay(2000)
            _isGenerating.value = false
            // _generatedImageUri.value = uriDeLaImagenGenerada
        }
    }
}

