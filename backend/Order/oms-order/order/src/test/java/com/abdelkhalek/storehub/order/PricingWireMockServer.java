package com.abdelkhalek.storehub.order;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PricingWireMockServer {
    private final WireMockServer wireMockServer;

    public PricingWireMockServer(int port) {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.options()
                        .port(port)
                        .usingFilesUnderDirectory("src/test/resources")
                        .mappingSource(new JsonFileMappingsSource(new ClasspathFileSource("src/test/resources/mappings/pricing")))


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
        PricingWireMockServer server = new PricingWireMockServer(3060);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}
