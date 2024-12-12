import java.io.File
import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Basics"
include(":app")
include(":lib_basics")
include(":lib_update")





// 读取 local.properties 文件
val propertiesFile = File(rootDir, "local.properties")
val properties = Properties().apply {
    if (propertiesFile.exists()) {
        load(propertiesFile.inputStream())
    }
}

// 从多处尝试读取账号信息
val ALIYUN_USERNAME: String = gradle.startParameter.projectProperties["ALIYUN_USERNAME"] ?: properties.getProperty("ALIYUN_USERNAME") ?: System.getenv("ALIYUN_USERNAME")
val ALIYUN_PASSWORD: String = gradle.startParameter.projectProperties["ALIYUN_PASSWORD"] ?: properties.getProperty("ALIYUN_PASSWORD") ?: System.getenv("ALIYUN_PASSWORD")

// 将账号信息定义为全局变量，便于在子模块中使用
gradle.extra["ALIYUN_USERNAME"] = ALIYUN_USERNAME
gradle.extra["ALIYUN_PASSWORD"] = ALIYUN_PASSWORD

// 配置release与snapshot仓库
dependencyResolutionManagement {
    repositories {
        maven {
            credentials {
                username = ALIYUN_USERNAME
                password = ALIYUN_PASSWORD
            }
            url = uri("https://packages.aliyun.com/623161cc52a8ac963194f2a3/maven/atoto-android-release")
        }
        maven {
            credentials {
                username = ALIYUN_USERNAME
                password = ALIYUN_PASSWORD
            }
            url = uri("https://packages.aliyun.com/623161cc52a8ac963194f2a3/maven/atoto-android-snapshot")
        }

    }
}


