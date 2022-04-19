package technology.semi.weaviate.client.v1.graphql.query;

import technology.semi.weaviate.client.Config;
import technology.semi.weaviate.client.base.BaseClient;
import technology.semi.weaviate.client.base.ClientResult;
import technology.semi.weaviate.client.base.Response;
import technology.semi.weaviate.client.base.Result;
import technology.semi.weaviate.client.v1.graphql.model.GraphQLQuery;
import technology.semi.weaviate.client.v1.graphql.model.GraphQLResponse;
import technology.semi.weaviate.client.v1.graphql.query.argument.NearObjectArgument;
import technology.semi.weaviate.client.v1.graphql.query.argument.NearTextArgument;
import technology.semi.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import technology.semi.weaviate.client.v1.graphql.query.argument.WhereArgument;
import technology.semi.weaviate.client.v1.graphql.query.builder.AggregateBuilder;
import technology.semi.weaviate.client.v1.graphql.query.fields.Fields;

public class Aggregate extends BaseClient<GraphQLResponse> implements ClientResult<GraphQLResponse> {
  private final AggregateBuilder.AggregateBuilderBuilder aggregateBuilder;
  private Boolean includesNearMediaFilter;
  public Aggregate(Config config) {
    super(config);
    this.aggregateBuilder = AggregateBuilder.builder();
    this.includesNearMediaFilter = false;
  }

  public Aggregate withClassName(String className) {
    this.aggregateBuilder.className(className);
    return this;
  }

  public Aggregate withFields(Fields fields) {
    this.aggregateBuilder.fields(fields);
    return this;
  }

  public Aggregate withWhere(WhereArgument where) {
    this.aggregateBuilder.withWhereArgument(where);
    return this;
  }

  public Aggregate withGroupBy(String propertyName) {
    this.aggregateBuilder.groupByClausePropertyName(propertyName);
    return this;
  }

  public Aggregate withNearVector(NearVectorArgument withNearVectorFilter) throws Exception {
    if (this.includesNearMediaFilter) {
      throw new Exception("cannot use multiple near<Media> filters in a single query");
    }

    this.aggregateBuilder.withNearVectorFilter(withNearVectorFilter);
    this.includesNearMediaFilter = true;
    return this;
  }

  public Aggregate withNearObject(NearObjectArgument withNearObjectFilter) throws Exception {
    if (this.includesNearMediaFilter) {
      throw new Exception("cannot use multiple near<Media> filters in a single query");
    }

    this.aggregateBuilder.withNearObjectFilter(withNearObjectFilter);
    this.includesNearMediaFilter = true;
    return this;
  }

  public Aggregate withNearText(NearTextArgument withNearTextFilter) throws Exception {
    if (this.includesNearMediaFilter) {
      throw new Exception("cannot use multiple near<Media> filters in a single query");
    }

    this.aggregateBuilder.withNearTextFilter(withNearTextFilter);
    this.includesNearMediaFilter = true;
    return this;
  }

  @Override
  public Result<GraphQLResponse> run() {
    String aggregrateQuery = aggregateBuilder.build().buildQuery();
    GraphQLQuery query = GraphQLQuery.builder().query(aggregrateQuery).build();
    Response<GraphQLResponse> resp = sendPostRequest("/graphql", query, GraphQLResponse.class);
    return new Result<>(resp);
  }
}
