plugins {
    java
    alias(libs.plugins.loom)
}

group = "dev.isxander"
version = "3.0.4"

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://maven.isxander.dev/releases")
        }
        filter {
            includeGroup("dev.isxander")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.quiltmc.org/repository/release")
        }
        filter {
            includeGroup("org.quiltmc.parsers")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.terraformersmc.com/releases")
        }
        filter {
            includeGroup("com.terraformersmc")
        }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.ladysnake.org/releases")
        }
        filter {
            includeGroup("com.github.0x3C50")
            includeGroup("io.github.ladysnake")
        }
    }
}

val minecraftVersion = libs.versions.minecraft.get()

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
    })
    modImplementation(libs.fabric.loader)

    libs.fabric.api.let {
        modImplementation(it)
        include(it)
    }

    libs.yet.another.config.lib.let {
        modImplementation(it)
        include(it)
    }

    libs.quilt.json5.let {
        implementation(it)
        include(it)
    }

    "com.github.0x3C50:Renderer:e5d9998655".let {
        modImplementation(it)
        include(it)
    }
}

java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    processResources {
        val modId: String by project
        val modName: String by project
        val modDescription: String by project
        val githubProject: String by project

        inputs.property("id", modId)
        inputs.property("group", project.group)
        inputs.property("name", modName)
        inputs.property("description", modDescription)
        inputs.property("version", project.version)

        filesMatching(listOf("fabric.mod.json", "quilt.mod.json")) {
            expand(
                "id" to modId,
                "group" to project.group,
                "name" to modName,
                "description" to modDescription,
                "version" to project.version,
            )
        }
    }
    
    remapJar {
        archiveClassifier.set("fabric-$minecraftVersion")   
    }
    
    remapSourcesJar {
        archiveClassifier.set("fabric-$minecraftVersion-sources")   
    }
}

java {
    withSourcesJar()   
}

