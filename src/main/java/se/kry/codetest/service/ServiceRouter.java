package se.kry.codetest.service;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceRouter {


    private static final String MESSAGE = "message";
    private static final String CONTENT_TYPE = "content-type";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String APPLICATION_JSON = "application/json";
    private static final String URL_FIELD = "url";
    private static final String SERVICE_URI = "/service";
    private static final String NAME_FIELD = "name";
    private static final String STATUS_FIELD = "status";


    final Router router;
    final ServiceRepository repository;

    public ServiceRouter(Router router, ServiceRepository repository) {
        this.router = router;
        this.repository = repository;
    }

    public void defineRoutes() {
        defineRoute();
        defineGetAll();
        defineCreate();
        defineDelete();

    }

    private void defineDelete() {
        router.delete(SERVICE_URI).handler(req -> {
            JsonObject jsonBody = req.getBodyAsJson();
            String url = jsonBody.getString(URL_FIELD);
            repository.findByURL(url).setHandler(res -> {
                if (res.result() == null) {
                    answerIsKo(req, String.format("The service with URL %s does not exists", url));
                } else {
                    repository.delete(url).setHandler(deletion -> {
                        if (deletion.succeeded()) {
                            answerIsOk(req, String.format("The service with URL %s is deleted", url));
                        } else {
                            answerIsKo(req, String.format("The service with URL %s cannot be deleted", url));
                        }
                    });
                }
            });
        });
    }

    private void defineCreate() {
        router.post(SERVICE_URI).handler(req -> {
            JsonObject jsonBody = req.getBodyAsJson();
            String url = jsonBody.getString(URL_FIELD);
            String name = jsonBody.getString(NAME_FIELD);
            try {
                Service service = new Service.Builder(url).withName(name).build();
                repository.addService(service).setHandler(creation -> {
                    if (creation.succeeded()) {
                        answerIsOk(req, String.format("The service with URL %s is created", url));
                    } else {
                        answerIsKo(req, String.format("The service with URL %s cannot be created", url));
                    }
                });
                ;
            } catch (MalformedURLException e) {
                answerIsKo(req, String.format("Mal formed URL %s.", url));
            }

        });
    }

    private void defineGetAll() {
        router.get(SERVICE_URI).handler(req -> repository.getAllServices().setHandler(services -> {
            List<JsonObject> jsonServices = services.result().stream().map(service ->
                    new JsonObject()
                            .put(NAME_FIELD, service.getName())
                            .put(STATUS_FIELD, service.getStatus())
                            .put(URL_FIELD, service.getUrl().toString()))
                    .collect(Collectors.toList());
            answerWithArray(req, jsonServices);

        }));
    }

    private void defineRoute() {
        router.route("/*").handler(StaticHandler.create());
    }


    private void answerIsOk(RoutingContext req, String s) {
        req.response()
                .putHeader(CONTENT_TYPE, TEXT_PLAIN)
                .end(new JsonObject().put(MESSAGE, "OK! " + s).encode());
    }

    private void answerIsKo(RoutingContext req, String s) {
        req.response()
                .putHeader(CONTENT_TYPE, TEXT_PLAIN)
                .end(new JsonObject().put(MESSAGE, "KO! " + s).encode());
    }

    private void answerWithArray(RoutingContext req, List<JsonObject> jsonServices) {
        req.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(new JsonArray(jsonServices).encode());
    }

}
