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

public class DiscountResponseTransformer extends ResponseTransformer {

    private final List<JsonNode> discounts;

    public DiscountResponseTransformer() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("__files/discounts.json");
            JsonNode root = mapper.readTree(is);
            discounts = StreamSupport.stream(root.spliterator(), false).collect(Collectors.toList());
            System.out.println(discounts);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load discounts.json", e);
        }
    }



    @Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        String productId = request.queryParameter("productId").firstValue();

        if (productId == null || productId.isEmpty()) {
            return Response.response()
                    .status(400)
                    .body("{\"message\":\"Missing productId query parameter\"}")
                    .build();
        }

        Optional<JsonNode> match = discounts.stream()
                .filter(p -> p.get("productId").asText().equals(productId))
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
                    .build();
        }
    }


    @Override
    public String getName() {
        return "discount-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }
}
