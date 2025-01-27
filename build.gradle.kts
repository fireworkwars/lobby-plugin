import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.bukkitPluginYaml
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.2.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
}

group = "foundation.esoteric"
version = "1.0-SNAPSHOT"

val paperApiVersion = "1.21.4"
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
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.esotericfoundation:firework-wars-core-plugin:0.5.1")

    paperweight.paperDevBundle("$paperApiVersion-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks {
    shadowJar {
        minimize {
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect"))
        }

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
    apiVersion = paperApiVersion
    description = project.description

    dependencies {
        server("FireworkWarsCore", PaperPluginYaml.Load.BEFORE, required = true, joinClasspath = true)
    }
}

bukkitPluginYaml {
    load = BukkitPluginYaml.PluginLoadOrder.POSTWORLD
}