plugins {
    java

    alias(libs.plugins.loom)
    alias(libs.plugins.loom.quiltflower)
}

group = "dev.isxander"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.isxander.dev/releases")
    maven("https://jitpack.io")
    maven("https://maven.quiltmc.org/repository/release")
}

val minecraftVersion = libs.versions.minecraft.get()

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        mappings("org.quiltmc:quilt-mappings:$minecraftVersion+build.${libs.versions.quilt.mappings.get()}:intermediary-v2")
        officialMojangMappings()
    })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    modImplementation(libs.yet.another.config.lib)

    libs.mixin.extras.let {
        implementation(it)
        include(it)
        annotationProcessor(it)
    }

    libs.quilt.json5.let {
        implementation(it)
        include(it)
    }
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

