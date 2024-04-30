import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.copixelate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.copixelate"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp { arg("room.schemaLocation", "$projectDir/schemas") }
    }

    signingConfigs {
        create("release") {
            // Retrieve signing credentials from secret file
            Properties().run {
                load(FileInputStream(project.file("keystore.properties")))
                storePassword = this["storePassword"] as String
                keyPassword = this["keyPassword"] as String
                keyAlias = this["keyAlias"] as String
                storeFile = file(this["storeFile"] as String)
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinComposeCompiler.get()
    }
}

dependencies {
    // Art Module
    implementation(project(":art"))

    // Kotlin
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ksp.api)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    // Bill of Materials, https://developer.android.com/jetpack/compose/bom
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.materialIcons)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Proto DataStore
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    // Google Play Services
    implementation(libs.android.gms.playServicesAuth)

    // Firebase
    // Bill of Materials, https://firebase.google.com/support/release-notes/android
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Compose Testing
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.ui.uiTestJunit4)
    debugImplementation(libs.androidx.compose.ui.uiTooling)
    debugImplementation(libs.androidx.compose.ui.uiTestManifest)
}

// Protobuf lite code generation for Proto DataStore
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().configureEach {
            builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
