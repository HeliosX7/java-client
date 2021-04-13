package technology.semi.weaviate.client.v1.misc.api;

import technology.semi.weaviate.client.Config;
import technology.semi.weaviate.client.base.BaseClient;
import technology.semi.weaviate.client.base.ClientResult;
import technology.semi.weaviate.client.base.Response;
import technology.semi.weaviate.client.base.Result;

public class ReadyChecker extends BaseClient<String> implements ClientResult<Boolean> {

  public ReadyChecker(Config config) {
    super(config);
  }

  @Override
  public Result<Boolean> run() {
    Response<String> resp = sendGetRequest("/.well-known/ready", String.class);
    return new Result<>(resp.getStatusCode(), resp.getStatusCode() == 200, resp.getErrors());
  }
}
