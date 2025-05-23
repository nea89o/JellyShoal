import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.jetbrainsCompose
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.kotlinSerialization)
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.composeHotReload)
	id("com.github.gmazzo.buildconfig") version "5.5.0"
}
kotlin {
	androidTarget {
		@OptIn(ExperimentalKotlinGradlePluginApi::class)
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_11)
		}
	}

	jvm("desktop")

	sourceSets {
		val desktopMain by getting

		commonMain.dependencies {
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.ui)
			implementation(compose.components.resources)
			implementation(compose.components.uiToolingPreview)
			implementation(libs.androidx.lifecycle.viewmodel)
			implementation(libs.androidx.lifecycle.runtimeCompose)
			implementation(libs.jellyfin.core)
			implementation(libs.kotlinx.serialization.json)
			implementation(libs.androidx.navigation.compose)
			implementation(libs.androidx.material3.iconsExtended)
//			implementation(libs.androidx.navigation.fragment)
//			implementation(libs.androidx.navigation.ui)
//			implementation(libs.androidx.navigation.features.fragment)
			runtimeOnly(libs.slf4j.simple)
		}
		commonTest.dependencies {
			implementation(libs.kotlin.test)
		}
		desktopMain.dependencies {
			implementation(compose.desktop.currentOs)
			implementation(libs.kotlinx.coroutinesSwing)
		}
		androidMain.dependencies {
			implementation(compose.preview)
			implementation(libs.androidx.activity.compose)
		}
	}
}
repositories {
	this.mavenCentral()
	this.google()
	this.jetbrainsCompose()
}

dependencies {
	debugImplementation(compose.uiTooling)
}
val versionName = "${project.version}"

buildConfig {
	packageName("moe.nea.jellyshoal.build")
	buildConfigField<String>("VERSION",versionName)
	buildConfigField<String>("BRAND","JellyShoal")
}

compose.desktop {
	application {
		mainClass = "moe.nea.jellyshoal.MainKt"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			packageName = "moe.nea.jellyshoal"
			packageVersion = versionName
		}
	}
}
android {
	namespace = "moe.nea.jellyshoal"
	compileSdk = libs.versions.android.compileSdk.get().toInt()

	defaultConfig {
		applicationId = "moe.nea.jellyshoal"
		minSdk = libs.versions.android.minSdk.get().toInt()
		targetSdk = libs.versions.android.targetSdk.get().toInt()
		versionCode = 1
		versionName = versionName
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
}
