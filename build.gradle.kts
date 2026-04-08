import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("de.eldoria.plugin-yml.paper")  version "0.8.0"
    id("com.gradleup.shadow") version "9.3.0"
}

group = "dev.panncake.harvestengine"
version = "1.1.2"

repositories {
    mavenCentral()
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

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveVersion.set("")

        relocate("dev.dejvokep.boostedyaml", "dev.panncake.harvestengine.libs")

        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}