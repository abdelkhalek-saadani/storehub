package proxiad.oms.pricing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.*;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProductResponseTransformer extends ResponseTransformer {

    private final List<JsonNode> products;

    public ProductResponseTransformer() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("__files/products.json");
            JsonNode root = mapper.readTree(is);
            products = StreamSupport.stream(root.spliterator(), false).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load products.json", e);
        }
    }


    @Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        String id = request.getUrl().replace("/products/", "");


        Optional<JsonNode> match = products.stream()
                .filter(p -> p.get("id").asText().equals(id))
                .findFirst();

        Optional<JsonNode> match2 = products.stream()
                .filter(p ->
                        {
                            return p.get("id").asText().equals(id);
                        }
                )
                .findFirst();

        if (match.isPresent()) {

            return Response.response()
                    .status(200)
                    .body(match.get().toString())
                    .headers(new HttpHeaders(HttpHeader.httpHeader("Content-Type", "application/json")))
                    .build();
        } else {

            return Response.response()
                    .status(404)
                    .body("{\"message\":\"Product not found\"}")
                    .headers(new HttpHeaders(HttpHeader.httpHeader("Content-Type", "application/json")))
                    .build();
        }
    }


    @Override
    public String getName() {
        return "product-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }
}
