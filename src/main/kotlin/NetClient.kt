import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class NetClient {

    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    val httpClient = HttpClient(OkHttp) {
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.SIMPLE
        }
        expectSuccess = true
        install(ContentNegotiation) {
            json(json, ContentType.Any)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 10000
        }
    }

    suspend inline fun <reified Value : Any> createJsonRequest(
        path: String,
        methodType: HttpMethod = HttpMethod.Get,
        body: Any = EmptyContent,
        headers: List<Pair<String, String>>? = null,
    ): Value =
        createRequest(path, methodType, body, ContentType.Application.Json, headers)

    suspend inline fun <reified Value : Any> createRequest(
        path: String,
        methodType: HttpMethod = HttpMethod.Get,
        body: Any = EmptyContent,
        contentType: ContentType? = null,
        headersList: List<Pair<String, String>>? = null
    ): Value {
        @Suppress("SwallowedException")
        return wrapInExceptionHandler {
            httpClient.request {
                if (!headersList.isNullOrEmpty()) {
                    headers {
                        headersList.forEach { pair ->
                            append(pair.first, pair.second)
                        }
                    }
                }
                method = methodType
                url(path)
                if (contentType != null) contentType(contentType)
                setBody(body)
            }.body()
        }
    }

    inline fun <reified Type : Any> wrapInExceptionHandler(block: () -> Type): Type {
        return try {
            block.invoke()
        } catch (e: ResponseException) {
            throw e
        } catch (e: SerializationException) {
            throw e
        } catch (e: Throwable) {
            throw e
        }
    }
}