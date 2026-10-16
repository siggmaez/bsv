plugins {
 id("com.android.library")
 id("org.jetbrains.kotlin.android")
 id("org.jetbrains.kotlin.plugin.serialization")
 id("org.jetbrains.kotlin.plugin.compose")
}
android {
 namespace = "dev.bsv.featureprofile"
 compileSdk = 35
 defaultConfig { minSdk = 26 }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 buildFeatures { compose = true }
}
kotlin { jvmToolchain(21); compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies {
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
 implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
implementation(platform("androidx.compose:compose-bom:2025.03.01"))
 implementation("androidx.compose.material3:material3")
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.foundation:foundation")
 implementation("androidx.compose.animation:animation")
 implementation(project(":core-settings"))
implementation(project(":core-ui"))
}
