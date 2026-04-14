package com.buildx.ide.model

data class GitHubWorkflow(
    val name: String,
    val path: String,
    val content: String,
    val lastModified: Long = System.currentTimeMillis()
)

data class BuildConfig(
    val projectName: String,
    val projectType: ProjectType = ProjectType.ANDROID,
    val buildType: BuildType = BuildType.DEBUG,
    val packageName: String = "",
    val minSdk: Int = 26,
    val targetSdk: Int = 34,
    val compileSdk: Int = 34,
    val versionCode: Int = 1,
    val versionName: String = "1.0.0",
    val dependencies: List<Dependency> = emptyList(),
    val githubToken: String = "",
    val repositoryName: String = ""
)

data class Dependency(
    val name: String,
    val version: String,
    val type: DependencyType = DependencyType.IMPLEMENTATION
)

enum class ProjectType {
    ANDROID, KOTLIN, JAVA, FLUTTER
}

enum class BuildType {
    DEBUG, RELEASE
}

enum class DependencyType {
    IMPLEMENTATION, API, COMPILE_ONLY, TEST_IMPLEMENTATION
}

data class ProjectFile(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val lastModified: Long = System.currentTimeMillis(),
    val children: List<ProjectFile>? = null
)

data class BuildOutput(
    val id: String,
    val status: BuildStatus = BuildStatus.PENDING,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val output: String = "",
    val errors: List<String> = emptyList(),
    val artifacts: List<String> = emptyList()
)

enum class BuildStatus {
    PENDING, RUNNING, SUCCESS, FAILED, CANCELLED
}

data class GitRepository(
    val url: String,
    val branch: String = "main",
    val localPath: String,
    val isCloned: Boolean = false
)

data class EditorSettings(
    val fontSize: Int = 14,
    val tabSize: Int = 4,
    val useSpaces: Boolean = true,
    val showLineNumbers: Boolean = true,
    val highlightCurrentLine: Boolean = true,
    val enableAutoComplete: Boolean = true,
    val theme: EditorTheme = EditorTheme.DARK,
    val wordWrap: Boolean = false
)

enum class EditorTheme {
    DARK, LIGHT, MONOKAI, SOLARIZED_DARK, SOLARIZED_LIGHT
}
