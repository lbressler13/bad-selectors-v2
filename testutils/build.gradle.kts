import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

android {
    val androidCompileSdk: Int by rootProject.extra
    val androidJavaVersion: JavaVersion by rootProject.extra
    val androidJvmTarget: String by rootProject.extra
    val androidMinSdk: Int by rootProject.extra

    namespace = "xyz.lbres.testutils"
    compileSdk = androidCompileSdk

    defaultConfig {
        minSdk = androidMinSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = androidJavaVersion
        targetCompatibility = androidJavaVersion
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(androidJvmTarget)
        }
    }
}

dependencies {
    val kotlinVersion: String by rootProject.extra

    val kotlinUtilsVersion: String by rootProject.extra
    val mockkVersion: String by rootProject.extra

    implementation(kotlin("test"))
    implementation("io.mockk:mockk:$mockkVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("xyz.lbres:kotlin-utils:$kotlinUtilsVersion")
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    val ktlintVersion: String by rootProject.extra
    val maxLineLength: String by rootProject.extra

    version.set(ktlintVersion)
    additionalEditorconfig.set(mapOf("max_line_length" to maxLineLength))
}
