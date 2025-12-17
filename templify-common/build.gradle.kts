dependencies {
    "compileOnly"(project(":templify-api"))
    "testImplementation"(project(":templify-api"))
}

tasks.test {
    useJUnitPlatform()
}