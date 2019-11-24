package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.WorkerExecutor;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import se.kry.codetest.core.connector.DBConnector;
import se.kry.codetest.poller.BackgroundPoller;
import se.kry.codetest.service.ServiceRepository;
import se.kry.codetest.service.ServiceRouter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {

    private Logger logger = Logger.getLogger(MainVerticle.class.getName());

    @Override
    public void start(Future<Void> startFuture) {
        DBConnector connector = new DBConnector(vertx);
        ServiceRepository repository = new ServiceRepository.Impl(connector);
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        WorkerExecutor executor = vertx.createSharedWorkerExecutor("poller-service-worker");
        executor.executeBlocking(promise -> {
            vertx.setPeriodic(1000 * 60, timerId -> new BackgroundPoller().pollServices(repository, vertx));
            promise.complete(null);
        }, res -> logger.fine("The result of poller service is: " + res.result()));

        executor.close();
        new ServiceRouter(router, repository).defineRoutes();
        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        logger.log(Level.CONFIG, "KRY code test service started");
                        startFuture.complete();
                    } else {
                        startFuture.fail(result.cause());
                    }
                });
    }
}



