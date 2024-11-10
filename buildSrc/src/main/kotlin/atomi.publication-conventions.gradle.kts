import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    pom {
        name.set(project.name)
        description.set(project.description)
        url.set("https://github.com/pandier/atomi")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/license/MIT")
            }
        }
        developers {
            developer {
                id.set("pandier")
                name.set("Pandier")
                url.set("https://github.com/pandier")
            }
        }
        scm {
            url.set("https://github.com/pandier/atomi")
            connection.set("scm:git:git://github.com/pandier/atomi.git")
            developerConnection.set("scm:git:ssh://git@github.com/pandier/atomi.git")
        }
    }
}
