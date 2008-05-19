package com.jonasboner.remoting.proxy;

import com.jonasboner.remoting.proxy.util.ClassLoaderHelper;
import com.jonasboner.remoting.proxy.util.WrappedRuntimeException;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Manages the remote proxy server.
 *
 * @author Jonas Bon&#233;r
 */
public class RemoteProxyServerManager {

    private static final boolean START_REMOTE_PROXY_SERVER = "true".equals(
            java.lang.System.getProperty(
                    "remoteproxyserver",
                    "false"
            )
    );

    /**
     * The sole instance.
     */
    private static final RemoteProxyServerManager INSTANCE = new RemoteProxyServerManager();

    /**
     * Returns the sole instance.
     *
     * @return the sole instance
     */
    public static RemoteProxyServerManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts up the remote proxy server.
     */
    public void start() {
        if (START_REMOTE_PROXY_SERVER) {
            RemoteProxyServer remoteProxyServer = new RemoteProxyServer(
            		ClassLoaderHelper.getLoader(), getInvoker());
            remoteProxyServer.start();
        }
    }

    /**
     * Forces start up of the remote proxy server.
     */
    public void forceStart() {
        final RemoteProxyServer remoteProxyServer = new RemoteProxyServer(
        		ClassLoaderHelper.getLoader(), getInvoker());
        remoteProxyServer.start();
    }

    /**
     * Returns the Invoker instance to use.
     *
     * @return the Invoker
     */
    private Invoker getInvoker() {
        Invoker invoker;
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(java.lang.System.getProperty("remoteproxy.properties")));
            String className = properties.getProperty("remote.server.invoker");
            invoker = (Invoker) ClassLoaderHelper.forName(className).newInstance();
        } catch (Exception e) {
            invoker = getDefaultInvoker();
        }
        return invoker;
    }

    /**
     * Returns the default Invoker.
     *
     * @return the default invoker
     */
    private Invoker getDefaultInvoker() {
        return new Invoker() {
            public Object invoke(final String handle,
                                 final String methodName,
                                 final Class[] paramTypes,
                                 final Object[] args,
                                 final Object context) {
                Object result;
                try {
                    final Object instance = RemoteProxy.getWrappedInstance(handle);
                    final Method method = instance.getClass().getMethod(methodName, paramTypes);
                    result = method.invoke(instance, args);
                } catch (Exception e) {
                    throw new WrappedRuntimeException(e);
                }
                return result;
            }
        };
    }

    /**
     * Private constructor.
     */
    private RemoteProxyServerManager() {

    }
}
