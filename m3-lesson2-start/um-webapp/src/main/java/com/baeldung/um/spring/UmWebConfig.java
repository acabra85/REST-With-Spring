package com.baeldung.um.spring;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.baeldung.um.persistence.model.Privilege;
import com.baeldung.um.persistence.model.Role;
import com.baeldung.um.persistence.model.User;

@Configuration
@ComponentScan({ "com.baeldung.common.web", "com.baeldung.um.web" })
public class UmWebConfig implements WebMvcConfigurer {

    public UmWebConfig() {
        super();
    }

    // beans

    public XStreamMarshaller xstreamMarshaller() {
        final XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAutodetectAnnotations(true);
        xStreamMarshaller.setAnnotatedClasses(new Class[] { User.class, Role.class, Privilege.class });
        xStreamMarshaller.getXStream()
            .addDefaultImplementation(java.sql.Timestamp.class, java.util.Date.class);

        return xStreamMarshaller;
    }

    public MarshallingHttpMessageConverter marshallingHttpMessageConverter() {
        final MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();
        final XStreamMarshaller xstreamMarshaller = xstreamMarshaller();
        marshallingHttpMessageConverter.setMarshaller(xstreamMarshaller);
        marshallingHttpMessageConverter.setUnmarshaller(xstreamMarshaller);

        return marshallingHttpMessageConverter;
    }

    // template

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(marshallingHttpMessageConverter());

        final ClassLoader classLoader = getClass().getClassLoader();
        if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader)) {
            messageConverters.add(new MappingJackson2HttpMessageConverter());
        }

        WebMvcConfigurer.super.configureMessageConverters(messageConverters);
    }

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
