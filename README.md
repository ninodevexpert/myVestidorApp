# MiVestidor

MiVestidor es una aplicación Android de vestidor virtual que utiliza **Firebase AI Logic** y **Gemini 2.5 Flash Image** para generar una imagen de una persona llevando las prendas seleccionadas.

El usuario añade una fotografía de cuerpo entero, selecciona hasta tres prendas desde la cámara o la galería y solicita a Gemini una composición fotorrealista que conserva, en lo posible, el rostro, la postura y el fondo de la fotografía original.

## Funcionalidades principales

- Selección de una fotografía de perfil desde el dispositivo.
- Captura o selección de hasta tres imágenes de prendas.
- Previsualización y eliminación individual de prendas.
- Generación y edición multimodal con `gemini-2.5-flash-image`.
- Presentación del resultado generado dentro de la aplicación.
- Corrección automática de la orientación EXIF y reducción de imágenes grandes.
- Protección de Firebase AI Logic mediante Firebase App Check.
- Interfaz construida con Jetpack Compose y el sistema visual **Vivid Couture**.

## Tecnologías

- Kotlin 2.0.21
- Jetpack Compose y Material 3
- Navigation Compose
- Android ViewModel, StateFlow y coroutines
- Coil para la carga de imágenes
- Firebase AI Logic
- Firebase App Check
  - Debug provider durante el desarrollo
  - Play Integrity en compilaciones de producción
- Firebase Analytics

Configuración Android actual:

- `minSdk`: 24
- `targetSdk`: 36
- `compileSdk`: 36
- Bytecode Java/JVM objetivo: 11

## Flujo de la aplicación

1. En **Configura tu Perfil**, el usuario selecciona una fotografía de cuerpo entero.
2. En **Vestidor Virtual**, añade entre una y tres prendas desde la cámara o la galería.
3. La aplicación normaliza las imágenes y las limita a una dimensión operativa de 1024 píxeles.
4. `MainViewModel` construye una petición multimodal con la fotografía personal, las prendas y las instrucciones de edición.
5. Firebase AI Logic envía la petición a `gemini-2.5-flash-image`.
6. La primera imagen devuelta por el modelo se guarda temporalmente en la caché de la aplicación y se muestra en pantalla.

## Estructura relevante

```text
app/src/
├── debug/.../VestidorApplication.kt       # App Check Debug Provider
├── release/.../VestidorApplication.kt     # App Check Play Integrity
└── main/
    ├── AndroidManifest.xml
    └── java/com/example/myvestidorapp/
        ├── MainActivity.kt                 # Navegación Compose
        ├── MainViewModel.kt                # Estado y generación con Gemini
        ├── utils/ImageUtils.kt             # Conversión, rotación y escalado
        └── ui/
            ├── components/                 # Componentes Vivid Couture
            ├── screens/                    # Perfil y vestidor virtual
            └── theme/                      # Colores, tema y tipografía
```

## Configuración de Firebase

### 1. Registrar la aplicación

1. Crea o selecciona un proyecto en [Firebase Console](https://console.firebase.google.com/).
2. Registra una aplicación Android con el package name:

   ```text
   com.example.myvestidorapp
   ```

3. Descarga `google-services.json` y colócalo en:

   ```text
   app/google-services.json
   ```

4. Activa Firebase AI Logic con el proveedor Gemini Developer API.

### 2. Activar App Check para AI Logic

En Firebase Console abre **Seguridad → App Check → APIs → Firebase AI Logic** y configura:

- Protección de referencia: **Aplicado**.
- Protección contra la repetición: **Inhabilitado** inicialmente.

La aplicación no funcionará si Firebase AI Logic exige App Check y el cliente no envía un token válido.

### 3. Desarrollo local

La variante `debug` instala automáticamente `DebugAppCheckProviderFactory`.

1. Ejecuta la aplicación en un emulador o dispositivo de pruebas.
2. Busca en Logcat un mensaje similar a:

   ```text
   DebugAppCheckProvider: Enter this debug secret into the allow list...
   ```

3. Copia el token y regístralo en **Firebase Console → App Check → Aplicaciones → Administrar tokens de depuración**.
4. Vuelve a ejecutar la aplicación.

> No publiques, compartas ni incluyas tokens de depuración en el repositorio.

### 4. Producción

La variante `release` utiliza `PlayIntegrityAppCheckProviderFactory`. Antes de distribuirla:

1. Registra en Firebase App Check la huella SHA-256 del certificado con el que firmas la aplicación.
2. Configura el proveedor Play Integrity.
3. Vincula el proyecto de Google Cloud correspondiente desde Google Play Console si vas a distribuir mediante Google Play.
4. Comprueba las métricas de App Check antes de publicar.

Consulta la [documentación de App Check para Firebase AI Logic](https://firebase.google.com/docs/ai-logic/app-check) y la [configuración de Play Integrity en Android](https://firebase.google.com/docs/app-check/android/play-integrity-provider).

## Ejecutar el proyecto

Requisitos:

- Android Studio compatible con AGP 8.13.1.
- JDK 17 o una versión posterior compatible con Gradle 8.13 (verificado con JDK 21).
- Android SDK 36.
- Un emulador o dispositivo con Android 7.0 (API 24) o superior.
- Un proyecto Firebase configurado como se explica arriba.

Desde Android Studio, sincroniza Gradle y ejecuta la configuración `app`.

También puedes compilar desde la terminal:

```bash
./gradlew :app:assembleDebug
```

El APK se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Para comprobar las variantes principales:

```bash
./gradlew :app:compileDebugKotlin :app:compileReleaseKotlin
```

## Diseño

La interfaz sigue el sistema **Vivid Couture**, orientado a una experiencia de moda tecnológica:

- Rosa eléctrico para las acciones principales.
- Cian para acciones secundarias.
- Fondos claros con gradientes rosa y azul.
- Tarjetas, botones y contenedores con radios amplios.
- Bordes suaves y sombras ambientales.
- Componentes reutilizables definidos en `ui/components/VividComponents.kt`.

Los colores dinámicos de Android están desactivados intencionadamente para mantener la identidad visual de forma consistente entre dispositivos.

## Consideraciones

- La generación requiere conexión a Internet y puede consumir cuota o generar costes en el proyecto de Google Cloud/Firebase.
- Las respuestas generativas no son deterministas y pueden variar incluso usando las mismas imágenes.
- Las imágenes generadas se almacenan en la caché privada de la aplicación; no se guardan automáticamente en la galería.
- Los errores de generación se registran actualmente en Logcat con la etiqueta `MainViewModel`.
- Nunca incluyas credenciales privadas, claves de cuentas de servicio ni tokens App Check debug en el repositorio.
