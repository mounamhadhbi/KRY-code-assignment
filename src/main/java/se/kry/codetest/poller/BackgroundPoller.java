package se.kry.codetest.poller;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import se.kry.codetest.service.ServiceRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BackgroundPoller {
    private Logger logger = Logger.getLogger(BackgroundPoller.class.getName());

    public void pollServices(ServiceRepository repository, Vertx vertx) {
        repository.getAllServices().setHandler(res -> res.result().forEach(service -> {
            if (service.getUrl() != null) {
                WebClient client = WebClient.create(vertx);
                client
                        .getAbs(service.getUrl().toString())
                        .send(ar -> {
                            repository.updateStatus(service.status(ar.succeeded()));
                            logger.log(Level.FINE, String.format("Status of URL: %s is: %s.", service.getUrl(), ar.succeeded()));
                        });
            } else {
                logger.log(Level.SEVERE, String.format("URL not defined for service: %s", service.getName()));
            }
        }));
    }
}
