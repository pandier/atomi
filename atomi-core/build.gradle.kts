plugins {
    id("atomi.java-conventions")
    id("atomi.publication-conventions")
}

dependencies {
    api(libs.adventure.api)
    implementation(libs.gson)
    implementation(libs.adventure.text.serializer.gson)
}
