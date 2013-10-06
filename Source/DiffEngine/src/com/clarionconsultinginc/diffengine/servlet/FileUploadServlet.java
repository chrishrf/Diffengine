package com.clarionconsultinginc.diffengine.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.clarionconsultinginc.diffengine.commons.fileUpload.CommonsDiskFileItemFactrory;
import com.clarionconsultinginc.diffengine.processor.CSVProcessor;

@SuppressWarnings("serial")
public class FileUploadServlet extends HttpServlet {
	private static CSVProcessor processor = new CSVProcessor();
//    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.getParameter("hiddenField1");
		if (ServletFileUpload.isMultipartContent(req)) {
			DiskFileItemFactory factory = new CommonsDiskFileItemFactrory();

			factory.setSizeThreshold(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD * 200);

			// File Upload handler
			ServletFileUpload handler = new ServletFileUpload(factory);

			// Parsing the Requests
			List<FileItem> items;
			try {
				items = handler.parseRequest(req);

				for (FileItem fileItem : items) {
					if (fileItem.isFormField()) {

					} else {
						// System.out.println(fileItem.getString());
						boolean success = processor.processCSV(fileItem.getString(), "Amazon");

						if (!success) {
							resp.getWriter().write("File - " + fileItem.getName() + " , already Uploaded");
						} else {
							resp.sendRedirect("/index.html");
						}

//						System.out.println("\n\n");
//						System.out.println(fileItem.getContentType());
//						System.out.println(fileItem.getName());
//						System.out.println(fileItem.getSize());
					}
				}
			} catch (FileUploadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

//    @Override
//    public void doPost(HttpServletRequest req, HttpServletResponse res)
//        throws ServletException, IOException {
//
//        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
//        List<BlobKey> blobKeys = blobs.get("myFile");
//
//        if (blobKeys == null) {
//            res.sendRedirect("/");
//        } else {
//        	BlobKey blobKey = blobKeys.get(0);
//            res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
//        }
//    }

}
