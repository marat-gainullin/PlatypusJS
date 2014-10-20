/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.login.Credentials;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.platypus.RequestEnvelope;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.concurrent.DeamonThreadFactory;
import com.eas.script.ScriptUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusHttpConnection extends PlatypusConnection {

    static {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((String aHostName, SSLSession aSslSession) -> aHostName.equalsIgnoreCase(aSslSession.getPeerHost()));
            HttpsURLConnection.setDefaultSSLSocketFactory(createSSLContext().getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException | NoSuchProviderException | KeyStoreException | CertificateException | UnrecoverableKeyException | URISyntaxException | IOException ex) {
            Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected Map<String, Cookie> cookies = new ConcurrentHashMap<>();
    protected Credentials basicCredentials;
    //
    private final ThreadPoolExecutor requestsSender;

    public PlatypusHttpConnection(URL aUrl, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts, int aMaximumThreads) throws Exception {
        super(aUrl, aOnCredentials, aMaximumAuthenticateAttempts);
        requestsSender = new ThreadPoolExecutor(aMaximumThreads, aMaximumThreads,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new DeamonThreadFactory("http-client-", false));
        requestsSender.allowCoreThreadTimeOut(true);
    }

    public Credentials getBasicCredentials() {
        return basicCredentials;
    }

    public void setBasicCredentials(Credentials aValue) {
        basicCredentials = aValue;
    }

    @Override
    public <R extends Response> void enqueueRequest(Request rq, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        enqueue(new RequestCallback(new RequestEnvelope(rq, null, null, null), (Response aResponse) -> {
            if (aResponse instanceof ErrorResponse) {
                if (onFailure != null) {
                    Exception cause = handleErrorResponse((ErrorResponse) aResponse);
                    if (ScriptUtils.getGlobalQueue() != null) {
                        ScriptUtils.getGlobalQueue().accept(() -> {
                            onFailure.accept(cause);
                        });
                    } else {
                        final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
                        synchronized (lock) {
                            onFailure.accept(cause);
                        }
                    }
                }
            } else {
                if (onSuccess != null) {
                    if (ScriptUtils.getGlobalQueue() != null) {
                        ScriptUtils.getGlobalQueue().accept(() -> {
                            if (rq instanceof LogoutRequest) {
                                cookies.clear();
                                basicCredentials = null;
                            }
                            onSuccess.accept((R) aResponse);
                        });
                    } else {
                        final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
                        synchronized (lock) {
                            if (rq instanceof LogoutRequest) {
                                cookies.clear();
                                basicCredentials = null;
                            }
                            onSuccess.accept((R) aResponse);
                        }
                    }
                }
            }
        }), onFailure);
    }

    private void startRequestTask(Runnable aTask) {
        Object closureLock = ScriptUtils.getLock();
        Object closureRequest = ScriptUtils.getRequest();
        Object closureResponse = ScriptUtils.getResponse();
        Object closureSession = ScriptUtils.getSession();
        PlatypusPrincipal closurePrincipal = PlatypusPrincipal.getInstance();
        requestsSender.submit(() -> {
            ScriptUtils.setLock(closureLock);
            ScriptUtils.setRequest(closureRequest);
            ScriptUtils.setResponse(closureResponse);
            ScriptUtils.setSession(closureSession);
            PlatypusPrincipal.setInstance(closurePrincipal);
            try {
                aTask.run();
            } finally {
                ScriptUtils.setLock(null);
                ScriptUtils.setRequest(null);
                ScriptUtils.setResponse(null);
                ScriptUtils.setSession(null);
                PlatypusPrincipal.setInstance(null);
            }
        });
    }

    private void enqueue(final RequestCallback rqc, Consumer<Exception> onFailure) {
        startRequestTask(() -> {
            try {
                PlatypusHttpRequestWriter httpSender = new PlatypusHttpRequestWriter(url, cookies, onCredentials, sequence, maximumAuthenticateAttempts, PlatypusHttpConnection.this);
                rqc.requestEnv.request.accept(httpSender);// wait completion analog
                if (rqc.onComplete != null) {
                    rqc.requestEnv.request.setDone(true);
                    rqc.completed = true;
                    rqc.onComplete.accept(httpSender.getResponse());
                } else {
                    synchronized (rqc) {
                        rqc.requestEnv.request.setDone(true);
                        rqc.response = httpSender.getResponse();
                        rqc.completed = true;
                        rqc.notifyAll();
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                if (onFailure != null) {
                    onFailure.accept(ex);
                }
            }
        });
    }

    @Override
    public <R extends Response> R executeRequest(Request rq) throws Exception {
        RequestCallback rqc = new RequestCallback(new RequestEnvelope(rq, null, null, null), null);
        enqueue(rqc, null);
        rqc.waitCompletion();
        if (rqc.response instanceof ErrorResponse) {
            throw handleErrorResponse((ErrorResponse) rqc.response);
        } else {
            if (rq instanceof LogoutRequest) {
                cookies.clear();
                basicCredentials = null;
            }
            return (R) rqc.response;
        }
    }

    @Override
    public void shutdown() {
        requestsSender.shutdown();
    }
}
