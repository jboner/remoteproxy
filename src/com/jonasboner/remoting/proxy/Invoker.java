package com.jonasboner.remoting.proxy;

/**
 * Invokes the method for an instance mapped to a specific handle.
 *
 * @author Jonas Bon&#233;r
 */
public interface Invoker {
    /**
     * Invokes a specific method on the object mapped to the role specified.
     *
     * @param handle     the handle to the implementation class (class name, mapped name, UUID etc.)
     * @param methodName the name of the method
     * @param paramTypes the parameter types
     * @param args       the arguments to the method
     * @param context    the context with the users principal and credentials
     * @return the result from the invocation
     */
    public Object invoke(final String handle,
                         final String methodName,
                         final Class[] paramTypes,
                         final Object[] args,
                         final Object context);
}