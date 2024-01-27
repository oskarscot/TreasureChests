plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.2"
}

group = "scot.oskar.treasurechests"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://storehouse.okaeri.eu/repository/maven-public")
    }
}
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.1.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")

    implementation("org.jetbrains.exposed:exposed-core:0.46.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.46.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.46.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.46.0")

    implementation("org.postgresql:postgresql:42.2.23")

    val okaeriConfigs = "5.0.0-beta.5"
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigs")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.58.0")
}

tasks {

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    shadowJar {
        relocate("org.apache.commons", "scot.oskar.commons")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

        archiveFileName.set("TreasureChests-$version.jar")
    }

    jar {
        dependsOn(shadowJar)
        dependsOn(test)
    }

    runServer {
        dependsOn(shadowJar)
        minecraftVersion("1.20.4")
    }
}