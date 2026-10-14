import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Same Kotlin version as flow-viewmodel-lessons, already cached on this machine
    kotlin("jvm") version "2.2.10"
}

// No dependencies on purpose: OOP is pure Kotlin. Compose lives in the .md docs only.

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

fun verifyTask(name: String, mode: String, desc: String) =
    tasks.register<JavaExec>(name) {
        group = "verification"
        description = desc
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("tools.VerifyKt")
        args(mode, project.projectDir.absolutePath)
    }

verifyTask("verifyOutputs", "outputs", "Run every lesson and diff against expected-output/")
verifyTask("updateOutputs", "update", "Regenerate expected-output/ from current lesson output")
verifyTask("verifyExercises", "exercises", "Stubs must print FAIL without crashing; solutions must be all PASS")
verifyTask("verifySelfTest", "selftest", "Self-test of the verifier output normalization")

tasks.register("verifyAll") {
    group = "verification"
    dependsOn("build", "verifySelfTest", "verifyOutputs", "verifyExercises")
}
