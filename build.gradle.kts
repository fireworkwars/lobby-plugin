import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.bukkitPluginYaml
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.2.0"
    id("io.papermc.paperweight.userdev") version "1.7.3"
}

group = "foundation.esoteric"
version = "1.0-SNAPSHOT"

val targetJavaVersion = 21

kotlin {
    jvmToolchain(targetJavaVersion)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    mavenLocal()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("foundation.esoteric:firework-wars-core-plugin:1.0-SNAPSHOT")
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("dev.triumphteam:triumph-gui:3.1.11")
}

tasks {
    shadowJar {
        minimize {
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect"))
        }

        relocate("dev.triumphteam.gui", "foundation.esoteric.fireworkwarslobby.gui")

        destinationDirectory.set(file("../firework-wars-plugin/run/plugins"))
    }

    build {
        dependsOn(shadowJar)
    }

    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

paperPluginYaml {
    name = "FireworkWarsLobby"
    authors = listOf("rolyPolyVole")
    website = "https://github.com/EsotericFoundation/firework-wars-lobby-plugin"

    main = "foundation.esoteric.fireworkwarslobby.FireworkWarsLobbyPlugin"
    apiVersion = "1.21.3"
    description = project.description

    dependencies {
        server("FireworkWarsCore", PaperPluginYaml.Load.BEFORE, required = true, joinClasspath = true)
    }
}

bukkitPluginYaml {
    load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
}