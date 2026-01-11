import org.gradle.kotlin.dsl.assemble
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.processResources
import org.gradle.kotlin.dsl.runServer
import org.gradle.kotlin.dsl.shadowJar

plugins {
    id("atomi.java-conventions")
    id("atomi.publication-conventions")
    id("com.gradleup.shadow") version "8.3.4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
}

dependencies {
    api(project(":atomi-core"))

    compileOnly(libs.paper.api)

    implementation(libs.adventure.text.serializer.legacy)
}

tasks {
    processResources {
        val properties = mapOf("version" to project.version, "description" to project.description)

        inputs.properties(properties)
        filesMatching("paper-plugin.yml") {
            expand(properties)
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        // In case we need to include dependencies in the future
        configurations = listOf()
    }

    runServer {
        minecraftVersion("1.21.11")
    }
}

