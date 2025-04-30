# YXT SDK使用说明文档

## 1.概述

YXT SDK 是一个用于集成伯索云教育平台功能的开发工具包，提供了实时课堂、作业巩固核心功能。本文档将指导您如何集成和使用该SDK。

## 2. 集成准备
### 2.1 环境要求
- Android 开发环境
- Android 7.0 （minSdk = 24）

### 2.2 权限配置
在 `AndroidManifest.xml` 中添加以下权限：
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 2.3 项目依赖

1.在项目级build.gradle中添加仓库

```
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'https://nexus.plaso.cn/repository/maven-public/' }
        maven { url 'https://jitpack.io' }
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'https://nexus.plaso.cn/repository/maven-public/' }
        maven { url 'https://jitpack.io' }
    }
}
```

settings.gradle.kts配置方式：

```
pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://nexus.plaso.cn/repository/maven-public/")
        maven("https://www.jitpack.io")
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://nexus.plaso.cn/repository/maven-public/")
        maven("https://jitpack.io")
        google()
        mavenCentral()
    }
}
```

2.在模块级build.gradle中添加依赖库

```
dependencies {
    implementation 'cn.plaso:yxtsdk:2.0.0-beta.6'
}
```

build.gradle.kts配置方式：

```
dependencies {
    implementation("cn.plaso:yxtsdk:2.0.0-beta.6")
}
```



## 3. SDK接入方式

### 3.1 SDK初始化

使用SDK前需要对SDK进行初始化，在 Application 的 onCreate 方法中调用以下代码（SDK初始化无需Token）：

```

class DemoMainApplication : Application() {
    private var mDemoMainApplication: DemoMainApplication? = null

    override fun onCreate() {
        super.onCreate()
        mDemoMainApplication = this
        
        // 回调接口处理初始化成功or失败场景
        YxtSDK.init(context, "您的机构名称，如：plaso",  object : SDKInitCallback {
            override fun onInitSuccess() {
                //初始化成功调用
            }
            override fun onInitError(code: Int?) {
                //初始化失败调用
            }
        })
    }
}
```

也可不实现回调接口：

```
YxtSDK.init(this, "您的机构名称，如：plaso")
```

### 3.2 SDK功能模块调用

#### 3.2.1 获取伯索账号Token

使用本 SDK 功能模块，需提供用户在 YXT 平台的合法 Token，用于身份认证与权限校验。

Token 具体获取方式请参考：[YXT API文档-Open API帮助文档-用户信息管理-用户访问Token](https://app.apifox.com/project/3466838) 

注意事项：

- 请确保 Token 有效且具备对应权限。


- Token 泄露可能导致安全风险，请妥善保管。

#### 3.2.2 Token 初始化与用户角色配置
在获取到有效的 YXT 平台 Token 后，需调用以下代码完成 SDK 的 Token 初始化及用户角色配置：

```
/**
 * 初始化Token并设置用户角色信息
 * 
 * @param token     通过YXT平台认证获取的有效Token（非空）
 * @param userType  用户角色类型，大小写敏感，限定值：
 *                  - "teacher" 教师角色
 *                  - "student" 学生角色
 * @param callback  初始化结果回调（可为null）
 *                  - onInitSuccess：
 *				     表示Token及用户角色已通过验证，此时可安全调用SDK其他功能。
 *                  - onInitSuccess：
 *				     需根据错误码进行差异化处理，建议提示用户重新登录或联系技术支持。
 */
YxtSDK.updateToken(bsToken, userType, object : SDKInitCallback {
    override fun onInitSuccess() {
        // Token初始化成功回调
        // 可在此执行后续业务逻辑
    }

    override fun onInitError(code: Int?) {
        // Token初始化失败回调
    }
})
```

#### 3.2.3 启动实时课堂页面

 * 前置条件：
     1. 必须完成SDK初始化（YxtSDK.init()）
     2. 必须已通过updateToken()设置有效Token和用户角色

```
YxtSDK.startLiveClass(this)
```

#### 3.2.4 启动作业页面

 * 前置条件：
     1. 必须完成SDK初始化（YxtSDK.init()）
     2. 必须已通过updateToken()设置有效Token和用户角色

```
YxtSDK.startHomework(this)
```

### 3.3 用户登出

用户登出，需调用如下代码清理用户在sdk中的用户信息：

```
YxtSDK.doLogout()
```

### 













