plugins {
    id("com.android.application")
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    defaultConfig {
        applicationId = "com.github.kr328.launchblocker"
        
        minSdkVersion(26)
        targetSdkVersion(29)
        
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        maybeCreate("release").apply {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    compileOnly(project(":hideapi"))
}

task("assembleJar", type = Jar::class) {
    from(zipTree(buildDir.resolve("outputs/apk/release/module-release-unsigned.apk")))
    include("classes.dex")

    destinationDirectory.set(buildDir.resolve("outputs/jars"))
    archiveFileName.set("boot-riru-launch-blocker.jar")
}

task("assembleMagisk", type = Zip::class) {
    from(zipTree(buildDir.resolve("outputs/apk/release/module-release-unsigned.apk"))) {
        include("assets/**", "lib/arm64-v8a/**", "lib/armeabi-v7a/**")
        eachFile {
            path = when {
                path.startsWith("lib/arm64-v8a/") ->
                    path.replace("lib/arm64-v8a/", "system/lib64/")
                path.startsWith("lib/armeabi-v7a/") ->
                    path.replace("lib/armeabi-v7a/", "system/lib/")
                path.startsWith("assets/") ->
                    path.replace("assets/", "")
                else ->
                    path
            }
        }
    }
    from(buildDir.resolve("outputs/jars/boot-riru-launch-blocker.jar")) {
        into("system/framework/")
    }

    destinationDirectory.set(buildDir.resolve("outputs"))
    archiveFileName.set("riru-launch-blocker.zip")
}

afterEvaluate {
    tasks["assembleJar"].dependsOn(tasks["assembleRelease"])
    tasks["assembleMagisk"].dependsOn(tasks["assembleJar"])
}