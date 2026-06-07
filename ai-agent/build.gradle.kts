plugins {
    id("java-library")
}

group = "com.learningAssistant"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.openai:openai-java:1.5.0")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.apache.pdfbox:pdfbox:3.0.2")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("net.sourceforge.tess4j:tess4j:5.10.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
