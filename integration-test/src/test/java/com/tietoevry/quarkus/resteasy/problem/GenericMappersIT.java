package com.tietoevry.quarkus.resteasy.problem;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GenericMappersIT {

    static final String SAMPLE_TITLE = "I'm a teapot";
    static final String SAMPLE_DETAIL = "A small one";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void runtimeExceptionShouldReturn500() {
        given()
                .queryParam("message", SAMPLE_DETAIL)
                .get("/throw/generic/runtime-exception")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body("instance", equalTo("/throw/generic/runtime-exception"))
                .body("title", equalTo(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("status", equalTo(INTERNAL_SERVER_ERROR.getStatusCode()))
                .body("detail", nullValue())
                .body("stacktrace", nullValue());
    }

    @Test
    void httpProblemShouldReturnHeaders() {
        final int status = 429;
        given()
                .queryParam("status", status)
                .queryParam("title", SAMPLE_TITLE)
                .queryParam("detail", SAMPLE_DETAIL)
                .get("/throw/generic/http-problem")
                .then()
                .log().all()
                .statusCode(status)
                .header("X-RFC7807", equalTo("IsAlive"))
                .body("title", equalTo(SAMPLE_TITLE))
                .body("status", equalTo(status))
                .body("detail", equalTo(SAMPLE_DETAIL))
                .body("stacktrace", nullValue());
    }

}
