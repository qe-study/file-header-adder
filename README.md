# file-header-adder

一个轻量的 IntelliJ Platform 插件，在编辑器右键菜单中新增 **「Add File Header」**，自动在文件顶部插入作者、日期、文件名、版权（公司）等头注释，省去手写模板的麻烦。

支持 Java / Kotlin / Groovy 以及其他通用语言，并可在 **Settings → Tools → File Header** 中自定义内容。

## ✨ 功能特性

- **一键插入文件头**：在任意文本编辑器中右键 → `Add File Header` 即可生成注释头。
- **多语言样式适配**：
  - Java / Kotlin / Groovy 使用 `/** */` 风格；
  - 其他语言使用通用 `/* */` 风格。
- **智能定位**：当文件以 `#!`（shebang）开头时（如 Python 脚本），自动跳过 shebang 行，在其后插入头注释。
- **可配置**：作者名、公司名、日期格式均可在设置面板中修改并持久化。
- **应用级持久化**：配置保存在 `FileHeaderSettings.xml`，IDE 重启后自动恢复。

## 📦 生成示例

### Java / Kotlin / Groovy 风格

```java
/**
author qe-present
company ACME Inc.
date 2026-07-15
file HelloWorld.java
*/

```

### 通用风格（Python 等）

```python
/*
 * Author:    qe-present
 * Company:   ACME Inc.
 * Date:      2026-07-15
 * File:      main.py
 */
```

## 🚀 安装与构建

### 环境要求

- JDK 17+
- IntelliJ IDEA（推荐最新版）
- Gradle（项目自带 `gradlew`，无需单独安装）

### 从源码构建

```bash
# 克隆仓库
git clone https://github.com/qe-present/file-header-adder.git
cd file-header-adder

# 构建插件 zip
./gradlew buildPlugin
```

构建产物位于 `build/distributions/` 下，可在 IntelliJ 中通过
`Settings → Plugins → ⚙️ → Install Plugin from Disk…` 安装。

### 在 IDE 中调试运行

```bash
./gradlew runIde
```

该命令会启动一个带有本插件的沙箱版 IntelliJ IDEA，便于即时调试。

## 🎯 使用方式

1. 打开任意源文件（如 `.java` / `.kt` / `.py`）。
2. 在编辑器中 **右键**，选择 **`Add File Header`**。
3. 文件头注释自动插入到文件顶部（含 shebang 处理）。

## ⚙️ 配置

路径：**Settings (Preferences) → Tools → File Header**

| 配置项       | 说明                                | 默认值             |
| ------------ | ----------------------------------- | ------------------ |
| Author       | 作者名                              | 当前系统用户名     |
| Company      | 公司名（为空则不输出公司行）        | 空                 |
| Date Format  | 日期格式（`SimpleDateFormat` 语法） | `yyyy-MM-dd`       |

## 🛠 技术栈

- **语言**：Kotlin
- **平台**：IntelliJ Platform（基于 [IntelliJ Platform Gradle Plugin](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html)）
- **最低兼容 IDE**：IntelliJ IDEA 2025.2

## 📝 项目结构

```
src/main/kotlin/com/github/qepresent/fileheaderadder/
├── actions/
│   └── FileHeader.kt                 # 右键菜单 Action：生成并插入文件头
└── settings/
    ├── HeaderSettings.kt             # 持久化设置模型（PersistentStateComponent）
    ├── HeaderSettingsConfigurable.kt # 设置面板入口
    └── HeaderSettingsComponent.kt    # 设置面板 UI
```

## 📜 License

本项目基于 [MIT License](./LICENSE) 开源。
