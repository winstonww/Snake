plugins {
    kotlin("js") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"

}
group = "me.winstonww"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
    maven {
        url =  uri("https://kotlin.bintray.com/js-externals")
    }
}
dependencies {
    testImplementation(kotlin("test-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
    implementation( "com.google.code.gson:gson:2.8.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
}
kotlin {
    js {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}