package me.ericfu.album.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import me.ericfu.album.exception.PhotoException;
import mediautil.image.jpeg.*;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
                .size(768, 768)
                .outputQuality(0.8)
                .toOutputStream(thumbnailStream);
        return new ByteArrayInputStream(thumbnailStream.toByteArray());
    }

    /**
     * Build a thumbnail for given InputStream. Caller is responsible for closing the input stream.
     *
     * @return OutputStream of thumbnail
     */
    @Deprecated
    public static InputStream getRotated(InputStream inputStream) throws IOException {
        ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
        Thumbnails.of(inputStream).scale(1.0).toOutputStream(thumbnailStream);
        return new ByteArrayInputStream(thumbnailStream.toByteArray());
    }

    /**
     * Build a thumbnail for given InputStream. Caller is responsible for closing the input stream.
     *
     * @return OutputStream of thumbnail
     */
    public static InputStream getRotatedLossless(MultipartFile file, String fileName) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            // Read image EXIF data
            LLJTran llj = new LLJTran(inputStream);
            llj.read(LLJTran.READ_INFO, true);
            AbstractImageInfo<?> imageInfo = llj.getImageInfo();
            if (!(imageInfo instanceof Exif)) {
                logger.info("Image '" + fileName + "' has no EXIF data (not a JPEG photo?)");
                return file.getInputStream();
            }

            // Determine the orientation
            Exif exif = (Exif) imageInfo;
            int orientation = 1;
            Entry orientationTag = exif.getTagValue(Exif.ORIENTATION, true);
            if (orientationTag != null) {
                orientation = (Integer) orientationTag.getValue(0);
            }

            // Determine required transform operation
            int operation = 0;
            if (orientation > 0 && orientation < Exif.opToCorrectOrientation.length)
                operation = Exif.opToCorrectOrientation[orientation];
            if (operation == 0) {
                return file.getInputStream(); // The orientation is correct, do nothing
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                // Transform image
                llj.read(LLJTran.READ_ALL, true);
                llj.transform(operation, LLJTran.OPT_DEFAULTS | LLJTran.OPT_XFORM_ORIENTATION);
                llj.save(outputStream, LLJTran.OPT_WRITE_ALL);
            } finally {
                llj.freeMemory();
            }
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (LLJTranException ex) {
            throw new PhotoException("LLJTran: " + ex.getMessage(), ex);
        }
    }
}
