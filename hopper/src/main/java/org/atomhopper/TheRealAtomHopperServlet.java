package org.atomhopper;

import com.rackspace.papi.commons.config.manager.UpdateListener;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.papi.service.context.jndi.ServletContextHelper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.atomhopper.config.v1_0.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This totally isn't what it looks like. Nope. Not me. I'd never reimplement something.
public class TheRealAtomHopperServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(TheRealAtomHopperServlet.class);

    @Override
    public void init() throws ServletException {
        final ConfigurationService configService = ServletContextHelper.getPowerApiContext(
                getServletContext()).configurationService();

        // Who loves runtime configuration updates? I do.
        configService.subscribeTo("ah-server.cfg.xml", new UpdateListener<Configuration>() {

            @Override
            public void configurationUpdated(Configuration configurationObject) {
                LOG.info("Booya: " + configurationObject);
            }
        }, Configuration.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Abdera abdera = new Abdera();

        final Document atomDocument = abdera.getParser().parse(req.getInputStream());

        resp.getWriter().write(atomDocument.getRoot().getQName().getLocalPart());
        resp.flushBuffer();
    }
}
