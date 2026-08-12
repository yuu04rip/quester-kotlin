plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.quester"

    compileSdk = 37

    packaging {
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }

    defaultConfig {
        applicationId = "com.example.quester"
        minSdk = 24

        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}


kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // --- Compose BOM ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime.annotation)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.graphics)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // --- Core Android ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)

    // --- Compose ---
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // --- Coil (Image Loading) ---
    implementation(libs.coil.compose)

    // --- Room Database ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- Test Unitari (JVM) ---
    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.work.testing)

    // --- Test Android (Instrumented) ---
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.work.testing)

    // --- Test UI Compose ---
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // --- Mockito per i test ---
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    // --- MockK per i test UI e Coroutine (Gestito via Version Catalog) ---
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)

    // --- Debug (Rimossi i duplicati di ui-test-manifest) ---
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}