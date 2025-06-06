import android.databinding.tool.ext.classSpec

buildscript {
    dependencies {
        classpath(libs.google.services)
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    id("com.google.gms.google-services") version "4.3.15" apply false // Google Services plugin
}