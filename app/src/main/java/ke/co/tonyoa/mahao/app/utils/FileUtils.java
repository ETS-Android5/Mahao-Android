package ke.co.tonyoa.mahao.app.utils;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static DocumentFile fromUri(Context context, Uri filepath) {
        DocumentFile documentFile;
        if (filepath.toString().startsWith("file")) {
            documentFile = DocumentFile.fromFile(new File(filepath.getPath()));
        } else {
            documentFile = DocumentFile.fromSingleUri(context, filepath);
        }
        return documentFile;
    }

    public static String getType(Context context, Uri uri) {
        DocumentFile file = fromUri(context, uri);
        String mimeType = file.getType();
        if (mimeType == null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mimeType==null){
                String uriString = uri.toString();
                mimeType=MimeTypeMap.getSingleton().getMimeTypeFromExtension(uriString.substring(uriString.lastIndexOf(".")+1));
            }
        }
        return mimeType;
    }

    public static byte[] readFile(Application application, Uri filepath) {
        byte[] filedata = null;
        try {
            InputStream inputStream = application.getContentResolver().openInputStream(filepath);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            filedata = buffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filedata;
    }

    public static Uri getCacheFile(@NonNull Context context, @NonNull String fileName, @NonNull String suffix) throws IOException {
        File file = File.createTempFile(fileName, suffix, context.getCacheDir());
        file.createNewFile();
        file.deleteOnExit();
        return FileProvider.getUriForFile(context, "ke.co.tonyoa.mahao.fileprovider", file);
    }
}
