// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.spotless)
}

spotless {
  kotlin {
    target("app/src/**/*.kt")
    ktfmt().googleStyle()
  }
  kotlinGradle {
    target("*.gradle.kts", "app/*.gradle.kts")
    ktfmt().googleStyle()
  }
  format("misc") {
    target("*.md", ".gitignore", "gradle/*.toml", "gradle/**/*.properties", "app/src/main/**/*.xml")
    trimTrailingWhitespace()
    endWithNewline()
  }
}
