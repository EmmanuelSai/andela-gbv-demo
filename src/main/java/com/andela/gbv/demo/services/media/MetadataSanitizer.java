package com.andela.gbv.demo.services.media;

import java.io.ByteArrayInputStream;
import java.util.HexFormat;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.stereotype.Service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

import com.drew.metadata.Metadata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Iterator;

@Slf4j
@Service
public class MetadataSanitizer {

    @Value("${media.max-dimension:2048}")
    private int maxDimension;

    @Value("${media.jpeg-quality:0.85}")
    private float jpegQuality;

    @Value("${media.max-input-bytes:10485760}")
    private long maxInputBytes;

    public SanitizedMedia sanitize(byte[] input, String declaredContentType) {
        if (input == null || input.length == 0) {
            throw new MediaSanitizationException("Empty media payload");
        }
        if (input.length > maxInputBytes) {
            throw new MediaSanitizationException(
                    "Media exceeds max size: " + input.length + " > " + maxInputBytes);
        }

        MetadataAudit audit = inspectMetadata(input);

        BufferedImage original;
        try (ByteArrayInputStream in = new ByteArrayInputStream(input)) {
            original = ImageIO.read(in);
        } catch (IOException e) {
            throw new MediaSanitizationException("Failed to decode image", e);
        }
        if (original == null) {
            throw new MediaSanitizationException(
                    "Unsupported image format or corrupt file: " + declaredContentType);
        }

        BufferedImage resized = downscaleIfNeeded(original);
        byte[] sanitized = encodeJpeg(resized);
        String sha256 = sha256Hex(sanitized);

        log.info("Media sanitized: {} -> {} bytes, {}x{}, sha256={}, gps={}, exif={}, xmp={}",
                input.length, sanitized.length, resized.getWidth(), resized.getHeight(),
                sha256, audit.hadGps, audit.hadExif, audit.hadXmp);

        return new SanitizedMedia(sanitized, "image/jpeg", sha256, audit);
    }

    private BufferedImage downscaleIfNeeded(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        int longest = Math.max(w, h);
        if (longest <= maxDimension)
            return copyToFresh(src);

        double scale = (double) maxDimension / longest;
        int newW = (int) Math.round(w * scale);
        int newH = (int) Math.round(h * scale);

        BufferedImage dst = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(src, 0, 0, newW, newH, null);
        } finally {
            g.dispose();
        }
        return dst;
    }

    private BufferedImage copyToFresh(BufferedImage src) {
        BufferedImage dst = new BufferedImage(
                src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        try {
            g.drawImage(src, 0, 0, null);
        } finally {
            g.dispose();
        }
        return dst;
    }

    private byte[] encodeJpeg(BufferedImage img) {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext())
            throw new MediaSanitizationException("No JPEG writer");
        ImageWriter writer = writers.next();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageOutputStream ios = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(jpegQuality);
            // null metadata: this is the key — no EXIF/XMP/ICC is written
            writer.write(null, new IIOImage(img, null, null), param);
            writer.dispose();
            return out.toByteArray();
        } catch (IOException e) {
            throw new MediaSanitizationException("Re-encode failed", e);
        }
    }

    public MetadataAudit inspectMetadata(byte[] input) {
        MetadataAudit audit = new MetadataAudit();
        try (ByteArrayInputStream in = new ByteArrayInputStream(input)) {
            Metadata metadata = ImageMetadataReader.readMetadata(in);
            if (metadata.containsDirectoryOfType(GpsDirectory.class))
                audit.hadGps = true;
            if (metadata.containsDirectoryOfType(ExifIFD0Directory.class)
                    || metadata.containsDirectoryOfType(ExifSubIFDDirectory.class))
                audit.hadExif = true;
            metadata.getDirectories().forEach(dir -> {
                String name = dir.getName();
                if (name != null && name.toLowerCase().contains("xmp"))
                    audit.hadXmp = true;
            });
        } catch (Exception e) {
            audit.inspectionFailed = true;
            log.warn("Metadata inspection failed (non-fatal): {}", e.getMessage());
        }
        return audit;
    }

    private String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(data));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public record SanitizedMedia(byte[] bytes, String contentType, String sha256, MetadataAudit audit) {
    }

    public static class MetadataAudit {
        public boolean hadGps;
        public boolean hadExif;
        public boolean hadXmp;
        public boolean inspectionFailed;
    }

    public static class MediaSanitizationException extends RuntimeException {
        public MediaSanitizationException(String msg) {
            super(msg);
        }

        public MediaSanitizationException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

}
