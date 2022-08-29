import com.mattmx.ktguis.utils.GitUpdateChecker

fun main() {
    val checker = GitUpdateChecker(
        "https://api.github.com/repos/Matt-MX/AnnouncerVelocity/releases/latest",
        "1.2.1",
    ) { outdated, latest ->
        println("$outdated : $latest")
    }
}