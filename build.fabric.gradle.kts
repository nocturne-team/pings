plugins {
	id("dev.kikugie.loom-back-compat")
	id("com.modrinth.minotaur") version "2.10.0"
}

// DO NOT set group = ...!
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-fabric"

val requiredJava = JavaVersion.VERSION_25

repositories {
	maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
}

dependencies {
	fun fapi(vararg modules: String) {
		for (it in modules) modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"]))
	}

	minecraft("com.mojang:minecraft:${sc.current.version}")
	loomx.applyMojangMappings()

	modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
	fapi(
		"fabric-api-base",
		"fabric-networking-api-v1",
		"fabric-key-mapping-api-v1",
		"fabric-lifecycle-events-v1",
		"fabric-rendering-v1",
		"fabric-resource-loader-v0",
	)

	// Optional at runtime: only used to show the config screen in ModMenu's list.
	modCompileOnly("com.terraformersmc:modmenu:${property("deps.modmenu")}")
	modLocalRuntime("com.terraformersmc:modmenu:${property("deps.modmenu")}")

	testImplementation(platform("org.junit:junit-bom:5.11.3"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

loom {
	fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
	accessWidenerPath = rootProject.file("src/main/resources/callouts.ct")

	runConfigs.all {
		preferGradleTask = true
		generateRunConfig = true
		runDirectory = rootProject.file("run")
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
	test {
		useJUnitPlatform()
	}

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
			register("loader", Regex("\\d+\\.\\d+").find(sc.properties.get<String>("deps.fabric_loader"))!!.value)
		}

		filesMatching("fabric.mod.json") { expand(props) }
		exclude("META-INF/neoforge.mods.toml", "META-INF/accesstransformer.cfg")
	}

	withType<Jar> {
		val name = project.property("mod.id")
		inputs.property("mod_id", name)
		from("../../LICENSE") { rename { "$it-$name" } }
	}
}

// Publishing: `./gradlew modrinth` uploads every loader/version jar. Needs MODRINTH_TOKEN; CHANGELOG is optional.
modrinth {
	val modVersion = property("mod.version") as String
	token = providers.environmentVariable("MODRINTH_TOKEN")
	projectId = "callouts"
	// MODRINTH_DEBUG=true prints what would be uploaded instead of uploading.
	debugMode = providers.environmentVariable("MODRINTH_DEBUG").map { it == "true" }.orElse(false)
	versionNumber = "$modVersion+${sc.current.version}-fabric"
	versionName = "Callouts $modVersion (Fabric ${sc.current.version})"
	versionType = when {
		"alpha" in modVersion -> "alpha"
		"beta" in modVersion || "rc" in modVersion -> "beta"
		else -> "release"
	}
	changelog = providers.environmentVariable("CHANGELOG").orElse("")
	uploadFile.set(tasks.named(if (tasks.names.contains("remapJar")) "remapJar" else "jar"))
	// Stonecutter flattens TOML lists into mod.mc_releases.0, .1, ...
	gameVersions.addAll(generateSequence(0) { it + 1 }.map { findProperty("mod.mc_releases.$it") as String? }.takeWhile { it != null }.filterNotNull().toList())
	loaders.add("fabric")
	dependencies {
		required.project("fabric-api")
		optional.project("modmenu")
	}
}
