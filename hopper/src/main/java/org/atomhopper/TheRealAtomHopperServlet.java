package org.atomhopper;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;

public class TheRealAtomHopperServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Abdera abdera = new Abdera();
        
        final Document atomDocument = abdera.getParser().parse(req.getInputStream());
        
        resp.getWriter().write(atomDocument.getRoot().getQName().getLocalPart());
        resp.flushBuffer();
    }
}
