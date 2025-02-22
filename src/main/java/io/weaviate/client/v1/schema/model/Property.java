package io.weaviate.client.v1.schema.model;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Property {
  String name;
  List<String> dataType;
  String description;
  String tokenization;
  /**
   * As of Weaviate v1.19 'indexInverted' is deprecated and replaced by 'indexFilterable'
   * and 'indexSearchable'.<br>
   * See <a href="https://weaviate.io/developers/weaviate/configuration/indexes#inverted-index">inverted index</a>
   */
  @Deprecated
  Boolean indexInverted;
  Boolean indexFilterable;
  Boolean indexSearchable;
  Object moduleConfig;
}
