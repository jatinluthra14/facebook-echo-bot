package com.example.chatbot;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.client.WebClientOptions;

import java.util.ArrayList;
import java.util.Map;

public class FacebookBotVerticle extends AbstractVerticle {

    private String VERIFY_TOKEN;
    private String ACCESS_TOKEN;

    @Override
    public void start() throws Exception {

        updateProperties();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/webhook").handler(this::verify);
        router.post("/webhook").handler(this::message);

        vertx.createHttpServer().requestHandler(router::accept)
                .listen(
                        Integer.getInteger("http.port", 8080), System.getProperty("http.address", "0.0.0.0"));
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new FacebookBotVerticle());
    }


    private void verify(RoutingContext routingContext) {
        String challenge = routingContext.request().getParam("hub.challenge");
        String token = routingContext.request().getParam("hub.verify_token");
        if (!VERIFY_TOKEN.equals(token)) {
            challenge = "fake";
        }

        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(challenge);
    }

    private void message(RoutingContext routingContext) {

        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end("done");

        final JsonObject hook = routingContext.getBodyAsJson();

        JsonArray entries = hook.getJsonArray("entry");

        entries.getList().forEach( (Object e) -> {

            System.out.println(e.getClass());

            Map entry = (Map) e ;
            ArrayList messagingList = (ArrayList) entry.get("messaging");
            System.out.println(messagingList.getClass());
            messagingList.forEach((Object m) -> {

                Map messaging = (Map) m ;
                Map sender = (Map) messaging.get("sender");
                messaging.put("recipient", sender);
                messaging.remove("sender");

                Map message = (Map) messaging.get("message");
                message.remove("mid");
                message.remove("seq");
                if(message.get("text") != null) {
                    if(message.get("text").toString().startsWith(".urb"))
                    {
                        Urban_Dict urban_dict = new Urban_Dict();
                        String word = new String(message.get("text").toString().replaceFirst(".urb", ""));
                        message.put("text", urban_dict.getMeaning(word));
                        messaging.put("message", message);
                    }
                    else if(message.get("text").toString().startsWith(".dtz"))
                    {
                        TimeZone dtz = new TimeZone();
                        String word = new String(message.get("text").toString().replaceFirst(".dtz", ""));
                        message.put("text", dtz.getDateTime(word));
                        messaging.put("message", message);
                    }
                    else
                    {
                        messaging.put("message", message);
                    }
                }
                else if(message.get("attachments") != null && message.get("sticker_id") == null)
                {
					message.put("text", "I got an attachment!");
                    message.remove("attachments");						
                    messaging.put("message", message);				
                }
				else if(message.get("sticker_id") != null)
                {
					message.put("text", "Thanks for sending me a sticker");
                    message.remove("sticker_id");						
					message.remove("attachments");		
                    messaging.put("message", message);				
                }
                else
                {
					message.put("text", "I got an attachment!");
                    messaging.put("message", message);
                }

                System.out.println(JsonObject.mapFrom(messaging));

                WebClientOptions options = new WebClientOptions();
                options.setSsl(true).setLogActivity(true);
                WebClient client = WebClient.create(vertx, options);


                client
                        .post(443, "graph.facebook.com", "/v2.6/me/messages/")
                        .addQueryParam("access_token", ACCESS_TOKEN)
                        .sendJsonObject(JsonObject.mapFrom(messaging), ar -> {
                            if (ar.succeeded()) {
                                // Obtain response
                                HttpResponse<Buffer> res = ar.result();

                                System.out.println("Received response with status code" + res.bodyAsString());
                            } else {
                                System.out.println("Something went wrong " + ar.cause().getMessage());
                            }
                        });
            });
        });
    }

    private void updateProperties() {
        
            VERIFY_TOKEN = System.getProperty("facebook.verify.token", "verty-token-default");
            ACCESS_TOKEN = System.getProperty("facebook.access.token", "access-token-default");
        
    }
}

