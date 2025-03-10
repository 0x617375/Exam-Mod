
plugins {
    id("com.android.application")
    
}

android {
    namespace = "com.exam.exammodwithx"
    compileSdk = 33
    //buildToolsVersion = "33.0.2"
    
    defaultConfig {
        applicationId = "com.exam.exammodwithx"
        minSdk = 21
        targetSdk = 32
        versionCode = 4
        versionName = "1.3"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    signingConfigs {
        create("release") {
            keyAlias = "abc"
            keyPassword = "abcdef"
            storeFile = file("abc.keystore")
            storePassword = "abcdef"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            //proguardFiles("proguard-rules.pro")
            getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        viewBinding = true
        
    }
    
}

dependencies {


    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("com.github.GrenderG:Toasty:1.5.2")
    //implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
