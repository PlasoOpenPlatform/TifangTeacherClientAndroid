plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "cn.plaso.yxt.tifang"
    compileSdk = 34

    defaultConfig {
        applicationId = "cn.plaso.yxt.tifang"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        signingConfigs {
            create("release") {
                storeFile = file("key/tifang.jks")
                storePassword = "cn.plaso.yxt.tifang"
                keyAlias = "tifang"
                keyPassword = "cn.plaso.yxt.tifang"
            }
        }

        vectorDrawables {
            useSupportLibrary = true
        }
    }
    tasks.register("prepareKotlinBuildScriptModel"){}
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions("color")
    productFlavors {
        create("plaso") {
            dimension = "color"
        }
    }

    lint {
        abortOnError = false

        // true--生成XML格式报告
        xmlReport = true
        // 指定xml报告文档(默认lint-results.xml)
        xmlOutput = file("build/reports/lint-report.xml")
        // true--生成HTML报告(带问题解释，源码位置，等)
        htmlReport = true
        // html报告可选路径(构建器默认是lint-results.html )
        htmlOutput = file("build/reports/lint-report.html")
        disable += "AutoDisposeDetector"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
//    implementation("cn.plaso:yxtsdk:1.0.40")
    implementation("cn.plaso:yxtsdk:2.0.0-teacher-beta.27")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.10.0")
}