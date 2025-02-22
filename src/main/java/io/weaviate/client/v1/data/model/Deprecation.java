package io.weaviate.client.v1.data.model;

import java.util.Date;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Deprecation {
  String apiType;
  String id;
  String[] locations;
  String mitigation;
  String msg;
  String plannedRemovalVersion;
  String removedIn;
  Date removedTime;
  Date sinceTime;
  String sinceVersion;
  String status;
}
