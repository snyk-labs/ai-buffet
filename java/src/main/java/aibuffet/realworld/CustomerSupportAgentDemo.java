package aibuffet.realworld;

import aibuffet.realworld.booking.BookingService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Standalone runner for the LangChain4j customer support agent example.
 *
 * <p>This is a simplified, Spring-free adaptation of the official example at
 * <a href="https://github.com/langchain4j/langchain4j-examples/tree/main/customer-support-agent-example">
 * langchain4j-examples/customer-support-agent-example</a>, which demonstrates a real-world
 * production-style agent with tools, chat memory, and RAG over company terms of use.
 *
 * <p>Try asking:
 * <ul>
 *   <li>"What is your cancellation policy?"</li>
 *   <li>"My name is John Doe and my booking number is MS-777. What are my booking details?"</li>
 *   <li>"Please cancel booking MS-777 for John Doe" (the agent should ask for confirmation first)</li>
 * </ul>
 */
public final class CustomerSupportAgentDemo {

    private static final String TERMS_RESOURCE = "/miles-of-smiles-terms-of-use.txt";

    private CustomerSupportAgentDemo() {}

    static CustomerSupportAgent createAgent(String apiKey) throws URISyntaxException {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .temperature(0.0)
                .build();

        ChatMemoryProvider memoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .build();

        ContentRetriever contentRetriever = buildContentRetriever();

        return AiServices.builder(CustomerSupportAgent.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(memoryProvider)
                .tools(new BookingTools(new BookingService()))
                .contentRetriever(contentRetriever)
                .build();
    }

    private static ContentRetriever buildContentRetriever() throws URISyntaxException {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        Path termsPath = Paths.get(
                CustomerSupportAgentDemo.class.getResource(TERMS_RESOURCE).toURI());
        Document document = FileSystemDocumentLoader.loadDocument(termsPath, new TextDocumentParser());

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(100, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .minScore(0.6)
                .build();
    }

    public static void main(String[] args) throws URISyntaxException {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("OPENAI_API_KEY is not set");
            System.exit(1);
        }

        CustomerSupportAgent agent = createAgent(apiKey);
        String sessionId = "demo-session";
        String currentDate = LocalDate.now().toString();

        System.out.println("Miles of Smiles customer support (type 'exit' to quit).");
        System.out.println("Demo booking: John Doe, MS-777\n");

        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        while (true) {
            System.out.print("You: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String userMessage = scanner.nextLine();
            if ("exit".equalsIgnoreCase(userMessage.trim())) {
                break;
            }

            try {
                Result<String> result = agent.answer(sessionId, userMessage, currentDate);
                System.out.println("Roger: " + result.content() + "\n");
            } catch (Exception err) {
                System.err.println("Error: " + err.getMessage() + "\n");
            }
        }
    }
}
