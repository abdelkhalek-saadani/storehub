package com.abdelkhalek.storehub.order;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductWireMockServer {
    private final WireMockServer wireMockServer;

    public ProductWireMockServer(int port) {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.options()
                        .port(port)
                        .usingFilesUnderDirectory("src/test/resources")
                        .mappingSource(new JsonFileMappingsSource(new ClasspathFileSource("src/test/resources/mappings/product")))
                        .extensions(new ResponseTemplateTransformer(true))// REGISTER TRANSFORMER

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
