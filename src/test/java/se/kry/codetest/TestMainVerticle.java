package se.kry.codetest;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TestMainVerticle extends IntegrationTestHelper {


    @Test
    @DisplayName("Start a web server on localhost responding to path /service on port 8080")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void start_http_server(Vertx vertx, VertxTestContext testContext) {

        WebClient.create(vertx)
                .get(8080, "localhost", "/service")
                .send(response -> testContext.verify(() -> {
                    assertNotNull(response.result());
                    assertEquals(200, response.result().statusCode());
                    JsonArray body = response.result().bodyAsJsonArray();
                    db.query("select * from service").setHandler(res -> {
                        if (res.result() != null) {
                            List<JsonObject> rows = res.result().getRows();
                            assertEquals(rows.size(), body.size());
                        }
                    });
                    testContext.completeNow();
                }));
    }


    @Test
    @DisplayName("Create Service on port 8080 must be ok")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void create_service_is_ok(Vertx vertx, VertxTestContext testContext) {
        JsonObject json = new JsonObject().put("url", "http://tati.com").put("name", "tati");
        WebClient.create(vertx)
                .post(8080, "localhost", "/service")
                .sendJsonObject(json, response -> testContext.verify(() -> {
                    assertNotNull(response.result());
                    assertEquals(200, response.result().statusCode());
                    testContext.completeNow();
                }));

    }


    @Test
    @DisplayName("Create Service on port 8080 must be ko with empty body")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void create_service_is_ko(Vertx vertx, VertxTestContext testContext) {
        WebClient.create(vertx)
                .post(8080, "localhost", "/service")
                .sendJsonObject(null, response -> testContext.verify(() -> {
                    assertNotNull(response.result());
                    assertEquals(500, response.result().statusCode());
                    testContext.completeNow();
                }));

    }

    @Test
    @DisplayName("Delete Service on port 8080 must be ok")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void delete_service_is_ok(Vertx vertx, VertxTestContext testContext) {
        JsonObject json = new JsonObject().put("url", "http://tati.com");
        WebClient.create(vertx)
                .delete(8080, "localhost", "/service")
                .sendJsonObject(json, response -> testContext.verify(() -> {
                    assertNotNull(response.result());
                    assertEquals(200, response.result().statusCode());
                    testContext.completeNow();
                }));

    }


    @Test
    @DisplayName("Delete Service on port 8080 must be ko with empty body")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void delete_service_is_ko(Vertx vertx, VertxTestContext testContext) {
        WebClient.create(vertx)
                .delete(8080, "localhost", "/service")
                .sendJsonObject(null, response -> testContext.verify(() -> {
                    assertNotNull(response.result());
                    assertEquals(500, response.result().statusCode());
                    testContext.completeNow();
                }));

    }

}
