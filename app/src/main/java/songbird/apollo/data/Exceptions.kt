package songbird.apollo.data

import java.io.IOException

class ParseBackendResponseException : RuntimeException()

class BackendException(
    val code: Int,
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

class NoConnectionException : IOException()