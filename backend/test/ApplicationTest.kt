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
        with(handleRequest(HttpMethod.Get, "/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Application running", response.content)
        }
    }

    @Test
    fun testLogin() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Post, "/api/login")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("login", response.content)
        }
    }

    @Test
    fun testAuthGoogle() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Post, "/auth/google")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Oauth", response.content)
        }
    }

    @Test
    fun testLogout() = withTestApplication(Application::module) {
        with(handleRequest(HttpMethod.Get, "/logout")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("logout", response.content)
        }
    }


}

