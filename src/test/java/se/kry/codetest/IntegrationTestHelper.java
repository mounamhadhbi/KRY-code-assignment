package se.kry.codetest;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import se.kry.codetest.core.connector.DBConnector;

@ExtendWith(VertxExtension.class)
abstract class IntegrationTestHelper {

    DBConnector db;


    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        db = new DBConnector(vertx);
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", 8080)
                        .put("http.host", "localhost")
                );

        vertx.deployVerticle(new MainVerticle(), options, testContext.succeeding(id -> testContext.completeNow()));
    }


}
