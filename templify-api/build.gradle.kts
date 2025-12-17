plugins {
    `java-library`
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "templify-api"
            pom {
                name.set("Templify API")
                description.set("Public API (models, enums, etc.) for Templify templating & placeholder replacement.")
            }
        }
    }
}