/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.coyote.http11;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.CharBuffer;
//import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

//import javax.servlet.AsyncContext;
//import javax.servlet.DispatcherType;
//import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.SimpleHttpClient;
import org.apache.catalina.startup.TesterServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.TomcatBaseTest;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.compat.JreCompat;
//import org.apache.tomcat.util.descriptor.web.SecurityCollection;
//import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
//import org.apache.tomcat.util.http.parser.TokenList;

public class TestHttp11Processor extends TomcatBaseTest {


//    @Test
//    public void testWithTESavedRequest() throws Exception {
//        Tomcat tomcat = getTomcatInstance();
//
//        // Use the normal Tomcat ROOT context
//        File root = new File("test/webapp");
//        tomcat.addWebapp("", root.getAbsolutePath());
//
//        tomcat.start();
//
//        String request =
//                "POST /echo-params.jsp HTTP/1.1" + SimpleHttpClient.CRLF +
//                        "Host: any" + SimpleHttpClient.CRLF +
//                        "Transfer-encoding: savedrequest" + SimpleHttpClient.CRLF +
//                        "Content-Length: 9" + SimpleHttpClient.CRLF +
//                        "Content-Type: application/x-www-form-urlencoded" +
//                        SimpleHttpClient.CRLF +
//                        SimpleHttpClient.CRLF +
//                        "test=data";
//
//        Client client = new Client(tomcat.getConnector().getLocalPort());
//        client.setRequest(new String[] {request});
//
//        client.connect();
//        client.processRequest();
//        Assert.assertTrue(client.isResponse501());
//    }

    //FAILS
    @Test
    public void testTEHeaderUnknown01() throws Exception {
        doTestTEHeaderInvalid("identity", false);
    }

//    //FAILS
//    @Test
//    public void testTEHeaderUnknown02() throws Exception {
//        doTestTEHeaderInvalid("identity, chunked", false);
//    }
//
//
//    @Test
//    public void testTEHeaderUnknown03() throws Exception {
//        doTestTEHeaderInvalid("unknown, chunked", false);
//    }
//
//
//    @Test
//    public void testTEHeaderUnknown04() throws Exception {
//        doTestTEHeaderInvalid("void", false);
//    }
//
//
//    @Test
//    public void testTEHeaderUnknown05() throws Exception {
//        doTestTEHeaderInvalid("void, chunked", false);
//    }
//
//
//    @Test
//    public void testTEHeaderUnknown06() throws Exception {
//        doTestTEHeaderInvalid("void, identity", false);
//    }
//
//
//    @Test
//    public void testTEHeaderUnknown07() throws Exception {
//        doTestTEHeaderInvalid("identity, void", false);
//    }
//
//
//    //FAILS
//    @Test
//    public void testTEHeaderChunkedNotLast01() throws Exception {
//        doTestTEHeaderInvalid("chunked, void", true);
//    }

    private void doTestTEHeaderInvalid(String headerValue, boolean badRequest) throws Exception {
        Tomcat tomcat = getTomcatInstance();

        // No file system docBase required
        Context ctx = tomcat.addContext("", null);

        // Add servlet
        Tomcat.addServlet(ctx, "TesterServlet", new TesterServlet());
        ctx.addServletMapping("/foo", "TesterServlet");

        tomcat.start();

        String request =
                "GET /foo HTTP/1.1" + SimpleHttpClient.CRLF +
                        "Host: localhost:" + getPort() + SimpleHttpClient.CRLF +
                        "Transfer-Encoding: " + headerValue + SimpleHttpClient.CRLF +
                        SimpleHttpClient.CRLF;

        Client client = new Client(tomcat.getConnector().getLocalPort());
        client.setRequest(new String[] {request});

        client.connect();
        client.processRequest(false);

        if (badRequest) {
            Assert.assertTrue(client.isResponse400());
        } else {
            Assert.assertTrue(client.isResponse501());
        }
    }


//    //FAILS
//    @Test
//    public void testWithTEChunkedHttp10() throws Exception {
//
//        getTomcatInstanceTestWebapp(true);
//
//        String request =
//                "POST /test/echo-params.jsp HTTP/1.0" + SimpleHttpClient.CRLF +
//                        "Host: any" + SimpleHttpClient.CRLF +
//                        "Transfer-encoding: chunked" + SimpleHttpClient.CRLF +
//                        "Content-Type: application/x-www-form-urlencoded" +
//                        SimpleHttpClient.CRLF +
//                        "Connection: close" + SimpleHttpClient.CRLF +
//                        SimpleHttpClient.CRLF +
//                        "9" + SimpleHttpClient.CRLF +
//                        "test=data" + SimpleHttpClient.CRLF +
//                        "0" + SimpleHttpClient.CRLF +
//                        SimpleHttpClient.CRLF;
//
//        Client client = new Client(getPort());
//        client.setRequest(new String[] {request});
//
//        client.connect();
//        client.processRequest();
//        Assert.assertTrue(client.isResponse200());
//        Assert.assertTrue(client.getResponseBody().contains("test - data"));
//    }
//
//    @Test
//    public void testWithTEUnsupported() throws Exception {
//        Tomcat tomcat = getTomcatInstance();
//
//        // Use the normal Tomcat ROOT context
//        File root = new File("test/webapp");
//        tomcat.addWebapp("", root.getAbsolutePath());
//
//        tomcat.start();
//
//        String request =
//                "POST /echo-params.jsp HTTP/1.1" + SimpleHttpClient.CRLF +
//                        "Host: any" + SimpleHttpClient.CRLF +
//                        "Transfer-encoding: unsupported" + SimpleHttpClient.CRLF +
//                        "Content-Length: 9" + SimpleHttpClient.CRLF +
//                        "Content-Type: application/x-www-form-urlencoded" +
//                        SimpleHttpClient.CRLF +
//                        SimpleHttpClient.CRLF +
//                        "test=data";
//
//        Client client = new Client(tomcat.getConnector().getLocalPort());
//        client.setRequest(new String[] {request});
//
//        client.connect();
//        client.processRequest();
//        Assert.assertTrue(client.isResponse501());
//    }


    private static final class Client extends SimpleHttpClient {

        public Client(int port) {
            setPort(port);
        }

        @Override
        public boolean isResponseBodyOK() {
            return getResponseBody().contains("test - data");
        }
    }
}