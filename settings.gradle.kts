pluginManagement {
    repositories {
        maven("https://mirrors.cloud.tencent.com/repo/")
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://maven.aliyun.com/nexus/content/repositories/jcenter")
        maven("https://maven.aliyun.com/nexus/content/repositories/releases/")
        maven("https://nexus.plaso.cn/repository/maven-public/")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://www.jitpack.io")
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://mirrors.cloud.tencent.com/repo/")
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://maven.aliyun.com/nexus/content/repositories/jcenter")
        maven("https://maven.aliyun.com/nexus/content/repositories/releases/")
        maven("https://nexus.plaso.cn/repository/maven-public/")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://jitpack.io")
        google()
        mavenCentral()
    }
}

rootProject.name = "TifangTeacherClientAndroid"
include(":app")
 