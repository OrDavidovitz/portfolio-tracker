package com.or.portfolio_tracker.price;

import com.or.portfolio_tracker.asset.AssetType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

@Component
public class AlphaVantagePriceProvider implements PriceProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper om = new ObjectMapper();

    public AlphaVantagePriceProvider(WebClient.Builder builder,
                                     @Value("${price.alpha-vantage.api-key}") String apiKey) {
        this.webClient = builder.baseUrl("https://www.alphavantage.co").build();
        this.apiKey = apiKey;
    }

    @Override
    public Optional<BigDecimal> getCurrentPrice(String symbol, AssetType type) {
        try {
            if (type == AssetType.CRYPTO) {
                return fetchCryptoUsd(symbol);
            } else {
                return fetchEquity(symbol);
            }
        } catch (WebClientResponseException e) {
            System.err.println("AlphaVantage API error for " + symbol + ": " + e.getRawStatusCode());
        } catch (Exception e) {
            System.err.println("Unexpected error fetching price for " + symbol + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    private Optional<BigDecimal> fetchEquity(String symbol) throws Exception {
        String sym = symbol.toUpperCase(Locale.ROOT);

        String json = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/query")
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", sym)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (json == null || json.isBlank()) return Optional.empty();

        var root = om.readTree(json);
        var quote = root.path("Global Quote");
        var priceNode = quote.path("05. price");
        if (priceNode.isMissingNode() || priceNode.asText().isBlank()) return Optional.empty();

        return Optional.of(new BigDecimal(priceNode.asText()));
    }

    private Optional<BigDecimal> fetchCryptoUsd(String symbol) throws Exception {
        String sym = symbol.toUpperCase(Locale.ROOT);

        String json = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/query")
                        .queryParam("function", "CURRENCY_EXCHANGE_RATE")
                        .queryParam("from_currency", sym)
                        .queryParam("to_currency", "USD")
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (json == null || json.isBlank()) return Optional.empty();

        var root = om.readTree(json);
        var rateNode = root.path("Realtime Currency Exchange Rate").path("5. Exchange Rate");
        if (rateNode.isMissingNode() || rateNode.asText().isBlank()) return Optional.empty();

        return Optional.of(new BigDecimal(rateNode.asText()));
    }
}