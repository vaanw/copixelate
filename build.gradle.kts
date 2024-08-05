plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose.compiler) apply false

    alias(libs.plugins.google.services) apply false
}

// Remove signed builds during clean task
tasks.register<Delete>("clean") {
    delete("app/release/")
    delete("app/debug/")
}
