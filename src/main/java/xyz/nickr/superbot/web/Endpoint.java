package xyz.nickr.superbot.web;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface Endpoint {

    Response serve(IHTTPSession session, String[] routes) throws IOException;

}
