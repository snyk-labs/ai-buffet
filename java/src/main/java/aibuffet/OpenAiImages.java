package aibuffet;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.images.ImageGenerateParams;
import com.openai.models.images.ImageModel;
import com.openai.models.images.ImagesResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Image generation using the OpenAI Images API (DALL-E).
 */
public final class OpenAiImages {

    private static final ImageModel MODEL = ImageModel.DALL_E_3;
    private static final ImageGenerateParams.Size SIZE = ImageGenerateParams.Size._1024X1024;

    private static OpenAIClient getClient() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not set");
        }
        return OpenAIOkHttpClient.builder().apiKey(apiKey).build();
    }

    public static List<String> generateImage(String prompt) {
        OpenAIClient client = getClient();
        ImageGenerateParams params = ImageGenerateParams.builder()
                .model(MODEL)
                .prompt(prompt)
                .size(SIZE)
                .n(1L)
                .responseFormat(ImageGenerateParams.ResponseFormat.URL)
                .quality(ImageGenerateParams.Quality.STANDARD)
                .build();

        ImagesResponse response = client.images().generate(params);
        List<String> urls = new ArrayList<>();
        response.data().ifPresent(images -> images.forEach(image ->
                image.url().ifPresent(urls::add)));
        return urls;
    }

    public static void main(String[] args) {
        String prompt = args.length > 0
                ? String.join(" ", args)
                : "A serene beach at sunset with gentle waves and a clear sky.";

        try {
            System.out.println("Generating image...");
            List<String> urls = generateImage(prompt);
            if (urls.isEmpty()) {
                System.out.println("No image URL returned.");
                return;
            }

            System.out.println("Image URL: " + urls.get(0));
            Path outPath = Path.of("generated-image-url.txt");
            Files.writeString(outPath, urls.get(0));
            System.out.println("URL written to " + outPath.toAbsolutePath());
        } catch (Exception err) {
            System.err.println(err.getMessage());
            System.exit(1);
        }
    }
}
