import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("atomi.java-conventions")
    id("atomi.publication-conventions")
    id("org.spongepowered.gradle.plugin") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.4"
}

dependencies {
    compileOnly(libs.brigadier)
    api(project(":atomi-core"))
}

sponge {
    apiVersion("11.0.0")
    license("MIT")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("atomi") {
        displayName("Atomi")
        entrypoint("io.github.pandier.atomi.sponge.internal.SpongeAtomiPlugin")
        description(project.description)
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

configurations.spongeRuntime {
    resolutionStrategy {
        eachDependency {
            if (target.name == "spongevanilla") {
                useVersion("1.20.6-11.0.0")
            }
        }
    }
}
