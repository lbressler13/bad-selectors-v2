import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

fun getEspressoRetries(): Int {
    val defaultRetries = 0

    return if (project.hasProperty("espressoRetries")) {
        val espressoRetries: String? by project
        espressoRetries?.toIntOrNull() ?: defaultRetries
    } else {
        defaultRetries
    }
}

android {
    val androidCompileSdk: Int by rootProject.extra
    val androidJavaVersion: JavaVersion by rootProject.extra
    val androidJvmTarget: String by rootProject.extra
    val androidMinSdk: Int by rootProject.extra

    namespace = "xyz.lbres.badselectorsv2"
    compileSdk = androidCompileSdk

    defaultConfig {
        applicationId = "xyz.lbres.badselectorsv2"
        minSdk = androidMinSdk
        targetSdk = androidCompileSdk
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("int", "ESPRESSO_RETRIES", getEspressoRetries().toString())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    flavorDimensions += "type"
    productFlavors {
        create("dev") {
            dimension = "type"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }

        create("final") {
            dimension = "type"
            versionNameSuffix = "-final"
        }
    }

    sourceSets.getByName("main") {
        java.setSrcDirs(listOf("src/main/kotlin"))
    }

    sourceSets.getByName("dev") {
        java.setSrcDirs(listOf("src/dev/kotlin"))
    }

    sourceSets.getByName("final") {
        java.setSrcDirs(listOf("src/final/kotlin"))
    }

    sourceSets.getByName("test") {
        java.setSrcDirs(listOf("src/test/kotlin"))
    }

    sourceSets.getByName("testDev") {
        java.setSrcDirs(listOf("src/testDev/kotlin"))
    }

    sourceSets.getByName("testFinal") {
        java.setSrcDirs(listOf("src/testFinal/kotlin"))
    }

    sourceSets.getByName("androidTest") {
        java.setSrcDirs(listOf("src/androidTest/kotlin"))
    }

    sourceSets.getByName("androidTestDev") {
        java.setSrcDirs(listOf("src/androidTestDev/kotlin"))
    }

    sourceSets.getByName("androidTestFinal") {
        java.setSrcDirs(listOf("src/androidTestFinal/kotlin"))
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    testOptions {
        animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
        }
        // resolves mockk issue: https://github.com/mockk/mockk/issues/297
        packaging {
            jniLibs {
                useLegacyPackaging = true
            }
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

    val androidxCoreVersion: String by rootProject.extra
    val androidxFragmentVersion: String by rootProject.extra
    val appCompatVersion: String by rootProject.extra
    val constraintLayoutVersion: String by rootProject.extra
    val kotlinUtilsVersion: String by rootProject.extra
    val lifecycleVersion: String by rootProject.extra
    val materialVersion: String by rootProject.extra
    val navigationVersion: String by rootProject.extra

    val androidxJunitVersion: String by rootProject.extra
    val androidxTestRulesVersion: String by rootProject.extra
    val androidxTestRunnerVersion: String by rootProject.extra
    val androidxTracingVersion: String by rootProject.extra
    val espressoVersion: String by rootProject.extra
    val mockkVersion: String by rootProject.extra
    val robolectricVersion: String by rootProject.extra

    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("androidx.core:core-ktx:$androidxCoreVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("xyz.lbres:kotlin-utils:$kotlinUtilsVersion")

    implementation(project(":customview"))

    // testing
    testImplementation(kotlin("test"))
    testImplementation("androidx.fragment:fragment-testing:${androidxFragmentVersion}")
    testImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-contrib:$espressoVersion")
    testImplementation("androidx.test.ext:junit-ktx:$androidxJunitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.robolectric:robolectric:$robolectricVersion")
    // https://developer.android.com/guide/fragments/test
    debugImplementation("androidx.fragment:fragment-testing-manifest:$androidxFragmentVersion")
    androidTestImplementation("androidx.fragment:fragment-testing:$androidxFragmentVersion")
    androidTestImplementation("androidx.test.ext:junit:$androidxJunitVersion")
    androidTestImplementation("androidx.test:rules:$androidxTestRulesVersion")
    androidTestImplementation("androidx.test:runner:$androidxTestRunnerVersion") // needed to run on emulator
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:$espressoVersion")
    androidTestImplementation("io.mockk:mockk-android:$mockkVersion")

    configurations.all {
        resolutionStrategy {
            force("androidx.tracing:tracing:$androidxTracingVersion")
        }
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    val ktlintVersion: String by rootProject.extra
    val maxLineLength: String by rootProject.extra

    version.set(ktlintVersion)
    additionalEditorconfig.set(mapOf("max_line_length" to maxLineLength))
}
