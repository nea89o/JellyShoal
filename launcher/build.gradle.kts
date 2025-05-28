plugins {
	java
}

abstract class GenerateManifest : DefaultTask() {
	@get:InputFiles
	abstract val jars: ConfigurableFileCollection

	@get:OutputFile
	abstract val outputFile: RegularFileProperty

	@TaskAction
	fun perform() {
		outputFile.get().asFile.printWriter().use { writer ->
			writer.println("Manifest-Version: 1.0")
			writer.print("Class-Path:")
			for (file in jars.asFileTree.files) {
				writer.println("  libraries/${file.name}")
			}
			writer.println("Main-Class: moe.nea.jellyshoal.Launcher")
		}
	}
}


val allDesktopJars = tasks.getByPath(":composeApp:allDesktopJars")

val generateManifest by tasks.registering(GenerateManifest::class) {
	outputFile.set(layout.buildDirectory.file("generated/manifestgen/MANIFEST.MF"))
	jars.from(files(allDesktopJars))
}

val generateLauncherJar by tasks.registering(Zip::class) {
	archiveFileName.set("JellyShoal.jar")
	from(generateManifest) { into("META-INF") }
	from(sourceSets.main.map { it.output })
}

val formatRelease by tasks.registering(Copy::class) {
	into("libraries") {
		from(allDesktopJars)
	}
	from(generateLauncherJar)
	into(layout.buildDirectory.dir("formattedRelease"))
}
