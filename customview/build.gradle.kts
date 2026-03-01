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

    namespace = "xyz.lbres.customview"
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
    kotlinOptions {
        jvmTarget = androidJvmTarget
    }
}

dependencies {
    val androidxCoreVersion: String by rootProject.extra
    val appCompatVersion: String by rootProject.extra
    val kotlinUtilsVersion: String by rootProject.extra
    val materialVersion: String by rootProject.extra

    val androidxJunitVersion: String by rootProject.extra
    val espressoVersion: String by rootProject.extra

    implementation("androidx.core:core-ktx:$androidxCoreVersion")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("xyz.lbres:kotlin-utils:$kotlinUtilsVersion")

    testImplementation(kotlin("test"))
    androidTestImplementation("androidx.test.ext:junit:$androidxJunitVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    val ktlintVersion: String by rootProject.extra
    val maxLineLength: String by rootProject.extra

    version.set(ktlintVersion)
    additionalEditorconfig.set(mapOf("max_line_length" to maxLineLength))
}
