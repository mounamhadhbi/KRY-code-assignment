package se.kry.codetest.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static se.kry.codetest.service.ServiceStatus.*;

public class Service {

    private final URL url;
    private String name;
    private ServiceStatus status;
    private LocalDateTime createdDateTime;


    private Service(URL url, String name, ServiceStatus status, LocalDateTime createdDateTime) {
        this.url = url;
        this.name = name;
        this.status = status;
        this.createdDateTime = createdDateTime;
    }

    public URL getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service that = (Service) o;
        return url.equals(that.url) &&
                Objects.equals(name, that.name) &&
                status == that.status &&
                Objects.equals(createdDateTime, that.createdDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, name, status, createdDateTime);
    }

    public Service status(boolean succeeded) {
        this.status = succeeded ? OK : FAILED;
        return this;
    }

    public static class Builder {
        private final URL url;
        private String name;
        private ServiceStatus checkedStatus;
        private LocalDateTime createdDateTime;

        public Builder(String url) throws MalformedURLException {
            this.url = new URL(url);
            this.createdDateTime = now();
            this.checkedStatus = NOT_CHECKED_YET;

        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withCheckedStatus(ServiceStatus checkedStatus) {
            this.checkedStatus = checkedStatus;
            return this;
        }

        public Builder withCreatedDateTime(String createdDateTime) {
            this.createdDateTime = LocalDateTime.parse(createdDateTime, ISO_DATE_TIME);
            return this;
        }

        public Service build() {
            return new Service(this.url, this.name, this.checkedStatus, this.createdDateTime);
        }

    }

}
