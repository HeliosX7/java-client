package io.weaviate.client.v1.graphql.query.builder;

import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.client.v1.graphql.query.argument.AskArgument;
import io.weaviate.client.v1.graphql.query.argument.Bm25Argument;
import io.weaviate.client.v1.graphql.query.argument.GroupArgument;
import io.weaviate.client.v1.graphql.query.argument.GroupByArgument;
import io.weaviate.client.v1.graphql.query.argument.HybridArgument;
import io.weaviate.client.v1.graphql.query.argument.NearImageArgument;
import io.weaviate.client.v1.graphql.query.argument.NearObjectArgument;
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument;
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import io.weaviate.client.v1.graphql.query.argument.SortArguments;
import io.weaviate.client.v1.graphql.query.argument.WhereArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.graphql.query.fields.Fields;
import io.weaviate.client.v1.graphql.query.fields.GenerativeSearchBuilder;
import io.weaviate.client.v1.graphql.query.util.Serializer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Builder
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetBuilder implements Query {
  String className;
  Fields fields;
  Integer offset;
  Integer limit;
  String after;
  String withConsistencyLevel;
  WhereArgument withWhereFilter;
  NearTextArgument withNearTextFilter;
  Bm25Argument withBm25Filter;
  HybridArgument withHybridFilter;
  NearObjectArgument withNearObjectFilter;
  AskArgument withAskArgument;
  NearImageArgument withNearImageFilter;
  NearVectorArgument withNearVectorFilter;
  GroupArgument withGroupArgument;
  SortArguments withSortArguments;
  GenerativeSearchBuilder withGenerativeSearch;
  GroupByArgument withGroupByArgument;

  private boolean includesFilterClause() {
    return ObjectUtils.anyNotNull(withWhereFilter, withNearTextFilter, withNearObjectFilter, withNearVectorFilter,
      withNearImageFilter, withGroupArgument, withAskArgument, withBm25Filter, withHybridFilter, limit, offset,
      withSortArguments, withConsistencyLevel, withGroupByArgument);
  }

  private String createFilterClause() {
    if (includesFilterClause()) {
      Set<String> filters = new LinkedHashSet<>();

      if (withWhereFilter != null) {
        filters.add(withWhereFilter.build());
      }
      if (withNearTextFilter != null) {
        filters.add(withNearTextFilter.build());
      }
      if (withBm25Filter != null) {
        filters.add(withBm25Filter.build());
      }
      if (withHybridFilter != null) {
        filters.add(withHybridFilter.build());
      }
      if (withNearObjectFilter != null) {
        filters.add(withNearObjectFilter.build());
      }
      if (withNearVectorFilter != null) {
        filters.add(withNearVectorFilter.build());
      }
      if (withGroupArgument != null) {
        filters.add(withGroupArgument.build());
      }
      if (withAskArgument != null) {
        filters.add(withAskArgument.build());
      }
      if (withNearImageFilter != null) {
        filters.add(withNearImageFilter.build());
      }
      if (limit != null) {
        filters.add(String.format("limit:%s", limit));
      }
      if (offset != null) {
        filters.add(String.format("offset:%s", offset));
      }
      if (after != null) {
        filters.add(String.format("after:%s", Serializer.quote(after)));
      }
      if (withSortArguments != null) {
        filters.add(withSortArguments.build());
      }
      if (withConsistencyLevel != null) {
        filters.add(String.format("consistencyLevel:%s", withConsistencyLevel));
      }
      if (withGroupByArgument != null) {
        filters.add(withGroupByArgument.build());
      }

      return String.format("(%s)", String.join(",", filters));
    }
    return "";
  }

  private String createFields() {
    if (ObjectUtils.allNull(fields, withGenerativeSearch)) {
      return "";
    }

    if (withGenerativeSearch == null) {
      return fields.build();
    }

    Field generate = withGenerativeSearch.build();
    Field generateAdditional = Field.builder()
      .name("_additional")
      .fields(new Field[]{generate})
      .build();

    if (fields == null) {
      return generateAdditional.build();
    }

    // check if _additional field exists. If missing just add new _additional with generate,
    // if exists merge generate into present one
    Map<Boolean, List<Field>> grouped = Arrays.stream(fields.getFields())
      .collect(Collectors.groupingBy(f -> "_additional".equals(f.getName())));

    List<Field> additionals = grouped.getOrDefault(true, new ArrayList<>());
    if (additionals.isEmpty()) {
      additionals.add(generateAdditional);
    } else {
      Field[] mergedInternalFields = Stream.concat(
        Arrays.stream(additionals.get(0).getFields()),
        Stream.of(generate)
      ).toArray(Field[]::new);

      additionals.set(0, Field.builder()
        .name("_additional")
        .fields(mergedInternalFields)
        .build()
      );
    }

    Field[] allFields = Stream.concat(
      grouped.getOrDefault(false, new ArrayList<>()).stream(),
      additionals.stream()
    ).toArray(Field[]::new);

    return Fields.builder()
      .fields(allFields)
      .build()
      .build();
  }

  @Override
  public String buildQuery() {
    return String.format("{Get{%s%s{%s}}}", Serializer.escape(className), createFilterClause(), createFields());
  }


  // created to support both types of setters: WhereArgument and deprecated WhereFilter
  public static class GetBuilderBuilder {
    private WhereArgument withWhereFilter;

    @Deprecated
    public GetBuilderBuilder withWhereFilter(WhereFilter whereFilter) {
      this.withWhereFilter = WhereArgument.builder().filter(whereFilter).build();
      return this;
    }
    public GetBuilderBuilder withWhereFilter(WhereArgument whereArgument) {
      this.withWhereFilter = whereArgument;
      return this;
    }
  }
}
