/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package counsilserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import org.apache.hc.core5.http.ExceptionLogger;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.MethodNotSupportedException;
import org.apache.hc.core5.http.bootstrap.nio.HttpServer;
import org.apache.hc.core5.http.bootstrap.nio.ServerBootstrap;
import org.apache.hc.core5.http.entity.ContentType;
import org.apache.hc.core5.http.impl.nio.BasicAsyncRequestConsumer;
import org.apache.hc.core5.http.impl.nio.BasicAsyncResponseProducer;
import org.apache.hc.core5.http.nio.HttpAsyncExchange;
import org.apache.hc.core5.http.nio.HttpAsyncRequestConsumer;
import org.apache.hc.core5.http.nio.HttpAsyncRequestHandler;
import org.apache.hc.core5.http.nio.entity.NStringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author xminarik
 */
public class webServer implements Runnable{
    JSONObject inputJson;
    int webPort;
    webServer(JSONObject input) throws FileNotFoundException, JSONException, IOException{
        System.out.println("starting web server");
        if (input == null) {    //shouldn't ocure
            System.err.println("missing CounsilWebServer.json");
            return;
        }
        
        if(!input.has("web server port")){
            System.err.println("missing \"web server port\"");
            System.exit(1);
        }else{
            webPort = input.getInt("web server port");
        }
        
        inputJson = input;

    }

    @Override
    public void run() {
        SSLContext sslcontext = null;
        System.out.println("running web server");
        try {
            IOReactorConfig config = IOReactorConfig.custom()
                    .setSoTimeout(15000)
                    .setTcpNoDelay(true)
                    .build();
            final HttpServer server = ServerBootstrap.bootstrap()
                    .setListenerPort(webPort)
                    .setServerInfo("CoUnSil/Configuration/web/Server")
                    .setIOReactorConfig(config)
                    .setSslContext(sslcontext)
                    .setExceptionLogger(ExceptionLogger.STD_ERR)
                    .registerHandler("*", new HttpFileHandler(inputJson))
                    .create();
            server.start();
            try{
                server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            }catch (InterruptedException ex) {
                System.out.println("stopping web server");
                server.shutdown(5, TimeUnit.SECONDS);
            }
        } catch (IOException ex) {
            Logger.getLogger(webServer.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    static class HttpFileHandler implements HttpAsyncRequestHandler<HttpRequest> {

        private final JSONObject configuration;
        private JSONArray rooms;
        private int connectNumber;

        public HttpFileHandler(final JSONObject configuration) {
            super();
            this.configuration = configuration;
            try {
                this.rooms = configuration.getJSONArray("rooms");
            } catch (JSONException ex) {
                this.rooms = new JSONArray();
                System.err.println("no room in configuration file");
                Logger.getLogger(webServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            connectNumber = 1;
        }

        public HttpAsyncRequestConsumer<HttpRequest> processRequest(
                final HttpRequest request,
                final HttpContext context) {
            // Buffer request content in memory for simplicity
            return new BasicAsyncRequestConsumer();
        }

        public void handle(
                final HttpRequest request,
                final HttpAsyncExchange httpexchange,
                final HttpContext context) throws HttpException, IOException {
            HttpResponse response = httpexchange.getResponse();
            handleInternal(request, response, context);
            httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
        }

        private void handleInternal(
                final HttpRequest request,
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {

            //check if supported method
            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported");
            }

            String target = request.getRequestLine().getUri();
            
            String strURL = URLDecoder.decode(target, "UTF-8");
            String[] parsedURL = strURL.split("/");
            if(parsedURL.length < 2){       // sort of url verifying
                System.out.println(parsedURL[0]);
                return;
            }
            
            if (rooms.length() < 1) {
                
                //no room in configuration file
                
                response.setStatusCode(HttpStatus.SC_NOT_FOUND);
                NStringEntity entity = new NStringEntity(
                        "{\"error\" : \"none rooms\"}",
                        ContentType.create("text/html", "UTF-8"));
                response.setEntity(entity);

            } else
            {
                boolean corectRequest = false;
                JSONObject outJson = new JSONObject();
                if(parsedURL[1].equals("roomList")){    // enquary on list of rooms
                    corectRequest = true;
                    JSONArray outArray = new JSONArray();
                    for(int i = 0; i < rooms.length(); i++){
                        JSONObject room;
                        try {
                            
                            room = rooms.getJSONObject(i);
                            String name = room.getString("name");
                            JSONObject jo = new JSONObject();
                            jo.put("name", name);
                            outArray.put(jo);
                            
                        } catch (JSONException ex) {
                            Logger.getLogger(CounsilServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        outJson.put("names", outArray);
                    } catch (JSONException ex) {
                        Logger.getLogger(CounsilServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if(parsedURL[1].equals("room")){
                    for(int i = 0; i < rooms.length(); i++){
                        JSONObject room;
                        try {
                            room = rooms.getJSONObject(i);
                            String name = room.getString("name");
                            if(name.equals(parsedURL[2])){
                                corectRequest = true;
                                outJson.put("name", name);
                                outJson.put("port start", room.getInt("port start"));
                                outJson.put("port end", room.getInt("port end"));
                                outJson.put("server ip", configuration.getString("server ip"));
                                outJson.put("comunication port", configuration.getInt("comunication port"));
                                outJson.put("dummy compress", configuration.getString("dummy compress"));
                                outJson.put("connect number", String.valueOf(connectNumber));
                                connectNumber = connectNumber % 100000; //loop after 100k connections, simple precosion for overflow
                                connectNumber++;    //increese for next connection
                            }
                        } catch (JSONException ex) {
                            Logger.getLogger(CounsilServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                if (!corectRequest) {
                
                    //enquiry incoretct room

                    response.setStatusCode(HttpStatus.SC_FORBIDDEN);
                    NStringEntity entity = new NStringEntity(
                            "{\"error\" : \"incorect room\"}",
                            ContentType.create("text/html", "UTF-8"));
                    response.setEntity(entity);
                    System.out.println("enquiry incoretct room");

                } else {

                    //enquiry corect room

                    HttpCoreContext coreContext = HttpCoreContext.adapt(context);
                    HttpConnection conn = coreContext.getConnection(HttpConnection.class);
                    response.setStatusCode(HttpStatus.SC_OK);
                    NStringEntity body = new NStringEntity(outJson.toString());
                    response.setEntity(body);
                    System.out.println("enquiry corect room");
                }
            }
        }
    }
}
