package com.project.doodle.evolvablebydesign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

  @Bean
  public static ObjectMapper objectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();

    final SimpleModule applicationSpecificSerializers = new SimpleModule();
    applicationSpecificSerializers.addSerializer(new Link.Serializer());
    applicationSpecificSerializers.addSerializer(new Link.Simple.Serializer());
    applicationSpecificSerializers.addSerializer(new Link.WithParameters.Serializer());
    applicationSpecificSerializers.addSerializer(new HypermediaRepresentation.Serializer());

    objectMapper.registerModule(applicationSpecificSerializers);
    objectMapper.registerModule(new ParameterNamesModule());
    objectMapper.registerModule(new Jdk8Module());
    objectMapper.registerModule(new JavaTimeModule());

    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    return objectMapper;
  }

}
