package com.coopera2075.cooperanki.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.type.format.jackson.Jackson3JsonFormatMapper;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;

class JsonFormatMapperConfigurationTest {
    @Test
    void configuresHibernateToUseJackson3ForJsonNodes() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            properties.load(input);
        }

        assertEquals(
                Jackson3JsonFormatMapper.class.getName(),
                properties.getProperty("spring.jpa.properties.hibernate.type.json_format_mapper"));

        Jackson3JsonFormatMapper mapper = new Jackson3JsonFormatMapper();
        JsonNode original = mapper.fromString(
                "{\"answers\":{\"socials\":[\"WhatsApp\",\"Facebook\"]}}",
                JsonNode.class);
        String serialized = mapper.toString(original, JsonNode.class);
        JsonNode restored = mapper.fromString(serialized, JsonNode.class);

        assertEquals(original, restored);
    }
}
