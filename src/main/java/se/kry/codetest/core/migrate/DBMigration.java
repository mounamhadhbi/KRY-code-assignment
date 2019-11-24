package se.kry.codetest.core.migrate;

import io.vertx.core.Vertx;
import se.kry.codetest.core.connector.DBConnector;

public class DBMigration {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DBConnector connector = new DBConnector(vertx);
    connector.query(
            "CREATE TABLE IF NOT EXISTS service (" +
                    "url VARCHAR(128) PRIMARY KEY NOT NULL, " +
                    "name VARCHAR(128), " +
                    "created VARCHAR(128), " +
                    "status VARCHAR(6) " +
                    ")").setHandler(done -> {
      if(done.succeeded()){
        System.out.println("completed db migrations");
      } else {
        done.cause().printStackTrace();
      }
      vertx.close(shutdown -> {
        System.exit(0);
      });
    });
  }
}
