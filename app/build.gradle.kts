plugins {
 id("com.android.application")
 id("org.jetbrains.kotlin.android")
 id("org.jetbrains.kotlin.plugin.serialization")
 id("org.jetbrains.kotlin.plugin.compose")
}
android {
 namespace = "dev.bsv.app"
 compileSdk = 35
 defaultConfig { applicationId = "dev.bsv.visuals"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "0.1.0"; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 buildFeatures { compose = true }
}
kotlin { jvmToolchain(21); compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies {
 androidTestImplementation(platform("androidx.compose:compose-bom:2025.03.01"))
 androidTestImplementation("androidx.compose.ui:ui-test-junit4")
 androidTestImplementation("androidx.test:runner:1.6.2")
 androidTestImplementation("androidx.test.ext:junit:1.2.1")
 debugImplementation("androidx.compose.ui:ui-test-manifest")

implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
 implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
implementation(platform("androidx.compose:compose-bom:2025.03.01"))
 implementation("androidx.compose.material3:material3")
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.foundation:foundation")
 implementation("androidx.compose.animation:animation")
 implementation(project(":core-settings"))
implementation(project(":core-ui"))
implementation("androidx.activity:activity-compose:1.10.1")
 implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
 implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
implementation(project(":feature-home"))
implementation(project(":feature-profile"))
implementation(project(":feature-visuals"))
implementation(project(":feature-interface"))
}
