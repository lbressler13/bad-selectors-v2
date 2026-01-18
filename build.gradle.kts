buildscript {
    repositories {
        google()
        mavenCentral()
    }

    val kotlinVersion by extra { "1.9.20" }
    val gradleVersion = "8.4.2"

    // only project-level dependencies, app-specific dependencies should go in application build files
    dependencies {
        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
