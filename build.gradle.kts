// main dependencies
val androidxCoreVersion by extra { "1.13.1" }
val appCompatVersion by extra { "1.7.0" }
val constraintLayoutVersion by extra { "2.1.4" }
val kotlinUtilsVersion by extra { "1.3.4" }
val lifecycleVersion by extra { "2.8.6" }
val materialVersion by extra { "1.12.0" }
val navigationVersion by extra { "2.8.3" }

// test dependencies
// test dependency versions: https://developer.android.com/jetpack/androidx/releases/test
val androidxJunitVersion by extra { "1.3.0" }
val androidxTestRulesVersion by extra { "1.7.0" }
val androidxTestRunnerVersion by extra { "1.7.0" }
val espressoVersion by extra { "3.7.0" }
val mockkVersion by extra { "1.14.9" }
val robolectricVersion by extra { "4.16.1" }

// android configurations
val androidCompileSdk by extra { 35 }
val androidJavaVersion by extra { JavaVersion.VERSION_11 }
val androidJvmTarget by extra { "11" }
// val androidMinSdk by extra { 35 }
val androidMinSdk by extra { 34 }

// ktlint
val ktlintVersion by extra { "0.49.1" }
val maxLineLength by extra { "120" }


buildscript {
    repositories {
        google()
        mavenCentral()
    }

    val kotlinVersion by extra { "2.2.0" }
    val gradleVersion = "8.12.3"

    // only project-level dependencies, app-specific dependencies should go in application build files
    dependencies {
        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

subprojects {
    repositories {
        // general repositories
        google()
        mavenCentral()

        // kotlin utils
        val githubUsername: String? = project.findProperty("gpr.user")?.toString() ?: System.getenv("USERNAME")
        val githubPassword: String? = project.findProperty("gpr.key")?.toString() ?: System.getenv("TOKEN")
        maven {
            url = uri("https://maven.pkg.github.com/lbressler13/kotlin-utils")
            credentials {
                username = githubUsername
                password = githubPassword
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
