plugins {
    id("java")
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.learningAssistant"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":ai-agentic-java"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.apache.pdfbox:pdfbox:3.0.2")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("net.sourceforge.tess4j:tess4j:5.10.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
