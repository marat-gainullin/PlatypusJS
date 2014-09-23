/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.*;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.HelloRequest;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import sun.misc.BASE64Encoder;

/**
 *
 * @author mg
 */
public class HttpRequestSender implements PlatypusRequestVisitor {

    public interface Authenticator {

        public void authenticate(Callable<Void> onAuthenticate, Callable<Void> onAuthenticated) throws Exception;
    }

    public static final String RESPONSE_MISSING_MSG = "%s must have a response.";
    //
    protected URL url;
    protected Map<String, Cookie> cookies;
    protected String username;
    protected String password;
    protected Authenticator authenticator;
    protected int maximumAuthenticateAttempts = 1;
    //
    protected String method = PlatypusHttpConstants.HTTP_METHOD_GET;
    protected String uriPrefix;
    protected Map<String, Object> params = new HashMap<>();
    protected HttpURLConnection conn;
    protected Response response;
    protected int authenticateRequestsDeepness;

    public HttpRequestSender(URL aUrl, Map<String, Cookie> aCookies, String aUserName, String aPassword, Authenticator aAuthenticator, int aMaximumAuthenticateAttempts) {
        super();
        url = aUrl;
        cookies = aCookies;
        username = aUserName;
        password = aPassword;
        authenticator = aAuthenticator;
        maximumAuthenticateAttempts = aMaximumAuthenticateAttempts;
    }

    public Response getResponse() {
        return response;
    }

    private void executeRequest(Request request) throws Exception {
        executeRequest(request, null);
    }

    private void executeRequest(Request request, Consumer<HttpURLConnection> onHeaders) throws Exception {
        //params.put(PlatypusHttpRequestParams.TYPE, request.getType());
        URL rqUrl = new URL(assembleUrl());
        conn = (HttpURLConnection) rqUrl.openConnection();
        conn.setDefaultUseCaches(false);
        conn.setInstanceFollowRedirects(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        if (onHeaders != null) {
            onHeaders.accept(conn);
        }
        conn.setRequestProperty(PlatypusHttpConstants.HEADER_USER_AGENT, PlatypusHttpConstants.AGENT_NAME);
        addCookies();
        String authScheme = completeRequest(request);
        final String redirectLocation = conn.getHeaderField(PlatypusHttpConstants.HEADER_LOCATION);
        conn.disconnect();
        if (authScheme != null) {
            if (authenticateRequestsDeepness < maximumAuthenticateAttempts) {
                authenticateRequestsDeepness++;
                try {
                    if (authScheme.toLowerCase().contains(PlatypusHttpConstants.BASIC_AUTH_NAME.toLowerCase())) {
                        authenticator.authenticate(() -> {
                            executeRequest(request, (HttpURLConnection aConnection) -> {
                                try {
                                    addBasicAuthentication(aConnection, username, password);
                                } catch (UnsupportedEncodingException ex) {
                                    Logger.getLogger(HttpRequestSender.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                            return null;
                        }, () -> {
                            executeRequest(request, null);
                            return null;
                        });
                        //} else if (authScheme.toLowerCase().contains(PlatypusHttpConstants.DIGEST_AUTH_NAME.toLowerCase())) {
                    } else if (PlatypusHttpConstants.FORM_AUTH_NAME.equals(authScheme)) {
                        authenticator.authenticate(() -> {
                            URL securityFormUrl = new URL(redirectLocation + "?" + PlatypusHttpConstants.SECURITY_CHECK_USER + "=" + URLEncoder.encode(username, SettingsConstants.COMMON_ENCODING) + "&" + PlatypusHttpConstants.SECURITY_CHECK_PASSWORD + "=" + URLEncoder.encode(password, SettingsConstants.COMMON_ENCODING));
                            HttpURLConnection securityFormConn = (HttpURLConnection) securityFormUrl.openConnection();
                            securityFormConn.setRequestMethod(PlatypusHttpConstants.HTTP_METHOD_POST);
                            securityFormConn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTTYPE, PlatypusHttpConstants.FORM_CONTENT_TYPE);
                            addCookies(securityFormConn);
                            securityFormConn.getResponseCode();
                            acceptCookies(securityFormConn);
                            return null;
                        }, null);
                        executeRequest(request, null);
                    } else {
                        throw new UnsupportedOperationException("Unsupported authorization scheme: " + authScheme);
                    }
                } finally {
                    authenticateRequestsDeepness--;
                }
            } else {
                throw new LoginException("Maximum authenticate attempts exceeded: " + maximumAuthenticateAttempts);
            }
        }
    }

    private static void addBasicAuthentication(HttpURLConnection aConnection, String aLogin, String aPassword) throws UnsupportedEncodingException {
        if (aLogin != null && !aLogin.isEmpty() && aPassword != null) {
            BASE64Encoder encoder = new BASE64Encoder();
            String requestAuthSting = PlatypusHttpConstants.BASIC_AUTH_NAME + " " + encoder.encode(aLogin.concat(":").concat(aPassword).getBytes(PlatypusHttpConstants.HEADERS_ENCODING_NAME));
            aConnection.setRequestProperty(PlatypusHttpConstants.HEADER_AUTHORIZATION, requestAuthSting);
        }
    }

    private String assembleUrl() throws UnsupportedEncodingException {
        StringBuilder buf = new StringBuilder();
        buf.append(url);
        if (uriPrefix != null) {
            buf.append(uriPrefix);
        }
        if (params.size() > 0) {
            buf.append("?");
            int paramsCount = 0;
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    buf.append(entry.getKey()).append("=").append(URLEncoder.encode(String.valueOf(entry.getValue()), SettingsConstants.COMMON_ENCODING));
                    if (paramsCount < params.size() - 1) {
                        buf.append("&");
                    }
                }
                paramsCount++;
            }

        }
        return buf.toString();
    }

    private void acceptCookies(HttpURLConnection aConnection) throws ParseException, NumberFormatException {
        Map<String, List<String>> headers = aConnection.getHeaderFields();
        List<String> cookieHeaders = headers.get(PlatypusHttpConstants.HEADER_SETCOOKIE);
        if (cookieHeaders != null) {
            for (String setCookieHeaderValue : cookieHeaders) {
                try {
                    Cookie cookie = Cookie.parse(setCookieHeaderValue);
                    cookies.put(cookie.getName(), cookie);
                } catch (ParseException | NumberFormatException ex) {
                    Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, ex.getMessage());
                    continue;
                }
            }
        }
    }

    public void acceptCookies() throws ParseException, NumberFormatException {
        acceptCookies(conn);
    }

    private void addCookies(HttpURLConnection aConnection) {
        String[] cookiesNames = cookies.keySet().toArray(new String[]{});
        for (String cookieName : cookiesNames) {
            Cookie cookie = cookies.get(cookieName);
            if (cookie != null && cookie.isActual()) {
                String cookieHeaderValue = cookieName + "=" + cookie.getValue();
                aConnection.addRequestProperty(PlatypusHttpConstants.HEADER_COOKIE, cookieHeaderValue);
            } else {
                cookies.remove(cookieName);
            }
        }
    }

    private void addCookies() {
        addCookies(conn);
    }

    private String completeRequest(Request aRequest) throws Exception {
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return conn.getHeaderField(PlatypusHttpConstants.HEADER_WWW_AUTH);
        } else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                && conn.getHeaderField(PlatypusHttpConstants.HEADER_LOCATION) != null
                && conn.getHeaderField(PlatypusHttpConstants.HEADER_LOCATION).toLowerCase().contains(PlatypusHttpConstants.SECURITY_REDIRECT_LOCATION.toLowerCase())) {
            return "form";
        } else {
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                PlatypusResponsesFactory responseFactory = new PlatypusResponsesFactory();
                aRequest.accept(responseFactory);
                response = responseFactory.getResponse();
                PlatypusHttpResponseReader reader = new PlatypusHttpResponseReader(conn);
                response.accept(reader);
                acceptCookies();
            } else {
                Logger.getLogger(HttpRequestSender.class.getName()).log(Level.SEVERE, String.format("Server error %d. %s", conn.getResponseCode(), conn.getResponseMessage()));
                response = new ErrorResponse(conn.getResponseCode() + " " + conn.getResponseMessage());
            }
        }
        return null;
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.put(PlatypusHttpRequestParams.QUERY_ID, rq.getQueryName());
        executeRequest(rq);
    }

    @Override
    public void visit(ModuleStructureRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.put(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleOrResourceName());
        executeRequest(rq);
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        String encodedResourcePath = (new URI(null, null, rq.getResourceName(), null)).toASCIIString();
        uriPrefix = "/" + encodedResourcePath;
        if (rq.getTimeStamp() != null) {
            executeRequest(rq, (HttpURLConnection aConn) -> {
                SimpleDateFormat sdf = new SimpleDateFormat(PlatypusHttpConstants.HTTP_DATE_FORMAT, Locale.US);
                aConn.addRequestProperty(PlatypusHttpConstants.HEADER_IF_MODIFIED_SINCE, sdf.format(rq.getTimeStamp()));
            });
        } else {
            executeRequest(rq);
        }
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_POST;
        executeRequest(rq);
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.put(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName());
        executeRequest(rq);
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.put(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName());
        executeRequest(rq);
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_POST;
        params.put(PlatypusHttpRequestParams.QUERY_ID, rq.getQueryId());
        executeRequest(rq);
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_POST;
        params.put(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName());
        params.put(PlatypusHttpRequestParams.METHOD_NAME, rq.getMethodName());
        executeRequest(rq);
    }

    @Override
    public void visit(KeepAliveRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        executeRequest(rq);
    }

    @Override
    public void visit(HelloRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        executeRequest(rq);
    }

    @Override
    public void visit(LoginRequest rq) throws Exception {
//        conn.authenticate(rq.getLogin(), rq.getPassword());
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        executeRequest(rq);
    }

    @Override
    public void visit(CredentialRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        executeRequest(rq);
    }

    @Override
    public void visit(IsUserInRoleRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.put(PlatypusHttpRequestParams.ROLE_NAME, rq.getRoleName());
        executeRequest(rq);
    }
}
