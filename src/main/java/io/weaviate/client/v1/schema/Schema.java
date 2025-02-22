package io.weaviate.client.v1.schema;

import io.weaviate.client.base.http.HttpClient;
import io.weaviate.client.Config;
import io.weaviate.client.v1.schema.api.ClassCreator;
import io.weaviate.client.v1.schema.api.ClassDeleter;
import io.weaviate.client.v1.schema.api.ClassExists;
import io.weaviate.client.v1.schema.api.ClassGetter;
import io.weaviate.client.v1.schema.api.PropertyCreator;
import io.weaviate.client.v1.schema.api.SchemaDeleter;
import io.weaviate.client.v1.schema.api.SchemaGetter;
import io.weaviate.client.v1.schema.api.ShardUpdater;
import io.weaviate.client.v1.schema.api.ShardsGetter;
import io.weaviate.client.v1.schema.api.ShardsUpdater;

public class Schema {
  private final Config config;
  private final HttpClient httpClient;

  public Schema(HttpClient httpClient, Config config) {
    this.config = config;
    this.httpClient = httpClient;
  }

  public SchemaGetter getter() {
    return new SchemaGetter(httpClient, config);
  }

  public ClassGetter classGetter() {
    return new ClassGetter(httpClient, config);
  }

  public ClassExists exists() {
    return new ClassExists(new ClassGetter(httpClient, config));
  }

  public ClassCreator classCreator() {
    return new ClassCreator(httpClient, config);
  }

  public ClassDeleter classDeleter() {
    return new ClassDeleter(httpClient, config);
  }

  public PropertyCreator propertyCreator() {
    return new PropertyCreator(httpClient, config);
  }

  public SchemaDeleter allDeleter() {
    return new SchemaDeleter(new SchemaGetter(httpClient, config), new ClassDeleter(httpClient, config));
  }

  public ShardsGetter shardsGetter() {
    return new ShardsGetter(httpClient, config);
  }

  public ShardUpdater shardUpdater() {
    return new ShardUpdater(httpClient, config);
  }

  public ShardsUpdater shardsUpdater() {
    return new ShardsUpdater(httpClient, config);
  }
}
