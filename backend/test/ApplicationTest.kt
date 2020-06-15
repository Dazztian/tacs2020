import com.utn.tacs.module
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals


class ApplicationTest {

    @Test
    fun testHealthCheck() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/configuration")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Application running", response.content)
        }
    }
}

