package io.quarkiverse.asyncapi.annotation.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(UnFilteredProfile.class)
public class AsyncApiAnnotationScannerUnFilteredTest {

    @Test
    void shouldScanAndFilterEmitterAnnotations() throws Exception {
        //given
        String yaml = Files.readAllLines(Path.of(System.getProperty("java.io.tmpdir"), "asyncApi.yaml")).stream()
                .collect(Collectors.joining("\n"));
        assertThat(yaml).isNotNull();
        JsonNode asyncAPI = ObjectMapperFactory.yaml().readTree(yaml);
        //when
        assertThat(asyncAPI.at("/channels")).isInstanceOf(ObjectNode.class);
        assertThat(asyncAPI.at("/channels")).hasSizeGreaterThanOrEqualTo(6);
        assertThat(asyncAPI.at("/channels/transfer-channel1/publish/message/payload")).hasSize(3);
        assertThat(
                asyncAPI.at("/channels/transfer-channel1/publish/message/payload/properties/value/properties/part/properties"))
                .hasSizeGreaterThan(3);
        assertThat(asyncAPI
                .at("/channels/transfer-channel1/publish/message/payload/properties/value/properties/company/properties"))
                .hasSize(7);
        JsonNode translationNodeOneOf = asyncAPI
                .at("/channels/channel-x/publish/message/payload/properties/translation/oneOf");
        assertThat(translationNodeOneOf).hasSize(2);
        assertThat(translationNodeOneOf.get(0).get("type").asText()).isEqualTo("string");
        assertThat(translationNodeOneOf.get(1).get("type").asText()).isEqualTo("object");
        assertThat(translationNodeOneOf.get(1).get("description").asText()).isNotNull();
        JsonNode oneOfOpenApiNodeOneOf = asyncAPI
                .at("/channels/channel-x/publish/message/payload/properties/openApiOneOfObject/oneOf");
        assertThat(oneOfOpenApiNodeOneOf.get(0).get("type").asText()).isEqualTo("string");
        assertThat(oneOfOpenApiNodeOneOf.get(1).get("type").asText()).isEqualTo("integer");
    }
}
