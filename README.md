<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/fecc84d4-2168-4d93-ae1b-445e5e6fd969

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. If you have already published your app in AI Studio, please [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.

## CI Build & Releases

Every push to `main` triggers the [Build & Release](../.github/workflows/build-release.yml) workflow:

1. Sets up JDK 17, the Android SDK and Gradle 9.3.1 (via the committed wrapper).
2. Creates a temporary `.env` with placeholder secrets and a debug keystore.
3. Runs `./gradlew assembleDebug`.
4. If the build succeeds, it creates a GitHub Release tagged `build-<commit-sha>` containing the debug APK (`app-debug.apk`).

You can also trigger it manually from the **Actions** tab. The release is only created when the build passes.
