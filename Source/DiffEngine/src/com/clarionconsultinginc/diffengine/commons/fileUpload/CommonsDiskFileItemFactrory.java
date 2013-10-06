package com.clarionconsultinginc.diffengine.commons.fileUpload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileCleaningTracker;

public class CommonsDiskFileItemFactrory extends DiskFileItemFactory{

    public FileItem createItem(String fieldName, String contentType,
            boolean isFormField, String fileName) {
        DiskFileItem result = new CommonsDiskFileItem(fieldName, contentType,
                isFormField, fileName, getSizeThreshold(), getRepository());
        FileCleaningTracker tracker = getFileCleaningTracker();
        if (tracker != null) {
            //tracker.track(result.getTempFile(), result);
        }
        return result;
    }
}
