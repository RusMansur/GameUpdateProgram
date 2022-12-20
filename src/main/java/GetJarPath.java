import java.io.File;
import java.net.URISyntaxException;

public interface GetJarPath {
    static File getPath() {
        File file;
        try {
            file = new File(
                    Main.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI().
                            getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}
