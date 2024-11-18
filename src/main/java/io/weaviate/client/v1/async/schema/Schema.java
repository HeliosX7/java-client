package io.weaviate.client.v1.async.schema;

import io.weaviate.client.Config;
import io.weaviate.client.v1.async.schema.api.ClassCreator;
import io.weaviate.client.v1.async.schema.api.ClassDeleter;
import io.weaviate.client.v1.async.schema.api.ClassExists;
import io.weaviate.client.v1.async.schema.api.ClassGetter;
import io.weaviate.client.v1.async.schema.api.ClassUpdater;
import io.weaviate.client.v1.async.schema.api.SchemaGetter;
import io.weaviate.client.v1.async.schema.api.PropertyCreator;
import io.weaviate.client.v1.async.schema.api.SchemaDeleter;
import io.weaviate.client.v1.async.schema.api.ShardsGetter;
import io.weaviate.client.v1.async.schema.api.ShardsUpdater;
import io.weaviate.client.v1.async.schema.api.TenantsCreator;
import io.weaviate.client.v1.async.schema.api.TenantsGetter;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;

public class Schema {
  private final CloseableHttpAsyncClient client;
  private final Config config;

  public Schema(CloseableHttpAsyncClient client, Config config) {
    this.client = client;
    this.config = config;
  }

  public SchemaGetter getter() {
    return new SchemaGetter(client, config);
  }

  public ClassGetter classGetter() {
    return new ClassGetter(client, config);
  }

  public ClassExists exists() {
    return new ClassExists(client, config);
  }

  public ClassCreator classCreator() {
    return new ClassCreator(client, config);
  }

  public ClassUpdater classUpdater() {
    return new ClassUpdater(client, config);
  }

  public ClassDeleter classDeleter() {
    return new ClassDeleter(client, config);
  }

  public PropertyCreator propertyCreator() {
    return new PropertyCreator(client, config);
  }

  public SchemaDeleter allDeleter() {
    return new SchemaDeleter(new SchemaGetter(client, config), new ClassDeleter(client, config));
  }

  public ShardsGetter shardsGetter() {
    return new ShardsGetter(client, config);
  }

  public ShardsUpdater shardsUpdater() {
    return new ShardsUpdater(client, config);
  }

  public TenantsCreator tenantsCreator() {
    return new TenantsCreator(client, config);
  }

  // TODO:async implement tenants updater and dbVersionSupport
  // which is being used here
//  public TenantsUpdater tenantsUpdater() {
//    return new TenantsUpdater(client, config, dbVersionSupport);
//  }

  public TenantsGetter tenantsGetter() {
    return new TenantsGetter(client, config);
  }
}
