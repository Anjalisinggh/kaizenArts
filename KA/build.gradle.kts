// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath "com.android.tools.build.gradle:4.1.2"
        classpath "com.google.gms:google-services:4.3.5"
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url "https://jitpack.io")
    }
}

// Apply plugins using the new plugins DSL (if supported by your project).
plugins {
    alias(libs.plugins.androidApplication) apply false




}
