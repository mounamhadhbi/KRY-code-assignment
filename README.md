# KRY code assignment

One of our developers built a simple service poller.
The service consists of a backend service written in Vert.x (https://vertx.io/) that keeps a list of services (defined by a URL), and periodically does a HTTP GET to each and saves the response ("OK" or "FAIL").

Unfortunately, the original developer din't finish the job, and it's now up to you to complete the thing.
Some of the issues are critical, and absolutely need to be fixed for this assignment to be considered complete.
There is also a wishlist of features in two separate tracks - if you have time left, please choose *one* of the tracks and complete as many of those issues as you can.

Critical issues (required to complete the assignment):

- Whenever the server is restarted, any added services disappear **==> Done** 
- There's no way to delete individual services **==> Done but you must specify the url in the input text**
- We want to be able to name services and remember when they were added **==> Done stored on the DB**
- The HTTP poller is not implemented **==> Done by using WebClient**

Frontend/Web track:
- We want full create/update/delete functionality for services  **==> Done only for create and delete**
- The results from the poller are not automatically shown to the user (you have to reload the page to see results) **==> Done on code.js (refreshServiceList)**
- We want to have informative and nice looking animations on add/remove services   **==> Done I added some colors, I hope that looks nice**

Backend track
- Simultaneous writes sometimes causes strange behavior ==> Not Done
- Protect the poller from misbehaving services (for example answering really slowly) **==> Done by using WorkerExecutor**
- Service URL's are not validated in any way ("sdgf" is probably not a valid service) **==> Done by using URL type**
- A user (with a different cookie/local storage) should not see the services added by another user **==> Not Done**

Spend maximum four hours working on this assignment - make sure to finish the issues you start.

Put the code in a git repo on GitHub and send us the link (niklas.holmqvist@kry.se) when you are done.

Good luck!

# Building
We recommend using IntelliJ as it's what we use day to day at the KRY office.
In intelliJ, choose
```
New -> New from existing sources -> Import project from external model -> Gradle -> select "use gradle wrapper configuration"
```

You can also run gradle directly from the command line:
```
./gradlew clean run
```
