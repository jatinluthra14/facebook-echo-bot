# Facebook echo bot using Vert.x
![Build Status](https://travis-ci.org/jboss-outreach/facebook-echo-bot.svg?branch=master)

This project shows how to deploy a facebook echo bot Vert.x 3 applications to Heroku. The same application can be deployed using 3 approaches:

* Using a one-click badge
* Using the maven plugin
* Using the git interface

If you use a fat-jar then deploying on heroku is as simple as one click. The only requirement is to create the Heroku specific [Procfile](../Procfile) with a `Dyno` of type web.

## Setting up the project
1. Install [Git](https://git-scm.com/)
2. Install [Maven](https://maven.apache.org/)
3. Clone the project:
```sh-session
$ git clone https://github.com/jboss-outreach/facebook-echo-bot
```
4. If deploying the app via [Heroku](#deploying-on-heroku), install the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli).

## Deploying the project

### Deploying on Heroku
[Heroku](https://www.heroku.com/) is a cloud platform that allows developers to build, run and operate applications entirely in the cloud. A limited, free tier is available for trying out this app.

Install the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli) and deploy the app using the [Heroku Maven Plugin](https://devcenter.heroku.com/articles/deploying-java-applications-with-the-heroku-maven-plugin):
```sh-session
$ heroku create
$ mvn package heroku:deploy
```

### Deploying on a local environment ([localhost](https://en.wikipedia.org/wiki/Localhost))
Build the app with [Maven](https://maven.apache.org/); set up the HTTPS tunnel and deploy the app with [Ngrok](https://ngrok.com/):
```sh-session
$ mvn clean package 
$ java -jar target/facebook-echo-bot-3.5.0-jar-with-dependencies.jar
$ ./ngrok http 8080
```

## Environment properties

4 properties must be configured before the app can be accessed:
```
 facebook.verify.token    # Decided by you. This has to be the same for both the app and Facebook.
 facebook.access.token    # This is used to send pi hits to Facebook on your behalf.
 http.port                # This is 8080 by default.
 http.address             # This is 0.0.0.0 by default.
```

One method of setting the properties is by the command: `-D<prop-name>=<prop-val>`.
Example:
```
$ java -Dhttp.port=$PORT -jar target/facebook-echo-bot-3.5.0-jar-with-dependencies.jar
```

When creating a project of your own, you'll need to borrow from the [`Procfile`](https://github.com/jboss-outreach/facebook-echo-bot/blob/master/Procfile) in the root directory of this project, and the `MAVEN_CUSTOM_OPTS` will only be necessary if your app is not the primary module of your Maven project.

## Accessing the app

The app's web hook is at `<host>:<port>/webhook`.
For Heroku-deployed apps, it is at `<app-name>.herokuapp.com/webhook`.

## Further reading and documentation

### Setting up your Facebook app
https://developers.facebook.com/docs/messenger-platform/getting-started/app-setup

## Contributing to the project

1. Fork and clone this repository.
2. Make changes to your local copy of this repository.
3. Commit and push your local changes to your fork on GitHub.
4. Review the changes on your fork.
5. Create a pull request to merge your changes into this repository.
