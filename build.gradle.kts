import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("de.eldoria.plugin-yml.paper")  version "0.8.0"
    id("com.gradleup.shadow") version "9.3.0"
    id("com.modrinth.minotaur") version "2.+"
}

group = "dev.panncake.harvestengine"
version = "1.2.0-SNAPSHOT"

val envVersion = System.getenv("RELEASE_VERSION")?.replace("v", "")
val envChangelog = System.getenv("RELEASE_CHANGELOG")

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://maven.devs.beer/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("dev.lone:api-itemsadder:4.0.10")

    implementation("org.spongepowered:configurate-yaml:4.2.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

paper {
    main = "${rootProject.group}.${rootProject.name}"
    name = rootProject.name
    version = "${rootProject.version}"
    apiVersion = "1.21"
    authors = listOf("Panncake")

    bootstrapDependencies  {
        register("ItemsAdder") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

    serverDependencies {
        register("ItemsAdder") {
            required = false
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("jLF6CQ6X")

    versionNumber.set(envVersion ?: project.version.toString())

    changelog.set(envChangelog ?: "No changelog provided")

    versionType.set("release")
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(listOf(
        "1.21",
        "1.21.1",
        "1.21.2",
        "1.21.3",
        "1.21.4",
        "1.21.5",
        "1.21.6",
        "1.21.7",
        "1.21.8",
        "1.21.9",
        "1.21.10",
        "1.21.11",
    ))
    loaders.set(listOf("paper"))
    dependencies {
        optional.project("ItemsAdder")
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("${project.name}-${project.version}.jar")

        relocate("dev.dejvokep.boostedyaml", "dev.panncake.harvestengine.libs")
        minimize()
    }

    jar {
        enabled = false
    }

    build {
        dependsOn(shadowJar)
    }
}