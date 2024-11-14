package io.weaviate.integration.tests.batch;

import io.weaviate.client.base.Result;
import io.weaviate.client.v1.batch.model.BatchDeleteOutput;
import io.weaviate.client.v1.batch.model.BatchDeleteResponse;
import io.weaviate.client.v1.batch.model.BatchDeleteResultStatus;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.data.replication.model.ConsistencyLevel;
import io.weaviate.client.v1.filters.Operator;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.integration.client.WeaviateTestGenerics;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import static org.assertj.core.api.Assertions.assertThat;

public class ClientBatchDeleteTestSuite {
  public static void testBatchDeleteDryRunVerbose(Supplier<Result<List<WeaviateObject>>> getObjects, Function<WhereFilter, Result<BatchDeleteResponse>> batchDelete) {
    // when
    WhereFilter whereFilter = WhereFilter.builder()
      .operator(Operator.Equal)
      .path(new String[]{ "name" })
      .valueText("Hawaii")
      .build();

    int allWeaviateObjects = countWeaviateObjects(getObjects);

    Result<BatchDeleteResponse> resResponse = batchDelete.apply(whereFilter);
    int remainingWeaviateObjects = countWeaviateObjects(getObjects);

    // then
    assertThat(remainingWeaviateObjects).isEqualTo(allWeaviateObjects);
    assertThat(resResponse).isNotNull();
    assertThat(resResponse.hasErrors()).isFalse();

    BatchDeleteResponse response = resResponse.getResult();
    assertThat(response).isNotNull();
    assertThat(response.getDryRun()).isTrue();
    assertThat(response.getOutput()).isEqualTo(BatchDeleteOutput.VERBOSE);

    BatchDeleteResponse.Match match = response.getMatch();
    assertThat(match).isNotNull();
    assertThat(match.getClassName()).isEqualTo("Pizza");
    assertThat(match.getWhereFilter()).isEqualTo(whereFilter);

    BatchDeleteResponse.Results results = response.getResults();
    assertThat(results).isNotNull();
    assertThat(results.getSuccessful()).isZero();
    assertThat(results.getFailed()).isZero();
    assertThat(results.getLimit()).isEqualTo(10000L);
    assertThat(results.getMatches()).isEqualTo(1L);
    assertThat(results.getObjects()).hasSize(1);

    BatchDeleteResponse.ResultObject object = results.getObjects()[0];
    assertThat(object).isNotNull();
    assertThat(object.getId()).isEqualTo(WeaviateTestGenerics.PIZZA_HAWAII_ID);
    assertThat(object.getStatus()).isEqualTo(BatchDeleteResultStatus.DRYRUN);
    assertThat(object.getErrors()).isNull();
  }

  public static void testBatchDeleteDryRunMinimal(Supplier<Result<List<WeaviateObject>>> getObjects, Function<WhereFilter, Result<BatchDeleteResponse>> batchDelete) {
    // when
    WhereFilter whereFilter = WhereFilter.builder()
      .operator(Operator.Like)
      .path(new String[]{ "description" })
      .valueText("microscopic")
      .build();

    int allWeaviateObjects = countWeaviateObjects(getObjects);
    Result<BatchDeleteResponse> resResponse = batchDelete.apply(whereFilter);
    int remainingWeaviateObjects = countWeaviateObjects(getObjects);

    // then
    assertThat(remainingWeaviateObjects).isEqualTo(allWeaviateObjects);
    assertThat(resResponse).isNotNull();
    assertThat(resResponse.hasErrors()).isFalse();

    BatchDeleteResponse response = resResponse.getResult();
    assertThat(response).isNotNull();
    assertThat(response.getDryRun()).isTrue();
    assertThat(response.getOutput()).isEqualTo(BatchDeleteOutput.MINIMAL);

    BatchDeleteResponse.Match match = response.getMatch();
    assertThat(match).isNotNull();
    assertThat(match.getClassName()).isEqualTo("Soup");
    assertThat(match.getWhereFilter()).isEqualTo(whereFilter);

    BatchDeleteResponse.Results results = response.getResults();
    assertThat(results).isNotNull();
    assertThat(results.getSuccessful()).isZero();
    assertThat(results.getFailed()).isZero();
    assertThat(results.getLimit()).isEqualTo(10000L);
    assertThat(results.getMatches()).isEqualTo(1L);
    assertThat(results.getObjects()).isNull();
  }

  public static void testBatchDeleteNoMatchWithDefaultOutputAndDryRun(Supplier<Result<List<WeaviateObject>>> getObjects, Function<WhereFilter, Result<BatchDeleteResponse>> batchDelete) {
    // when
    long inAMinute = Instant.now().plusSeconds(60).toEpochMilli();
    WhereFilter whereFilter = WhereFilter.builder()
      .operator(Operator.GreaterThan)
      .path(new String[]{ "_creationTimeUnix" })
      .valueText(Long.toString(inAMinute))
      .build();

    int allWeaviateObjects = countWeaviateObjects(getObjects);
    Result<BatchDeleteResponse> response = batchDelete.apply(whereFilter);
    int remainingWeaviateObjects = countWeaviateObjects(getObjects);

    // then
    assertThat(remainingWeaviateObjects).isEqualTo(allWeaviateObjects);
    assertThat(response).isNotNull();
    assertThat(response.hasErrors()).isFalse();

    BatchDeleteResponse result = response.getResult();
    assertThat(response.getResult()).isNotNull();
    assertThat(result.getDryRun()).isFalse();
    assertThat(result.getOutput()).isEqualTo(BatchDeleteOutput.MINIMAL);

    BatchDeleteResponse.Match match = result.getMatch();
    assertThat(match).isNotNull();
    assertThat(match.getClassName()).isEqualTo("Pizza");
    assertThat(match.getWhereFilter()).isEqualTo(whereFilter);

    BatchDeleteResponse.Results results = result.getResults();
    assertThat(results).isNotNull();
    assertThat(results.getSuccessful()).isZero();
    assertThat(results.getFailed()).isZero();
    assertThat(results.getLimit()).isEqualTo(10000L);
    assertThat(results.getMatches()).isZero();
    assertThat(results.getObjects()).isNull();
  }

  public static void testBatchDeleteAllMatchesWithDefaultDryRun(Supplier<Result<List<WeaviateObject>>> getObjects, Function<WhereFilter, Result<BatchDeleteResponse>> batchDelete) {
    // when
    long inAMinute = Instant.now().plusSeconds(60).toEpochMilli();
    WhereFilter whereFilter = WhereFilter.builder()
      .operator(Operator.LessThan)
      .path(new String[]{ "_creationTimeUnix" })
      .valueText(Long.toString(inAMinute))
      .build();

    int allWeaviateObjects = countWeaviateObjects(getObjects);
    Result<BatchDeleteResponse> response = batchDelete.apply(whereFilter);
    int remainingWeaviateObjects = countWeaviateObjects(getObjects);

    // then
    assertThat(remainingWeaviateObjects).isEqualTo(allWeaviateObjects - 4);
    assertThat(response).isNotNull();
    assertThat(response.hasErrors()).isFalse();

    BatchDeleteResponse result = response.getResult();
    assertThat(response.getResult()).isNotNull();
    assertThat(result.getDryRun()).isFalse();
    assertThat(result.getOutput()).isEqualTo(BatchDeleteOutput.VERBOSE);

    BatchDeleteResponse.Match match = result.getMatch();
    assertThat(match).isNotNull();
    assertThat(match.getClassName()).isEqualTo("Pizza");
    assertThat(match.getWhereFilter()).isEqualTo(whereFilter);

    BatchDeleteResponse.Results results = result.getResults();
    assertThat(results).isNotNull();
    assertThat(results.getSuccessful()).isEqualTo(4);
    assertThat(results.getFailed()).isZero();
    assertThat(results.getLimit()).isEqualTo(10000L);
    assertThat(results.getMatches()).isEqualTo(4);

    BatchDeleteResponse.ResultObject[] objects = results.getObjects();
    assertThat(objects).hasSize(4);
    assertThat(objects).doesNotContainNull();
    assertThat(objects).extracting(BatchDeleteResponse.ResultObject::getStatus)
      .containsOnly(BatchDeleteResultStatus.SUCCESS);
    assertThat(objects).extracting(BatchDeleteResponse.ResultObject::getErrors)
      .containsOnlyNulls();
    assertThat(objects).extracting(BatchDeleteResponse.ResultObject::getId)
      .contains(WeaviateTestGenerics.PIZZA_HAWAII_ID, WeaviateTestGenerics.PIZZA_DOENER_ID,
        WeaviateTestGenerics.PIZZA_QUATTRO_FORMAGGI_ID, WeaviateTestGenerics.PIZZA_FRUTTI_DI_MARE_ID);
  }

  private static int countWeaviateObjects(Supplier<Result<List<WeaviateObject>>> getObjects) {
    Result<List<WeaviateObject>> resResponse = getObjects.get();
    assertThat(resResponse).isNotNull();
    assertThat(resResponse.hasErrors()).isFalse();

    List<WeaviateObject> response = resResponse.getResult();
    assertThat(response).isNotNull();

    return response.size();
  }
}
