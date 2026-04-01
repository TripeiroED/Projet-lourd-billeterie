package billeterie.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AppResources {
    private AppResources() {
    }

    public static InputStream openStream(String fileName) {
        String normalized = fileName.startsWith("/") ? fileName.substring(1) : fileName;
        String classpathPath = normalized.startsWith("resources/") ? "/" + normalized : "/resources/" + normalized;

        InputStream stream = AppResources.class.getResourceAsStream(classpathPath);
        if (stream != null) {
            return stream;
        }

        String relativeName = normalized.startsWith("resources/")
                ? normalized.substring("resources/".length())
                : normalized;

        Path[] candidates = new Path[] {
                Path.of("projet", "resources", relativeName),
                Path.of("resources", relativeName),
                Path.of(normalized)
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                try {
                    return Files.newInputStream(candidate);
                } catch (IOException ignored) {
                    // Try the next candidate.
                }
            }
        }

        return null;
    }
}
