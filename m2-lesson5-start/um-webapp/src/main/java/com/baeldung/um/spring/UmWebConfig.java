package com.baeldung.um.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Configuration
@ComponentScan({"com.baeldung.um.web", "com.baeldung.common.web"})
public class UmWebConfig extends WebMvcConfigurerAdapter {

    public UmWebConfig() {
        super();
    }

    // configuration

    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        Stream.of(findJsonMapper(converters), findXmlMapper(converters))
                .filter(e -> e.isPresent())
            .map(e -> e.get())
            .forEach(this::enableSerializationAndDeserializationFeatures);
    }

    private void enableSerializationAndDeserializationFeatures(ObjectMapper mapper) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private Optional<ObjectMapper> findXmlMapper(List<HttpMessageConverter<?>> converters) {
        Optional<HttpMessageConverter<?>> xmlConverterFound = converters.stream()
                .filter(c -> c instanceof MappingJackson2XmlHttpMessageConverter)
                .findFirst();
        if (xmlConverterFound.isPresent()) {
            return Optional.of(((MappingJackson2XmlHttpMessageConverter) xmlConverterFound.get())
                    .getObjectMapper());
        }
        return Optional.empty();
    }

    private Optional<ObjectMapper> findJsonMapper(List<HttpMessageConverter<?>> converters) {
        final Optional<HttpMessageConverter<?>> jsonConverterFound = converters.stream()
                .filter(c -> c instanceof AbstractJackson2HttpMessageConverter)
                .findFirst();
        if (jsonConverterFound.isPresent()) {
            return Optional.of(((AbstractJackson2HttpMessageConverter) jsonConverterFound.get())
                    .getObjectMapper());
        }
        return Optional.empty();
    }
}
