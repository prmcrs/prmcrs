package com.rim.samples.device.helloworlddemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * HelloWorldDemo.java
 * Adapted from the BlackBerry JDE 4.6.1 HelloWorld sample.
 * Copyright (c) 1998-2008 Research In Motion Ltd.
 * Adapted application code (c) 2026 Pablo Goitisolo.
 * Network diagnostic features adapted from the previous
 * BlackBerry 7290 HelloWorld tester.
 */
class HelloWorldDemo extends UiApplication
{
    /**
     * Entry point for application.
     */
    public static void main(String[] args)
    {
        HelloWorldDemo theApp = new HelloWorldDemo();
        theApp.enterEventDispatcher();
    }

    /**
     * Default constructor.
     */
    private HelloWorldDemo()
    {
        pushScreen(new HelloWorldScreen());
    }
}

/**
 * Main application screen.
 * Keeps the JDE 4.6.1 sample structure while adding
 * the network diagnostic functionality from the old 7290 app.
 */
final class HelloWorldScreen extends MainScreen
{
    private RichTextField _connectionStatus;
    private String _statusText = "";
    private int _apnMode = APN_DEFAULT;

    private static final int APN_DEFAULT = 0;
    private static final int APN_OLD = 1;
    private static final int APN_NEW = 2;

    private static final String WML_URL =
        "http://wap.localize-friends.de/wap/en/chat/rooms.wml";
    private static final String BS0DD_URL = "http://wap.bs0dd.net/";

    HelloWorldScreen()
    {
        super(DEFAULT_MENU | DEFAULT_CLOSE);

        LabelField title =
            new LabelField(
                "[>] NEW PRIMO CLI TESTER",
                LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH
            );
        setTitle(title);

        add(
            new RichTextField(
                "\n" +
                "BlackBerry 8900 / JDE 4.6.1\n" +
                "Una aplicacion hecha por Pablo.\n" +
                "Dedicada a Emma, mi bebe hermosa <3\n" +
                "Desde 2026 para el mundo!!\n"
            )
        );

        add(
            new RichTextField(
                "\n===========================\n" +
                " NETWORK DIAGNOSTIC TOOL\n" +
                "===========================\n" +
                "Pruebas independientes de red.\n" +
                "DNS, TCP por IP, HTTP y WAP/WML.\n" +
                "EDGE no garantiza datos activos.\n\n"
            )
        );

        add(new LabelField("APN a usar:"));

        RadioButtonGroup apnGroup = new RadioButtonGroup();

        RadioButtonField apnDefault =
            new RadioButtonField("Default (sin APN)", apnGroup, true);
        RadioButtonField apnOld =
            new RadioButtonField("Personal Viejo", apnGroup, false);
        RadioButtonField apnNew =
            new RadioButtonField("Personal Nuevo", apnGroup, false);

        apnDefault.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    _apnMode = APN_DEFAULT;
                }
            }
        );
        apnOld.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    _apnMode = APN_OLD;
                }
            }
        );
        apnNew.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    _apnMode = APN_NEW;
                }
            }
        );

        add(apnDefault);
        add(apnOld);
        add(apnNew);

        // TEST 1
        ButtonField tcpButton =
            new ButtonField("1 - TEST TCP SOCKET / DNS", ButtonField.CONSUME_CLICK);
        tcpButton.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    startTcpSocketTest();
                }
            }
        );
        add(tcpButton);

        // TEST 2
        ButtonField rawHttpButton =
            new ButtonField("2 - TEST RAW HTTP", ButtonField.CONSUME_CLICK);
        rawHttpButton.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    startRawHttpTest();
                }
            }
        );
        add(rawHttpButton);

        // TEST 3
        ButtonField httpButton =
            new ButtonField("3 - TEST HTTP CONNECTION", ButtonField.CONSUME_CLICK);
        httpButton.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    startHttpConnectionTest();
                }
            }
        );
        add(httpButton);

        // TEST 4
        ButtonField bingoButton =
            new ButtonField("4 - TEST HTTPBINGO", ButtonField.CONSUME_CLICK);
        bingoButton.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    startHttpBingoTest();
                }
            }
        );
        add(bingoButton);

        // TEST 5
        ButtonField ipButton =
            new ButtonField("5 - TEST TCP BY IP (1.1.1.1:80)", ButtonField.CONSUME_CLICK);
        ipButton.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    startNumericIpTest();
                }
            }
        );
        add(ipButton);

        // TEST 6
        ButtonField dnsPortButton =
            new ButtonField("6 - TEST DNS PORT 53 (10.0.0.1)", ButtonField.CONSUME_CLICK);
        dnsPortButton.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    startDnsPortTest();
                }
            }
        );
        add(dnsPortButton);

        // TEST 7
        ButtonField wapOneButton =
            new ButtonField("7 - TEST WAP SITE (BS0DD)", ButtonField.CONSUME_CLICK);
        wapOneButton.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    startWapTest(BS0DD_URL);
                }
            }
        );
        add(wapOneButton);

        // TEST 8
        ButtonField wapTwoButton =
            new ButtonField("8 - TEST WML PAGE (LOCALIZE-FRIENDS)", ButtonField.CONSUME_CLICK);
        wapTwoButton.setChangeListener(
            new FieldChangeListener()
            {
                public void fieldChanged(Field field, int context)
                {
                    startWapTest(WML_URL);
                }
            }
        );
        add(wapTwoButton);

        add(
            new RichTextField(
                "\n---------------------------\n" +
                " DEBUG OUTPUT\n" +
                "---------------------------\n"
            )
        );

        _statusText = "READY.\n";
        _connectionStatus = new RichTextField(_statusText);
        add(_connectionStatus);
    }

    // R/T/Y/U = tests 1-4, I/O/P = tests 5-7, L = test 8
    protected boolean keyChar(char key, int status, int time)
    {
        switch (key)
        {
            case 'r':
            case 'R':
                startTcpSocketTest();
                return true;

            case 't':
            case 'T':
                startRawHttpTest();
                return true;

            case 'y':
            case 'Y':
                startHttpConnectionTest();
                return true;

            case 'u':
            case 'U':
                startHttpBingoTest();
                return true;

            case 'i':
            case 'I':
                startNumericIpTest();
                return true;

            case 'o':
            case 'O':
                startDnsPortTest();
                return true;

            case 'p':
            case 'P':
                startWapTest(BS0DD_URL);
                return true;

            case 'l':
            case 'L':
                startWapTest(WML_URL);
                return true;
        }

        return super.keyChar(key, status, time);
    }

    private void scrollToBottom()
    {
        try
        {
            int virtualHeight = getVirtualHeight();
            int visibleHeight = getVisibleHeight();

            if (virtualHeight > visibleHeight)
            {
                setVerticalScroll(virtualHeight - visibleHeight);
            }
        }
        catch (Exception e)
        {
            // Screen may not be scrollable yet; ignore.
        }
    }

    private static void closeQuietly(InputStream in)
    {
        if (in != null)
        {
            try { in.close(); } catch (IOException e) { }
        }
    }

    private static void closeQuietly(OutputStream out)
    {
        if (out != null)
        {
            try { out.close(); } catch (IOException e) { }
        }
    }

    private static void closeQuietly(javax.microedition.io.Connection c)
    {
        if (c != null)
        {
            try { c.close(); } catch (IOException e) { }
        }
    }

    private String buildUrl(String base)
    {
        String suffix = "";

        if (_apnMode == APN_OLD)
        {
            suffix = ";apn=gprs.personal.com" +
                     ";tunnelauthusername=gprs" +
                     ";tunnelauthpassword=adgj";
        }
        else if (_apnMode == APN_NEW)
        {
            suffix = ";apn=internet.personal.com" +
                     ";tunnelauthusername=gprs" +
                     ";tunnelauthpassword=adgb";
        }

        return base + ";deviceside=true" + suffix;
    }

    // ---------------------------------------------------------------
    // TEST 1 - TCP SOCKET / DNS
    // ---------------------------------------------------------------

    private void startTcpSocketTest()
    {
        appendStatus("\n[TEST 1] TCP SOCKET / DNS\n");
        appendStatus("Target: example.com:80\n");
        appendStatus("Transport: TCP/IP DIRECTO\n");
        appendStatus("Resolving hostname...\n");

        new Thread(
            new Runnable()
            {
                public void run()
                {
                    testTcpSocket();
                }
            }
        ).start();
    }

    private void testTcpSocket()
    {
        SocketConnection socket = null;

        try
        {
            appendStatus("Connector.open()...\n");

            socket = (SocketConnection) Connector.open(
                buildUrl("socket://example.com:80"));

            appendStatus("TCP SOCKET: OK\n");
            appendStatus("DNS + TCP RESOLUTION: OK\n");
            appendStatus("Internet socket opened.\n");
            appendStatus("RESULTADO: TCP FUNCIONA\n");
        }
        catch (IOException e)
        {
            appendStatus("TCP SOCKET: ERROR\n");
            appendStatus(e.toString() + "\n");
            appendStatus(
                "Si aparece DNSException, el fallo ocurre\n" +
                "antes de establecer el TCP socket.\n");
        }
        catch (Exception e)
        {
            appendStatus("TCP SOCKET: EXCEPTION\n");
            appendStatus(e.toString() + "\n");
        }
        finally
        {
            closeQuietly(socket);
        }
    }

    // ---------------------------------------------------------------
    // TEST 2 - RAW HTTP OVER SOCKET
    // ---------------------------------------------------------------

    private void startRawHttpTest()
    {
        appendStatus("\n[TEST 2] RAW HTTP\n");
        appendStatus("Target: example.com:80\n");
        appendStatus("Opening TCP socket...\n");

        new Thread(
            new Runnable()
            {
                public void run()
                {
                    testRawHttp();
                }
            }
        ).start();
    }

    private void testRawHttp()
    {
        SocketConnection socket = null;
        OutputStream output = null;
        InputStream input = null;

        try
        {
            socket = (SocketConnection) Connector.open(
                buildUrl("socket://example.com:80"));

            appendStatus("TCP: OK\n");

            output = socket.openOutputStream();
            input = socket.openInputStream();

            String request =
                "GET / HTTP/1.0\r\n" +
                "Host: example.com\r\n" +
                "Connection: close\r\n" +
                "\r\n";

            appendStatus("Sending HTTP GET...\n");

            output.write(request.getBytes());
            output.flush();

            appendStatus("HTTP request sent.\n");

            byte[] buffer = new byte[128];
            int count = input.read(buffer);

            if (count > 0)
            {
                String response = new String(buffer, 0, count);

                appendStatus("SERVER RESPONSE:\n");
                appendStatus(response);
                appendStatus("\nRAW HTTP: OK\n");
            }
            else
            {
                appendStatus("SERVER SENT NO DATA\n");
            }
        }
        catch (IOException e)
        {
            appendStatus("RAW HTTP: ERROR\n");
            appendStatus(e.toString() + "\n");
        }
        catch (Exception e)
        {
            appendStatus("RAW HTTP: EXCEPTION\n");
            appendStatus(e.toString() + "\n");
        }
        finally
        {
            closeQuietly(input);
            closeQuietly(output);
            closeQuietly(socket);
        }
    }

    // ---------------------------------------------------------------
    // TEST 3 - HTTPCONNECTION
    // ---------------------------------------------------------------

    private void startHttpConnectionTest()
    {
        appendStatus("\n[TEST 3] HTTP CONNECTION\n");
        appendStatus("Target: example.com\n");
        appendStatus("Opening HTTP connection...\n");

        new Thread(
            new Runnable()
            {
                public void run()
                {
                    testHttpConnection();
                }
            }
        ).start();
    }

    private void testHttpConnection()
    {
        HttpConnection connection = null;

        try
        {
            connection = (HttpConnection) Connector.open(
                buildUrl("http://example.com/"));

            connection.setRequestMethod(HttpConnection.GET);

            appendStatus("HTTP connection opened.\n");

            int responseCode = connection.getResponseCode();

            appendStatus("HTTP CODE: " + responseCode + "\n");

            if (responseCode == HttpConnection.HTTP_OK)
            {
                appendStatus("HTTP CONNECTION: OK\n");
                appendStatus("RESULTADO: HTTP FUNCIONA\n");
            }
            else
            {
                appendStatus("HTTP CONNECTION: ERROR\n");
            }
        }
        catch (IOException e)
        {
            appendStatus("HTTP CONNECTION: IO ERROR\n");
            appendStatus(e.toString() + "\n");
        }
        catch (Exception e)
        {
            appendStatus("HTTP CONNECTION: EXCEPTION\n");
            appendStatus(e.toString() + "\n");
        }
        finally
        {
            closeQuietly(connection);
        }
    }

    // ---------------------------------------------------------------
    // TEST 4 - HTTPBINGO
    // ---------------------------------------------------------------

    private void startHttpBingoTest()
    {
        appendStatus("\n[TEST 4] HTTPBINGO\n");
        appendStatus("Testing external HTTP endpoint...\n");

        new Thread(
            new Runnable()
            {
                public void run()
                {
                    testHttpBingo();
                }
            }
        ).start();
    }

    private void testHttpBingo()
    {
        HttpConnection connection = null;

        try
        {
            connection = (HttpConnection) Connector.open(
                buildUrl("http://httpbingo.org/status/200"));

            connection.setRequestMethod(HttpConnection.GET);

            appendStatus("Connection opened.\n");

            int responseCode = connection.getResponseCode();

            appendStatus("HTTP CODE: " + responseCode + "\n");

            if (responseCode == HttpConnection.HTTP_OK)
            {
                appendStatus("HTTPBINGO: OK\n");
                appendStatus("TCP/IP DIRECTO FUNCIONA\n");
                appendStatus("*** INTERNET TEST PASSED ***\n");
            }
            else
            {
                appendStatus("HTTPBINGO: ERROR\n");
            }
        }
        catch (IOException e)
        {
            appendStatus("HTTPBINGO: IO ERROR\n");
            appendStatus(e.toString() + "\n");
        }
        catch (Exception e)
        {
            appendStatus("HTTPBINGO: EXCEPTION\n");
            appendStatus(e.toString() + "\n");
        }
        finally
        {
            closeQuietly(connection);
        }
    }

    // ---------------------------------------------------------------
    // TEST 5 - TCP BY NUMERIC IP
    // ---------------------------------------------------------------

    private void startNumericIpTest()
    {
        appendStatus("\n[TEST 5] TCP BY NUMERIC IP\n");
        appendStatus("Target: 1.1.1.1:80\n");
        appendStatus("This does not require hostname DNS resolution.\n");

        new Thread(
            new Runnable()
            {
                public void run()
                {
                    SocketConnection socket = null;

                    try
                    {
                        socket = (SocketConnection) Connector.open(
                            buildUrl("socket://1.1.1.1:80"));

                        appendStatus("TCP BY IP: CONNECTED\n");
                        appendStatus(
                            "Basic IP route + outbound TCP appear to work.\n");
                    }
                    catch (Exception e)
                    {
                        appendStatus("TCP BY IP: FAILED\n");
                        appendStatus(e.toString() + "\n");
                        appendStatus(
                            "If hostname test gives DNSException too, compare both results.\n");
                    }
                    finally
                    {
                        closeQuietly(socket);
                    }
                }
            }
        ).start();
    }

    // ---------------------------------------------------------------
    // TEST 6 - TCP PORT 53 PROBE
    // ---------------------------------------------------------------

    private void startDnsPortTest()
    {
        appendStatus("\n[TEST 6] DNS SERVER TCP PORT PROBE\n");
        appendStatus("Target: 10.0.0.1:53\n");
        appendStatus("This checks TCP port 53 only; DNS usually uses UDP.\n");
        appendStatus("A failure here does NOT prove DNS is unavailable.\n");

        new Thread(
            new Runnable()
            {
                public void run()
                {
                    SocketConnection socket = null;

                    try
                    {
                        socket = (SocketConnection) Connector.open(
                            buildUrl("socket://10.0.0.1:53"));

                        appendStatus("10.0.0.1:53 TCP: CONNECTED\n");
                    }
                    catch (Exception e)
                    {
                        appendStatus("10.0.0.1:53 TCP: FAILED/UNSUPPORTED\n");
                        appendStatus(e.toString() + "\n");
                    }
                    finally
                    {
                        closeQuietly(socket);
                    }

                    SocketConnection googleDns = null;

                    try
                    {
                        appendStatus("Checking 8.8.8.8:53 TCP...\n");

                        googleDns = (SocketConnection) Connector.open(
                            buildUrl("socket://8.8.8.8:53"));

                        appendStatus("8.8.8.8:53 TCP: CONNECTED\n");
                    }
                    catch (Exception e)
                    {
                        appendStatus("8.8.8.8:53 TCP: FAILED/UNSUPPORTED\n");
                        appendStatus(e.toString() + "\n");
                    }
                    finally
                    {
                        closeQuietly(googleDns);
                    }
                }
            }
        ).start();
    }

    // ---------------------------------------------------------------
    // TEST 7/8 - HTTP FETCH OF WAP/WML SITES
    // ---------------------------------------------------------------

    private void startWapTest(final String url)
    {
        appendStatus("\n[WAP/WML TEST] " + url + "\n");
        appendStatus("Fetching plain HTTP; this is not a WAP gateway test.\n");

        new Thread(
            new Runnable()
            {
                public void run()
                {
                    HttpConnection connection = null;
                    InputStream input = null;

                    try
                    {
                        connection = (HttpConnection) Connector.open(
                            buildUrl(url));

                        connection.setRequestMethod(HttpConnection.GET);

                        int code = connection.getResponseCode();

                        appendStatus("HTTP STATUS: " + code + "\n");
                        appendStatus(
                            "CONTENT TYPE: " + connection.getType() + "\n");

                        input = connection.openInputStream();

                        byte[] buffer = new byte[160];
                        int count = input.read(buffer);

                        if (count > 0)
                        {
                            appendStatus("BODY PREVIEW (first bytes):\n");
                            appendStatus(new String(buffer, 0, count) + "\n");
                            appendStatus("WAP/WML FETCH: DATA RECEIVED\n");
                        }
                        else
                        {
                            appendStatus(
                                "HTTP responded, but no body bytes were read.\n");
                        }
                    }
                    catch (Exception e)
                    {
                        appendStatus("WAP/WML FETCH: FAILED\n");
                        appendStatus(e.toString() + "\n");
                        appendStatus(
                            "A site can be offline or redirect to HTTPS; compare other tests.\n");
                    }
                    finally
                    {
                        closeQuietly(input);
                        closeQuietly(connection);
                    }
                }
            }
        ).start();
    }

    // ---------------------------------------------------------------
    // STATUS / UI
    // ---------------------------------------------------------------

    private void appendStatus(final String text)
    {
        UiApplication.getUiApplication().invokeLater(
            new Runnable()
            {
                public void run()
                {
                    _statusText = _statusText + text;
                    _connectionStatus.setText(_statusText);

                    UiApplication.getUiApplication().invokeLater(
                        new Runnable()
                        {
                            public void run()
                            {
                                scrollToBottom();
                            }
                        }
                    );
                }
            }
        );
    }

    // ---------------------------------------------------------------
    // CLOSE
    // ---------------------------------------------------------------

    public boolean onClose()
    {
        Dialog.alert("Hasta la vista, baby!");
        System.exit(0);
        return true;
    }
}
