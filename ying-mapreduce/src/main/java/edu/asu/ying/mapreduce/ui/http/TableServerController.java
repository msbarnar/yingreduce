package edu.asu.ying.mapreduce.ui.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;

import edu.asu.ying.mapreduce.TableServerDaemon;
import edu.asu.ying.mapreduce.io.table.SimpleServerTableProxy;
import edu.asu.ying.mapreduce.io.table.SimpleServerTableProxyProvider;
import edu.asu.ying.mapreduce.rpc.net.NodeNotFoundException;
import edu.asu.ying.mapreduce.table.ElementSource;
import edu.asu.ying.mapreduce.table.TableID;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * The {@link TableServerController} is a servlet that provides the interface with 
 * control over the {@link TableServerDaemon}, such as connecting to networks or
 * uploading files.
 */
public final class TableServerController 
	extends Controller {

	private final class ErrorResponseException extends RuntimeException {
		private final String response;
		
		public ErrorResponseException(final String response) {
			this.response = response;
		}
		
		public final String getResponse() { return this.response; }
	}
	
	private static final long serialVersionUID = -42609805888037945L;
	
	private final TableServerDaemon daemon;
	
	public TableServerController(final TableServerDaemon daemon) {
		this.daemon = daemon;
	}
	
	private void errorResponse(final Throwable cause) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<exception>");
			sb.append(cause.getClass().getSimpleName());
		sb.append("</exception>");
		sb.append("<detail>");
			sb.append(cause.getMessage());
		sb.append("</detail>");
		if (cause.getCause() != null) {
			sb.append("<cause>");
				sb.append(cause.getCause().toString());
			sb.append("</cause>");
		}
		
		cause.printStackTrace();
		
		throw new ErrorResponseException(sb.toString());
	}
	
	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		
		
		response.setHeader("Cache-Control","no-store, max-age=0, no-cache, must-revalidate");     
        response.addHeader("Cache-Control", "post-check=0, pre-check=0"); 
        response.setHeader("Pragma", "no-cache");  
        response.setDateHeader("Expires", 0);
        
		final String method = request.getParameter("method");
		try {
			// Get the action to take from the parameters
			if (method == null || method.equals("")) {
				errorResponse(new IllegalArgumentException("Method name cannot be empty."));
			}
			
			if (!method.equals("get")) {
				// FIXME: hackity hack hack because getTable wants to open the output stream
				response.setContentType("text/xml");
				response.getWriter().write("<response>");
				response.getWriter().write("<method>".concat(method).concat("</method>"));
			}
			
			final Method webMethod = this.actions.get(method);
			if (webMethod == null) {
				errorResponse(new UnsupportedOperationException());
			} else {
				try {
					webMethod.invoke(this, request, response);
				} catch (final Throwable e) {
					errorResponse(e);
				}
			}
		} catch (final ErrorResponseException e) {
			response.getWriter().write(e.getResponse());
		}
		if (!method.equals("get")) {
			response.getWriter().write("</response>");
			response.flushBuffer();
		}
	}
	
	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		response.setContentType("text/xml");
		response.getWriter().write("<response>");
		response.setHeader("Cache-Control","no-store, max-age=0, no-cache, must-revalidate");     
        response.addHeader("Cache-Control", "post-check=0, pre-check=0"); 
        response.setHeader("Pragma", "no-cache");  
        response.setDateHeader("Expires", 0);
        
		try {
			// For file uploads
			final boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (!isMultipart) {
				errorResponse(new IllegalArgumentException("POST is only for multipart content."));
			}
			
			// Get the file onto disk
			final ServletContext context = this.getServletConfig().getServletContext();
			FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(context);
			final DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setFileCleaningTracker(fileCleaningTracker);
			final File repository = (File) context.getAttribute("javax.servlet.context.tempdir");
			factory.setRepository(repository);
			final ServletFileUpload upload = new ServletFileUpload(factory);
			
			List<FileItem> items = null;
			try {
				items = upload.parseRequest(request);
			} catch (final FileUploadException e) {
				errorResponse(e);
			}
			
			final Iterator<FileItem> iter = items.iterator();
			String szTableId = null;
			while (iter.hasNext()) {
				final FileItem item = iter.next();
				if (item.isFormField()) {
					// Get file attributes
					if (item.getFieldName().trim().toLowerCase().equals("tableid")) {
						szTableId = item.getString().trim().toLowerCase();
					}
				} else {
					// Process file upload
					if (szTableId == null || szTableId.equals("")) {
						errorResponse(new IllegalStateException("Tried to process table data without a valid Table ID."));
					}
					final InputStream stream = item.getInputStream();
					final TableID tableId = new TableID(szTableId);
					
					response.getWriter().write("<tableid>".concat(tableId.toString()).concat("</tableid>"));
					
					final ElementSource reader = this.daemon.getElementSource(tableId, stream);
					for (;;) {
						try {
							reader.readNextElement();
						} catch (final NoSuchElementException e) {
							break;
						} catch (final IOException e) {
							errorResponse(e);
						}
					}
					
					response.getWriter().write("<success>true</success>");
				}
			}
		} catch (final ErrorResponseException e) {
			response.getWriter().write(e.getResponse());
		}
		
		response.getWriter().write("</response>");
		response.flushBuffer();
	}
	
	@WebMethod(name = "connect")
	public void doConnect(final HttpServletRequest request, final HttpServletResponse response) 
			throws NodeNotFoundException, URISyntaxException, IOException {
		
		final URI uri = URI.create(request.getParameter("uri"));
		this.daemon.getSendChannel().join(uri);
		
		final Object remoteUri = this.daemon.getSendChannel().getTransportSink().getProperties().get("bootstrap-uri");
		if (remoteUri != null) {
			response.getWriter().write("<remote-addr>".concat(remoteUri.toString()).concat("</remote-addr>"));
		}
		response.getWriter().write("<success>true</success>");
	}
	
	@WebMethod(name = "nothing")
	public void doNothing(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final int rnd = (new Random()).nextInt();
		final String val = request.getParameter("value");
		if (val != null) {
			response.getWriter().write("<yousaid>".concat(val).concat("</yousaid>"));
		}
		response.getWriter().write("<value>".concat(String.valueOf(rnd)).concat("</value>"));
		response.getWriter().write("<success>true</success>");
	}
	
	@WebMethod(name = "tables")
	public void getTables(final HttpServletRequest requset, final HttpServletResponse response) throws IOException {
		// Send back the list of tables
		final PrintWriter w = response.getWriter();
		w.write("<tables>");
		final SimpleServerTableProxyProvider prov = this.daemon.getProxyProvider();
		final Map<String, SimpleServerTableProxy> tables = prov.getTable();
		for (final Map.Entry<String, SimpleServerTableProxy> entry : tables.entrySet()) {
			w.write("<table>");
				w.write("<id>");
					w.write(entry.getKey());
				w.write("</id>");
				w.write("<pageCount>");
					w.write(String.valueOf(entry.getValue().getPageCount()));
				w.write("</pageCount>");
			w.write("</table>");
		}
		w.write("</tables>");
	}
	
	@WebMethod(name = "get")
	public void getTable(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		// Retrieve a complete table and return its contents
		final String tableId = request.getParameter("tableid");
		final InputStream inStream = this.daemon.getTableStream(new TableID(tableId));
		final ServletOutputStream outStream = response.getOutputStream();
		
		response.setContentType("text/plain");
		response.setHeader("Content-disposition", "attachment; filename=".concat(tableId).concat(".txt"));
		
		final byte[] buffer = new byte[2048];
		int bytesRead = 0;
		
		while (-1 != (bytesRead = inStream.read(buffer, 0, buffer.length))) {
			outStream.write(buffer, 0, bytesRead);
		}
		
		outStream.flush();
		outStream.close();
	}
}
