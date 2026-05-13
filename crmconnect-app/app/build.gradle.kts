import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

val localProps = Properties().also { props ->
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { props.load(it) }
}

val backendUrl: String = localProps.getProperty("BACKEND_URL") ?: "http://10.0.2.2:8080/api/v1/"

android {
    namespace = "com.wtc.crmconnect.app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.wtc.crmconnect.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BACKEND_URL", "\"$backendUrl\"")
    }

    signingConfigs {
        create("release") {
            val ksPath = localProps.getProperty("KEYSTORE_PATH", "")
            val ksPass = localProps.getProperty("KEYSTORE_PASSWORD", "")
            val kAlias = localProps.getProperty("KEY_ALIAS", "")
            val kPass  = localProps.getProperty("KEY_PASSWORD", "")
            if (ksPath.isNotEmpty()) {
                storeFile = file(ksPath)
                storePassword = ksPass
                keyAlias = kAlias
                keyPassword = kPass
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = if (localProps.getProperty("KEYSTORE_PATH", "").isNotEmpty()) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("androidx.compose.foundation.ExperimentalFoundationApi")
    }
}

dependencies {
    // Compose BOM — controla versões de todos os artefatos androidx.compose.*
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Core + lifecycle + activity
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose Material 3 + UI + ícones
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.text)

    // Material 2 — TEMPORÁRIO (Fase 1). Telas legadas Sprint 1 ainda importam
    // androidx.compose.material.*. Será removido na Fase 2 quando reescrevermos
    // as telas para Material 3 only (decisão travada em padroes.md).
    implementation(libs.androidx.compose.material)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt + Hilt Compose
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Network: Retrofit + Moshi + OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Token storage cifrado
    implementation(libs.androidx.security.crypto)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Acessórios já em uso na Sprint 1 (mantidos enquanto telas legadas dependem)
    implementation(libs.accompanist.pager)

    // Firebase — FCM push notifications (F6)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
