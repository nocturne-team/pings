plugins {
	id("net.neoforged.moddev") version "2.0.147"
	id("com.modrinth.minotaur") version "2.10.0"
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-neoforge"

val requiredJava = JavaVersion.VERSION_25

dependencies {
	testImplementation(platform("org.junit:junit-bom:5.11.3"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

neoForge {
	version = property("deps.neo_loader") as String
	accessTransformers.from(rootProject.file("src/main/resources/META-INF/accesstransformer.cfg"))

	mods {
		register("callouts") {
			sourceSet(sourceSets.main.get())
		}
	}

	runs {
		register("client") {
			gameDirectory = file("../../run/")
			client()
		}

		register("server") {
			gameDirectory = file("../../run/")
			server()
		}
	}
}

java {
	withSourcesJar()
	targetCompatibility = requiredJava
	sourceCompatibility = requiredJava

	toolchain {
		vendor = JvmVendorSpec.ADOPTIUM
		languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
	}
}

tasks {
	processResources {
		fun MutableMap<String, String>.register(key: String, value: String) {
			inputs.property(key, value)
			set(key, value)
		}

		val props = buildMap {
			register("id", sc.properties["mod.id"])
			register("name", sc.properties["mod.name"])
			register("version", sc.properties["mod.version"])
			register("minecraft", sc.properties["mod.mc_compat"])
		}

		filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
		exclude("fabric.mod.json", "*.ct")
	}

	named("createMinecraftArtifacts") {
		dependsOn("stonecutterGenerate")
	}

	withType<Jar> {
		val name = project.property("mod.id")
		inputs.property("mod_id", name)
		from("../../LICENSE") { rename { "$it-$name" } }
	}

	test {
		useJUnitPlatform()
	}
}

// Lets tests use Minecraft classes without booting FML.
sourceSets.test {
	compileClasspath += sourceSets.main.get().compileClasspath
	runtimeClasspath += sourceSets.main.get().compileClasspath
}

// Publishing: `./gradlew modrinth` uploads every loader/version jar. Needs MODRINTH_TOKEN; CHANGELOG is optional.
modrinth {
	val modVersion = property("mod.version") as String
	token = providers.environmentVariable("MODRINTH_TOKEN")
	projectId = "callouts"
	// MODRINTH_DEBUG=true prints what would be uploaded instead of uploading.
	debugMode = providers.environmentVariable("MODRINTH_DEBUG").map { it == "true" }.orElse(false)
	versionNumber = "$modVersion+${sc.current.version}-neoforge"
	versionName = "Callouts $modVersion (NeoForge ${sc.current.version})"
	versionType = when {
		"alpha" in modVersion -> "alpha"
		"beta" in modVersion || "rc" in modVersion -> "beta"
		else -> "release"
	}
	changelog = providers.environmentVariable("CHANGELOG").orElse("")
	uploadFile.set(tasks.named("jar"))
	// Stonecutter flattens TOML lists into mod.mc_releases.0, .1, ...
	gameVersions.addAll(generateSequence(0) { it + 1 }.map { findProperty("mod.mc_releases.$it") as String? }.takeWhile { it != null }.filterNotNull().toList())
	loaders.add("neoforge")
	dependencies {
	}
}
