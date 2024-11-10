plugins {
    id("atomi.java-conventions")
    id("atomi.publication-conventions")
    id("com.gradleup.shadow") version "8.3.4"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api(project(":atomi-core"))

    compileOnly(libs.spigot.api)

    implementation(libs.adventure.text.serializer.legacy)
    implementation(libs.commandapi.bukkit)
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to version, "description" to description)
    }
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    dependencies {
        // We don't need these dependencies, because they are included in Spigot
        exclude(dependency("com.google.code.gson:gson"))
        exclude(dependency("com.google.errorprone:"))
        exclude(dependency("org.jetbrains:annotations"))
        exclude("LICENSE")
    }

    relocate("dev.jorel.commandapi", "io.github.pandier.atomi.spigot.commandapi")
}
