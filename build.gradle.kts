import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.30.0"
}

description = "commons library :3"

subprojects {

    apply(plugin = "java-library")
    plugins.apply("com.vanniktech.maven.publish")

    group = "io.github.canary-prism"

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

        signAllPublications()

        pom {

            name = project.name
            description = project.description

            url = "https://github.com/Canary-Prism/commons"

            licenses {
                license {
                    name = "The Apache License, Version 2.0"
                    url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            }

            developers {
                developer {
                    id = "Canary-Prism"
                    name = "Canary Prism"
                    email = "canaryprsn@gmail.com"
                }
            }

            scm {
                url = "https://github.com/Canary-Prism/commons"
                connection = "scm:git:git://github.com/Canary-Prism/commons.git"
                developerConnection = "scm:git:ssh://git@github.com:Canary-Prism/commons.git"
            }
        }
    }

    repositories {
        mavenCentral()
    }

    java {
        modularity.inferModulePath = true
    }


    tasks.javadoc {
        javadocTool = javaToolchains.javadocToolFor {
            languageVersion = JavaLanguageVersion.of(23)
        }

        (options as StandardJavadocDocletOptions).tags(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:"
        )
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        compileOnly("org.jetbrains:annotations:24.0.0")
    }

    tasks.test {
        useJUnitPlatform()
    }


    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
    }

}
