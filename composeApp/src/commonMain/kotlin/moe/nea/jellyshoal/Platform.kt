package moe.nea.jellyshoal

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform