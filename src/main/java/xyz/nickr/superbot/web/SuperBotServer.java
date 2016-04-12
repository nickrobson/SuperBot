package xyz.nickr.superbot.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fi.iki.elonen.NanoHTTPD;

public class SuperBotServer extends NanoHTTPD {

    public static void main(String[] args) {
        try {
            new SuperBotServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {}
    }

    private static final Map<String, Endpoint> endpoints = new HashMap<>();

    public SuperBotServer() throws IOException {
        super(8081);
        StandardEndpoints.register();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri().substring(1);
        String[] routes = uri.split("/");
        if (endpoints.containsKey(routes[0])) {
            try {
                Response res = endpoints.get(routes[0]).serve(session, routes);
                if (res != null)
                    return res;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return super.serve(session);
    }

    public static void registerEndpoint(Endpoint endpoint, String... aliases) {
        Objects.requireNonNull(endpoint, "endpoint");
        Objects.requireNonNull(aliases, "aliases");
        for (String alias : aliases) {
            endpoints.put(alias, endpoint);
        }
    }

}
