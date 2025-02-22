package io.weaviate.client.v1.batch.util;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import io.weaviate.client.base.util.TriConsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReferencesPath {

  public String buildCreate(Params params) {
    return build(
      params,
      this::addQueryConsistencyLevel
    );
  }

  @SafeVarargs
  private final String build(Params params, TriConsumer<Params, List<String>, List<String>>... appenders) {
    Objects.requireNonNull(params);

    List<String> pathParams = new ArrayList<>();
    List<String> queryParams = new ArrayList<>();

    pathParams.add("/batch/references");
    Arrays.stream(appenders).forEach(consumer -> consumer.accept(params, pathParams, queryParams));

    String path = String.join("/", pathParams);
    if (!queryParams.isEmpty()) {
      return path + "?" + String.join("&", queryParams);
    }
    return path;
  }


  private void addQueryConsistencyLevel(Params params, List<String> pathParams, List<String> queryParams) {
    if (StringUtils.isNotBlank(params.consistencyLevel)) {
      queryParams.add(String.format("%s=%s", "consistency_level", StringUtils.trim(params.consistencyLevel)));
    }
  }


  @Builder
  @ToString
  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public static class Params {

    String consistencyLevel;
  }
}
