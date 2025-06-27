package songbird.apollo.data

class ParseBackendResponseException : RuntimeException()

class BackendException(
    val code: Int,
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)