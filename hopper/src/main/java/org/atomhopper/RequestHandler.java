package org.atomhopper;

import com.rackspace.papi.commons.util.pooling.ConstructionStrategy;
import com.rackspace.papi.commons.util.pooling.GenericBlockingResourcePool;
import com.rackspace.papi.commons.util.pooling.Pool;
import com.rackspace.papi.commons.util.pooling.ResourceConstructionException;
import com.rackspace.papi.commons.util.pooling.ResourceContextException;
import com.rackspace.papi.commons.util.pooling.SimpleResourceContext;
import com.rackspace.papi.service.event.EventService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.ParseException;

public class RequestHandler {

    private final EventService eventService;
    private final Pool<Abdera> abderraParserPool;

    public RequestHandler(EventService eventService) {
        this.eventService = eventService;

        abderraParserPool = new GenericBlockingResourcePool<Abdera>(new ConstructionStrategy<Abdera>() {

            @Override
            public Abdera construct() throws ResourceConstructionException {
                return new Abdera();
            }
        }, 0, 50);
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) {
        abderraParserPool.use(new SimpleResourceContext<Abdera>() {

            @Override
            public void perform(Abdera abdera) throws ResourceContextException {
                try {
                    final Document atomDocument = abdera.getParser().parse(request.getInputStream());

                    if (atomDocument != null && atomDocument.getRoot() != null) {
                        final Element atomElement = atomDocument.getRoot();

                        final String localName = atomElement.getQName().getLocalPart();

                        if (localName.equals("entry")) {
                            
                        }
                    }
                } catch (IOException ioe) {
                } catch (ParseException pe) {
                }
            }
        });
    }
}
