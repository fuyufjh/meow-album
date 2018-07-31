package me.ericfu.album.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.TimeZone;

public class PhotoUtils {

    private static final Logger logger = LoggerFactory.getLogger(PhotoUtils.class);

    public static Date extractDate(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, TimeZone.getDefault());
        } catch (ImageProcessingException e) {
            logger.info("Failed to extract date from uploaded photo: " + e.getMessage());
            return new Date();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHashCode(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get extension (aka. prefix) of uploaded file.
     *
     * @return extension name including dot, or '.jpg' if failed to extract it
     */
    public static String getFileExtName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            logger.warn("Can not get original file name, use .jpg");
            return ".jpg";
        }
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex < 0) {
            logger.warn("Can not extract ext name from original file name: " + originalFileName);
            return ".jpg";
        }
        return originalFileName.substring(dotIndex).toLowerCase();
    }

    /**
     * Build a thumbnail for given InputStream. Caller is responsible for closing the input stream.
     *
     * @return OutputStream of thumbnail
     */
    public static InputStream getThumbnail(InputStream inputStream) throws IOException {
        ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
        Thumbnails.of(inputStream)
                .size(1024, 1024)
                .outputQuality(0.8)
                .toOutputStream(thumbnailStream);
        return new ByteArrayInputStream(thumbnailStream.toByteArray());
    }
}
