plugins {
    java

    alias(libs.plugins.loom)
    alias(libs.plugins.loom.vineflower)
}

group = "dev.isxander"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.isxander.dev/snapshots")
    maven("https://jitpack.io")
    maven("https://maven.quiltmc.org/repository/release")
    maven("https://maven.terraformersmc.com/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.ladysnake.org/releases")
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

    include(modImplementation("com.github.0x3C50:Renderer:master-SNAPSHOT")!!)
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

