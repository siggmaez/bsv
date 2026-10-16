plugins {
 id("com.android.library")
 id("org.jetbrains.kotlin.android")
 id("org.jetbrains.kotlin.plugin.serialization")
}
android {
 namespace = "dev.bsv.coresettings"
 compileSdk = 35
 defaultConfig { minSdk = 26 }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
kotlin { jvmToolchain(21); compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies {
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
 implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
implementation("androidx.datastore:datastore-preferences:1.1.3")
testImplementation("junit:junit:4.13.2")
}
