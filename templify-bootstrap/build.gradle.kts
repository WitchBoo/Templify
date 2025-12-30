
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.juppiter)
}

repositories {
    mavenCentral()
    maven("https://maven.whereareiam.me/development/")
    maven("https://maven.whereareiam.me/release/")
}

dependencies {
    rootProject.subprojects.forEach { subproject ->
        val path = subproject.path
        if (path != ":templify-bootstrap" && path.startsWith(":templify-")) {
            "implementation"(project(path))
        }
    }

    moduleLibrary(rootProject.libs.guice)
    moduleLibrary(rootProject.libs.configura)
}

tasks.withType<ShadowJar> {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")

    val defaultDestination = rootProject.layout.buildDirectory.dir("libs")

    val customOutputDir = if (project.hasProperty("output")) {
        project.layout.dir(project.provider { File(project.property("output").toString()) })
    } else {
        null
    }

    destinationDirectory.set(customOutputDir ?: defaultDestination)
}

moduleJson {
    main = "me.whereareiam.templify.Templify"
    name = rootProject.name
    version = project.version.toString()
    author = "whereareiam"
    description = "A module for pre-startup processing and modification of service templates before they are deployed"
    runtimeModule = true
}
