import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.jetbrainsCompose
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	`java-base`
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.kotlinSerialization)
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.composeHotReload)
	id("com.github.gmazzo.buildconfig") version "5.6.7"
	id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

kotlin {
	androidTarget {
		@OptIn(ExperimentalKotlinGradlePluginApi::class)
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_11)
		}
	}

	compilerOptions {
		if (project.findProperty("jellyshoal.enableComposeCompilerReports") == "true") { // Collect metrics
			val metricsDirectory = layout.buildDirectory.dir("compose_metrics").get().asFile.absoluteFile
			freeCompilerArgs.addAll(
				"-P",
				"plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$metricsDirectory",
				"-P",
				"plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$metricsDirectory",
			)
		}
		freeCompilerArgs.add("-Xallow-kotlin-package")
	}

	jvm("desktop")

	sourceSets {
		val desktopMain by getting

		commonMain.dependencies {
			// Compose basics
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.ui)
			implementation(compose.components.resources)
			implementation(compose.components.uiToolingPreview)
			implementation(libs.androidx.lifecycle.viewmodel)
			implementation(libs.androidx.lifecycle.runtimeCompose)

			// Icons!
			implementation(libs.androidx.material3.iconsExtended)

			// J'Fin
			implementation(libs.jellyfin.core)
			implementation(libs.kotlinx.serialization.json)

			// New navigation system / voyager
			implementation(libs.voyager.navigator)
			implementation(libs.voyager.screenModel)

			implementation(libs.coil.compose)
			implementation(libs.coil.network.okhttp)
			// Old android navigation options
//			implementation(libs.androidx.navigation.compose)
//			implementation(libs.androidx.material3.iconsExtended)
//			implementation(libs.androidx.navigation.fragment)
//			implementation(libs.androidx.navigation.ui)
//			implementation(libs.androidx.navigation.features.fragment)

			// Logging
			implementation(libs.kotlinLogging)
			runtimeOnly(libs.slf4j.simple)
		}
		commonTest.dependencies {
			implementation(libs.kotlin.test)
		}
		desktopMain.dependencies {
			implementation(compose.desktop.currentOs)
			implementation(libs.kotlinx.coroutinesSwing)
			implementation(libs.directories)

			implementation(libs.vlcj)

			implementation(libs.auto.service.annotations)
		}
		androidMain.dependencies {
			implementation(compose.preview)
			implementation(libs.androidx.activity.compose)
			implementation(libs.androidx.core.ktx)
		}
	}
}

val extraNatives by configurations.creating {
	isCanBeResolved = true
	isCanBeDeclared = true
	isCanBeConsumed = false // Should this be false?
	this.attributes {
		attribute(
			Attribute.of("ui", String::class.java),
			"awt"
		)
	}
}

//configurations.forEach { println(it.name) }

repositories {
	this.mavenCentral()
	this.google()
	this.jetbrainsCompose()
	this.maven("https://jitpack.io") {
		name = "jitpack"
		content {
			includeGroupByRegex("(io|com)\\.github\\..+")
		}
	}
}
compose.desktop {
	application {
		mainClass = "moe.nea.jellyshoal.MainKt"
		this.nativeDistributions {
			this.targetFormats(
				TargetFormat.Msi,
				TargetFormat.AppImage
			)
		}
		this.buildTypes {
			this.release {
				this.proguard {
					obfuscate.set(false)
					isEnabled.set(false)
				}
			}
		}
	}
}
dependencies {
	"kspDesktop"(libs.auto.service.ksp)

	debugImplementation(compose.uiTooling)
	// TODO: do multiple distros for each os / arch combo
	extraNatives(compose.desktop.linux_x64)
	extraNatives(compose.desktop.macos_x64)
	extraNatives(compose.desktop.macos_arm64)
	extraNatives(compose.desktop.windows_x64)
}
val versionName = "${project.version}"

buildConfig {
	packageName("moe.nea.jellyshoal.build")
	buildConfigField<String>("VERSION", versionName)
	buildConfigField<String>("BRAND", "JellyShoal")
	buildConfigField<String>("GROUP_QUALIFIER", "moe.nea")
	buildConfigField<String>("PUBLISHER", "Linnea Gräf")
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
	composeOptions {
		kotlinCompilerExtensionVersion = libs.versions.composeMultiplatform.get()
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	buildFeatures { compose = true
	}
}

val allDesktopJars by tasks.registering(Copy::class) {
	from(configurations.named("desktopRuntimeClasspath"))
	from(tasks.named("desktopJar"))
	from(extraNatives)
	into(layout.buildDirectory.dir("allDesktopJars"))
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
