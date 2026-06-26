package proxiad.oms.pricing;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductWireMockServer {
    private final WireMockServer wireMockServer;

    public ProductWireMockServer(int port) {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.options()
                        .port(port)
                        .usingFilesUnderDirectory("src/test/resources")
                        .extensions(new ProductResponseTransformer(),
                                new DiscountResponseTransformer())// REGISTER TRANSFORMER

        );
    }

    public void start() {
        wireMockServer.start();
        log.info("WireMock server started on port {}", wireMockServer.port());

    }

    public void stop() {
        wireMockServer.stop();
        log.info("WireMock server stopped");
    }

    public void resetMappings() {
        wireMockServer.resetMappings();
    }

    public static void main(String[] args) {
        ProductWireMockServer server = new ProductWireMockServer(3000);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}
