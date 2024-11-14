package io.weaviate.client.v1.async.schema.api;

import io.weaviate.client.Config;
import io.weaviate.client.base.AsyncBaseClient;
import io.weaviate.client.base.AsyncClientResult;
import io.weaviate.client.base.Response;
import io.weaviate.client.base.Result;
import io.weaviate.client.base.http.async.ResponseParser;
import io.weaviate.client.base.util.UrlEncoder;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import java.util.concurrent.Future;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;

public class ClassUpdater extends AsyncBaseClient<Boolean> implements AsyncClientResult<Boolean> {

  private WeaviateClass clazz;

  public ClassUpdater(CloseableHttpAsyncClient client, Config config) {
    super(client, config);
  }

  public ClassUpdater withClass(WeaviateClass clazz) {
    this.clazz = clazz;
    return this;
  }

  @Override
  public Future<Result<Boolean>> run(FutureCallback<Result<Boolean>> callback) {
    String path = String.format("/schema/%s", UrlEncoder.encodePathParam(clazz.getClassName()));
    return sendPutRequest(path, clazz, callback, new ResponseParser<Boolean>() {
      @Override
      public Result<Boolean> parse(HttpResponse response, String body, ContentType contentType) {
        Response<WeaviateClass> resp = this.serializer.toResponse(response.getCode(), body, WeaviateClass.class);
        return new Result<>(resp.getStatusCode(), resp.getStatusCode() == 200, resp.getErrors());
      }
    });
  }
}
