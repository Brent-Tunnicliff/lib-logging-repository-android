// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.serialization)
    id("maven-publish")
}

val javaVersion = JavaVersion.toVersion(libs.versions.java.get())

android {
    namespace = "dev.tunnicliff.logging"
    compileSdk = 35

    defaultConfig {
        minSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            testCoverage {
                enableAndroidTestCoverage = true
                enableUnitTestCoverage = true
            }
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "dev.tunnicliff"
                artifactId = "lib-logging-android"
                version = "1.0.0-beta.7"

                pom {
                    packaging = "aar"
                    name.set("lib-logging-android")
                    description.set("lib-logging-android: Logging library for android applications.")
                    url.set("https://github.com/Brent-Tunnicliff/lib-logging-android")
                    inceptionYear.set("2024")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("brent")
                            name.set("Brent Tunnicliff")
                            email.set("brent@tunnicliff.dev")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/Brent-Tunnicliff/lib-logging-android.git")
                        developerConnection.set("scm:git:ssh://git@github.com:Brent-Tunnicliff/lib-logging-android.git")
                        url.set("https://github.com/Brent-Tunnicliff/lib-logging-android")
                    }
                }
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.lib.container.android)
    implementation(libs.lib.ui.android)
    implementation(libs.material)

    annotationProcessor(libs.androidx.room.compiler)

    ksp(libs.androidx.room.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
