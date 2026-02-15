plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.xlsxtodatabase"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.xlsxtodatabase"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
     buildFeatures {
    //    viewBinding = true
        dataBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0-alpha09")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    implementation("org.apache.poi:poi-ooxml:5.5.1")
    implementation("org.apache.xmlbeans:xmlbeans:5.3.0")
    implementation("org.apache.commons:commons-collections4:4.5.0")


}