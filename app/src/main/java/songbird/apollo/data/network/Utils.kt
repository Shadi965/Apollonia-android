package songbird.apollo.data.network

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import songbird.apollo.BackendException
import songbird.apollo.NoConnectionException
import songbird.apollo.ParseBackendResponseException
import java.io.IOException
import java.net.ConnectException

internal const val URL = "http://192.168.0.101"

suspend fun <T> wrapRetrofitExceptions(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: JsonDataException) {
        throw ParseBackendResponseException()
    } catch (e: JsonEncodingException) {
        throw ParseBackendResponseException()
    } catch (e: HttpException) {
        throw BackendException(e.code(), e.message, e)
    } catch (e: ConnectException) {
        throw BackendException(0, e.message, e)
    } catch (e: IOException) {
        throw NoConnectionException()
    }
}