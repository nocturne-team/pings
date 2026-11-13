plugins {
	id("net.neoforged.moddev.legacyforge") version "2.0.147"
	id("com.modrinth.minotaur") version "2.10.0"
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-neoforge"

val requiredJava = JavaVersion.VERSION_17

dependencies {
	testImplementation(platform("org.junit:junit-bom:5.11.3"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// Matches the slf4j 2 binding Log4j brings in; the compile classpath alone has an older api.
	testRuntimeOnly("org.slf4j:slf4j-api:2.0.7")
}

legacyForge {
	enable {
		neoForgeVersion = property("deps.neo_loader") as String
	}

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

// mods.toml and pack.mcmeta for the Forge 47 layout.
sourceSets.main {
	resources.srcDir(rootProject.file("src/legacy/resources"))
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

		filesMatching("META-INF/mods.toml") { expand(props) }
		exclude("fabric.mod.json", "*.ct", "META-INF/neoforge.mods.toml", "META-INF/accesstransformer.cfg")
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

sourceSets.test {
	compileClasspath += sourceSets.main.get().compileClasspath
	runtimeClasspath = files(configurations.testRuntimeClasspath) + runtimeClasspath + sourceSets.main.get().compileClasspath
}

modrinth {
	val modVersion = property("mod.version") as String
	token = providers.environmentVariable("MODRINTH_TOKEN")
	projectId = "callouts"
	debugMode = providers.environmentVariable("MODRINTH_DEBUG").map { it == "true" }.orElse(false)
	versionNumber = "$modVersion+${sc.current.version}-neoforge"
	versionName = "Callouts $modVersion (NeoForge ${sc.current.version})"
	versionType = when {
		"alpha" in modVersion -> "alpha"
		"beta" in modVersion || "rc" in modVersion -> "beta"
		else -> "release"
	}
	changelog = providers.environmentVariable("CHANGELOG").orElse("")
	// The reobfuscated jar is the one that runs outside dev.
	uploadFile.set(tasks.named("reobfJar"))
	gameVersions.addAll(generateSequence(0) { it + 1 }.map { findProperty("mod.mc_releases.$it") as String? }.takeWhile { it != null }.filterNotNull().toList())
	// One jar runs on both, they share the Forge 47 API.
	loaders.addAll("neoforge", "forge")
}
