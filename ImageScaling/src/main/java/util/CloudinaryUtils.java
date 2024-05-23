package util;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CloudinaryUtils {
    private static Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dpkxkkrnl",
            "api_key", "952447821531272",
            "api_secret", "5gikx65b5vs_eWYi4b7d40fiPHc",
            "secure", true)
    );

    public static Map upload(Map params) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(new File("doc.mp4"), params);
        return uploadResult;
    }

    public static void getById(Cloudinary cloudinary, String path) {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dpkxkkrnl",
                "api_key", "952447821531272",
                "api_secret", "5gikx65b5vs_eWYi4b7d40fiPHc",
                "secure", true));

    }
}
