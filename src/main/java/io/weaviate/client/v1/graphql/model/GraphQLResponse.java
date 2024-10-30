package io.weaviate.client.v1.graphql.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.weaviate.client.base.SneakyError;
import io.weaviate.client.base.WeaviateErrorMessage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GraphQLResponse implements SneakyError {
  Object data;
  GraphQLError[] errors;


  /**
   * Extract the 'message' portion of every error in the response, omitting 'path' and 'location'.
   * 
   * @returns Non-throwable WeaviateErrorMessages
   */
  public List<WeaviateErrorMessage> errorMessages() {
    if (errors == null || errors.length == 0) {
      return null;
    }
    return Arrays.stream(errors).map(err -> {
      return new WeaviateErrorMessage(err.getMessage(), null);
    }).collect(Collectors.toList());
  }
}
