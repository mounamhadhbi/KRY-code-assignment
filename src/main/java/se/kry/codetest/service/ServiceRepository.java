package se.kry.codetest.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import se.kry.codetest.core.connector.DBConnector;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public interface ServiceRepository {
    Logger logger = Logger.getLogger(ServiceRepository.class.getName());

    Future<Services> getAllServices();

    Future<ResultSet> updateStatus(Service service);

    Future<ResultSet> addService(Service service);

    Future<ResultSet> delete(String url);

    Future<ResultSet> findByURL(String url);

    class Impl implements ServiceRepository {

        private DBConnector connector;

        public Impl(DBConnector connector) {
            this.connector = connector;
        }

        @Override
        public Future<Services> getAllServices() {
            List<Service> services = new ArrayList<>();
            Future<Services> fut = Future.future();

            connector.query("SELECT * FROM service ORDER BY name").setHandler(res -> {
                        if (res != null && res.result() != null) {
                            for (JsonObject entries : res.result().getRows()) {
                                try {
                                    Service service = buildServiceFrom(entries);
                                    services.add(service);
                                } catch (MalformedURLException e) {
                                    logger.log(
                                            Level.SEVERE, "MalFormed URL Of Service: {0} ", entries.getString("url"));
                                }
                            }
                            fut.complete(Services.of(services));
                        }
                    }
            );
            return fut;
        }

        @Override
        public Future<ResultSet> findByURL(String url) {
            return connector.query("SELECT * FROM service WHERE url = ?", new JsonArray().add(url))
                    .setHandler(res -> log(res, "FIND SERVICE", url));
        }

        @Override
        public Future<ResultSet> addService(Service service) {
            return connector.query(
                    "INSERT INTO service (url, name, status, created) VALUES (?, ?, ?, ?)",
                    new JsonArray().add(service.getUrl().toString())
                            .add(service.getName())
                            .add(service.getStatus().name())
                            .add(service.getCreatedDateTime().format(ISO_DATE_TIME))
            ).setHandler(res -> log(res, "ADD SERVICE", service.getUrl().toString()));
        }

        @Override
        public Future<ResultSet> delete(String url) {
            return connector.query("DELETE FROM service WHERE url = ?", new JsonArray().add(url))
                    .setHandler(res -> log(res, "DELETE SERVICE", url));
        }

        @Override
        public Future<ResultSet> updateStatus(Service service) {
            return connector.query(
                    "UPDATE service SET status = ? WHERE url = ?",
                    new JsonArray().add(service.getStatus()).add(service.getUrl().toString()))
                    .setHandler(res -> log(res, "UPDATE STATUS", service.getUrl().toString()));
        }


        private void log(AsyncResult<ResultSet> res, String action, String serviceURL) {
            if (res.succeeded()) {
                logger.log(Level.FINE, String.format("Success to %s for URL %s.", action, serviceURL));
            } else {
                logger.log(
                        Level.SEVERE, String.format("Failed to %s for URL %s.", action, serviceURL));
            }
        }

        private Service buildServiceFrom(JsonObject serviceRow) throws MalformedURLException {
            return new Service.Builder(serviceRow.getString("url"))
                    .withCheckedStatus(ServiceStatus.valueOf(serviceRow.getString("status")))
                    .withName(serviceRow.getString("name"))
                    .withCreatedDateTime(serviceRow.getString("created"))
                    .build();
        }
    }


}
