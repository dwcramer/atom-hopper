package org.atomhopper;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class AtomHopperJettyServerBuilder {

    private final int portNumber;

    public AtomHopperJettyServerBuilder(int portNumber) {
        this.portNumber = portNumber;
    }

    private Server buildNewInstance() {
        final Server jettyServerReference = new Server(portNumber);
        final ServletContextHandler rootContext = buildRootContext(jettyServerReference);

        final ServletHolder atomHopServer = new ServletHolder(TheRealAtomHopperServlet.class);
        rootContext.addServlet(atomHopServer, "/*");

        return jettyServerReference;
    }

    private ServletContextHandler buildRootContext(Server serverReference) {
        final ServletContextHandler servletContext = new ServletContextHandler(serverReference, "/");

        return servletContext;
    }

    public Server newServer() {
        return buildNewInstance();
    }

    public static void main(String[] args) throws Exception {
        new AtomHopperJettyServerBuilder(8080).newServer().start();
    }
}