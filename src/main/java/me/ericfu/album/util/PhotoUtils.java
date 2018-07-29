package me.ericfu.album.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

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

}
