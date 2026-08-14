# Weather & Clock

Aplicación de clima y reloj mundial inspirada en One UI, con glassmorphism adaptativo, visuales dinámicos de día/noche y widgets para la pantalla de inicio.

## Características

- **Clima actual y pronóstico** con datos de [Open-Meteo](https://open-meteo.com) y fallback automático a [Met.no](https://api.met.no).
- **Búsqueda de ciudades** avanzada (geocodificación con [Nominatim](https://nominatim.openstreetmap.org)).
- **Reloj mundial** con doble reloj por ciudad.
- **Widgets** de clima (2x2) y reloj dual (4x1) para la pantalla de inicio.
- **Glassmorphism adaptativo**: la interfaz se adapta al tema claro/oscuro del sistema.
- **Visuales dinámicos** de día/noche según la hora y el clima de cada ciudad.
- **Notificaciones** de clima y persistencia local (Room + DataStore).
- **Estudio de widgets**: personaliza el estilo de tus widgets.

## Requisitos

- [Android Studio](https://developer.android.com/studio) (con Android SDK 36)
- JDK 17+

## Compilar

```bash
./gradlew assembleDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`.

> El build de release requiere un keystore firmado. Define `KEYSTORE_PATH`, `STORE_PASSWORD` y `KEY_PASSWORD` en el entorno (ver `app/build.gradle.kts`).

## Ejecutar localmente

1. Abre el proyecto en Android Studio.
2. (Opcional) Crea un archivo `.env` en la raíz con `GEMINI_API_KEY` si usas funciones de IA (ver `.env.example`).
3. Ejecuta la app en un emulador o dispositivo físico.

## CI Build & Releases

Cada push a `main` dispara el workflow [Build & Release](.github/workflows/build-release.yml):

1. Configura JDK 17, Android SDK y Gradle 9.3.1 (wrapper incluido).
2. Crea un `.env` temporal con secretos placeholder y un keystore de debug.
3. Ejecuta `./gradlew assembleDebug`.
4. Si el build funciona, crea una GitHub Release etiquetada `build-<sha>` con el APK (`app-debug.apk`).

También puedes ejecutarlo manualmente desde la pestaña **Actions**. La release solo se crea cuando el build pasa.

## Licencia

Ver [LICENSE](LICENSE).