package br.com.concrete.mock.infra.component;

import br.com.concrete.mock.configuration.model.CaptureState;
import br.com.concrete.mock.configuration.repository.CaptureStateRepository;
import br.com.concrete.mock.generic.model.ExternalApiResult;
import br.com.concrete.mock.generic.model.Request;
import br.com.concrete.mock.infra.model.UriConfiguration;
import br.com.concrete.mock.infra.property.ApiProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class ExternalApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalApi.class);

    private final ApiProperty apiProperty;
    private final QueryStringBuilder queryStringBuilder;
    private final RestTemplate restTemplate;
    private final HeaderFilter headerFilter;
    private final CaptureStateRepository captureStateRepository;

    @Autowired
    public ExternalApi(ApiProperty apiProperty, QueryStringBuilder queryStringBuilder, RestTemplate restTemplate,
                       HeaderFilter headerFilter, CaptureStateRepository captureStateRepository) {
        this.apiProperty = apiProperty;
        this.queryStringBuilder = queryStringBuilder;
        this.restTemplate = restTemplate;
        this.headerFilter = headerFilter;
        this.captureStateRepository = captureStateRepository;
    }

    public Optional<ExternalApiResult> execute(final Request request) {
        final Boolean state = captureStateRepository
                .getCurrent()
                .map(CaptureState::isEnabled)
                .orElse(true);


        final UriConfiguration uriConfiguration = apiProperty
                .getConfiguration(request.getUri())
                .orElse(new UriConfiguration(apiProperty.getHost(), Pattern.compile(".*"), state));
        final Optional<HttpHeaders> httpHeaders = headerFilter.execute(request.getHeaders());

        LOGGER.info("### EXTERNAL API ###");
        LOGGER.info("{}", uriConfiguration);
        request.getBody().ifPresent(LOGGER::info);
        httpHeaders.ifPresent(list -> LOGGER.info(list.toString()));

        final HttpEntity<String> entity = httpHeaders
                .map(headers -> request.getBody().map(body -> new HttpEntity<>(body, headers))
                        .orElse(new HttpEntity<>(headers)))
                .orElse(request.getBody().map(HttpEntity<String>::new).orElse(new HttpEntity<>((String) null)));

        final String parameters = request.getQuery().map(queryStringBuilder::fromMap).orElse("");
        final String url = uriConfiguration
                .getHost()
                .concat(request.getUri())
                .concat(parameters);

        LOGGER.info("URL => {}", url);
        ResponseEntity<String> apiResult = null;
        try {
            apiResult = restTemplate.exchange(url, HttpMethod.valueOf(request.getMethod().name().toUpperCase()), entity,
                    String.class);
            return Optional.of(new ExternalApiResult(apiResult, uriConfiguration));
        } catch (Exception e) {
            LOGGER.error("Erro chamada API " + HttpMethod.valueOf(request.getMethod().name().toUpperCase()) + " " + url,
                    e);

            LOGGER.error("Tentando mais uma vez " + HttpMethod.valueOf(request.getMethod().name().toUpperCase()) + " "
                    + url);
        }
        // }

        return Optional.empty();

    }

}
