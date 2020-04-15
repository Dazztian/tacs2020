import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import junit.framework.Assert.assertEquals
import org.junit.Test


class ApplicationTest {
    @Test
    fun testRequests() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/countries")) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }
}

