package org.atomhopper;

import com.rackspace.papi.commons.config.manager.UpdateListener;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.papi.service.context.jndi.ServletContextHelper;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.commons.lang.StringUtils;
import org.atomhopper.config.WorkspaceConfigProcessor;
import org.atomhopper.config.v1_0.Configuration;
import org.atomhopper.config.v1_0.WorkspaceConfiguration;
import org.atomhopper.exceptions.ContextAdapterResolutionException;
import org.atomhopper.servlet.ApplicationContextAdapter;
import org.atomhopper.servlet.DefaultEmptyContext;
import org.atomhopper.servlet.ServletInitParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This totally isn't what it looks like. Nope. Not me. I'd never reimplement something.
public class TheRealAtomHopperServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(TheRealAtomHopperServlet.class);
    private ApplicationContextAdapter applicationContextAdapter;

    @Override
    public void init() throws ServletException {
        final ServletContext servletContext = getServletContext();

        applicationContextAdapter = getContextAdapter();
        applicationContextAdapter.usingServletContext(servletContext);

        final ConfigurationService configService = ServletContextHelper.getPowerApiContext(getServletContext()).configurationService();

        // Who loves runtime configuration updates? I do.
        configService.subscribeTo("ah-server.cfg.xml", new UpdateListener<Configuration>() {

            @Override
            public void configurationUpdated(Configuration configurationObject) {
                if (configurationObject != null) {
                    final ServletContext servletContext = getServletContext();
                    final RegexTargetResolver resolver = new RegexTargetResolver();

                    for (WorkspaceConfiguration workspaceConfig : configurationObject.getWorkspace()) {
                        final WorkspaceConfigProcessor processor = new WorkspaceConfigProcessor(workspaceConfig, getContextAdapter(), resolver, servletContext.getContextPath());

                        processor.toHandler();
                    }
                }
            }
        }, Configuration.class);

        super.init();
    }

    protected ApplicationContextAdapter getContextAdapter() throws ContextAdapterResolutionException {
        String adapterClass = getInitParameter(ServletInitParameter.CONTEXT_ADAPTER_CLASS.toString());

        // If no adapter class is set then use the default empty one
        if (StringUtils.isBlank(adapterClass)) {
            adapterClass = DefaultEmptyContext.class.getName();
        }

        try {
            final Object freshAdapter = Class.forName(adapterClass).newInstance();

            if (freshAdapter instanceof ApplicationContextAdapter) {
                return (ApplicationContextAdapter) freshAdapter;
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);

            throw new ContextAdapterResolutionException(ex.getMessage(), ex);
        }

        throw new ContextAdapterResolutionException("Unknown application context adapter class: " + adapterClass);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
