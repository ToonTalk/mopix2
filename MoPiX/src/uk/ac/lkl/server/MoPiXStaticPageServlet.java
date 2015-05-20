package uk.ac.lkl.server;

import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.lkl.shared.CommonUtils;

public class MoPiXStaticPageServlet extends StaticPageServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
	String pathInfo = request.getPathInfo();
//	String guid = CommonUtils.getFileName(pathInfo);
	String extension = CommonUtils.getFileExtension(pathInfo);
	String name = pathInfo.substring(1);
	PrintWriter out = null;
	if (!respondUsingResourceJar(response, pathInfo, name, out)) {
	    return;
	}
	setContentType(response, extension);
    }
    
    @Override
    protected String getResourceArchiveFileName() {
	return "mopix_static_resources_v2.zip";
    }
}
