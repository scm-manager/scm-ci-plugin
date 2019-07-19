package com.cloudogu.scm.ci.cistatus.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class CIStatusDtoCollectionTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUpObjectMapper() {
    objectMapper = new ObjectMapper();
    // ensure jaxb annotation handler is registered
    objectMapper.findAndRegisterModules();
  }

  @Test
  void shouldNotWrapCollection() throws IOException {
    CIStatusDto dto = new CIStatusDto();
    dto.setName("test");

    CIStatusDtoCollection collection = new CIStatusDtoCollection(Collections.singleton(dto));
    JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collection));

    assertThat(jsonNode.isArray()).isTrue();
    assertThat(jsonNode.get(0).get("name").asText()).isEqualTo("test");
  }

  public static class CollectionWrapper {

    private CIStatusDtoCollection collection;

    public CollectionWrapper(CIStatusDtoCollection collection) {
      this.collection = collection;
    }

    public CIStatusDtoCollection getCollection() {
      return collection;
    }

  }

}
