// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

plugins {
    id("androidx.room")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("maven-publish")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "dev.tunnicliff.logging"
    compileSdk = 34

    defaultConfig {
        minSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "dev.tunnicliff"
            artifactId = "logging"
            version = "0.1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-paging:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.paging:paging-compose:3.3.2")
    implementation("com.github.Brent-Tunnicliff:lib-container-android:1.0.0-beta.1")
    implementation("com.github.Brent-Tunnicliff:lib-ui-android:0.1.0-alpha.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

    // Example of github lib.
    // implementation("com.github.Brent-Tunnicliff:temp_poc:0.0.4")

    annotationProcessor("androidx.room:room-compiler:2.6.1")

    ksp("androidx.room:room-compiler:2.6.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.0")

    testImplementation("androidx.room:room-testing:2.6.1")
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
