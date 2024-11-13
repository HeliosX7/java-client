package io.weaviate.integration.tests.classifications;

import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.batch.model.ObjectGetResponse;
import io.weaviate.client.v1.classifications.model.Classification;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.schema.model.DataType;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import io.weaviate.integration.client.WeaviateTestGenerics;
import org.junit.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClassificationsTestSuite {

  public static void testScheduler(Supplier<Result<Classification>> supplier,
                                   Supplier<Result<Classification>> supplierComplete,
                                   WeaviateTestGenerics testGenerics,
                                   WeaviateClient client) {

    // given
    createClassificationClasses(client, testGenerics);

    // when
    Result<Classification> classification = supplier.get();
    Result<Classification> classificationWithComplete = supplierComplete.get();

    // then
    assertNotNull(classification);
    assertNotNull(classification.getResult());
    assertTrue(Arrays.asList(classification.getResult().getBasedOnProperties()).contains("description"));
    assertTrue(Arrays.asList(classification.getResult().getClassifyProperties()).contains("tagged"));
    assertEquals("running", classification.getResult().getStatus());
    assertNotNull(classificationWithComplete);
    assertNotNull(classificationWithComplete.getResult());
    assertTrue(Arrays.asList(classificationWithComplete.getResult().getBasedOnProperties()).contains("description"));
    assertTrue(Arrays.asList(classificationWithComplete.getResult().getClassifyProperties()).contains("tagged"));
    assertNotEquals("running", classificationWithComplete.getResult().getStatus());
  }

  public static void testGetter(Supplier<Result<Classification>> supplierScheduler,
                                Function<String, Result<Classification>> supplierGetter,
                                WeaviateTestGenerics testGenerics,
                                WeaviateClient client) {
    // given
    createClassificationClasses(client, testGenerics);

    // when
    Result<Classification> classification = supplierScheduler.get();
    Result<Classification> knnClassification = supplierGetter.apply(classification.getResult().getId());

    // then
    assertNotNull(classification);
    assertNotNull(classification.getResult());
    assertNotNull(knnClassification);
    assertNotNull(knnClassification.getResult());
    assertEquals(classification.getResult().getId(), knnClassification.getResult().getId());
    assertTrue(knnClassification.getResult().getSettings() instanceof Map);
    Map settings = (Map) knnClassification.getResult().getSettings();
    assertEquals(3.0, settings.get("k"));
  }


  private static void createClassificationClasses(WeaviateClient client, WeaviateTestGenerics testGenerics) {
    testGenerics.createWeaviateTestSchemaFood(client);
    // define Tag class
    Property nameProperty = Property.builder()
      .dataType(Arrays.asList(DataType.TEXT))
      .description("name")
      .name("name")
      .build();
    WeaviateClass schemaClassTag = WeaviateClass.builder()
      .className("Tag")
      .description("tag for a pizza")
      .properties(Stream.of(nameProperty).collect(Collectors.toList()))
      .build();
    Result<Boolean> classCreate = client.schema().classCreator().withClass(schemaClassTag).run();
    assertNotNull(classCreate);
    assertTrue(classCreate.getResult());
    // add tagged property
    Property tagProperty = Property.builder()
      .dataType(Arrays.asList("Tag"))
      .description("tag of pizza")
      .name("tagged")
      .build();
    Result<Boolean> addTaggedProperty = client.schema().propertyCreator().withProperty(tagProperty).withClassName("Pizza").run();
    assertNotNull(addTaggedProperty);
    assertTrue(addTaggedProperty.getResult());
    // create 2 pizzas
    String pizza1ID = "97fa5147-bdad-4d74-9a81-f8babc811b09";
    WeaviateObject pizza1 = WeaviateObject.builder().className("Pizza").id(pizza1ID).properties(new HashMap<String, Object>() {{
      put("name", "Quattro Formaggi");
      put("description", "Pizza quattro formaggi Italian: [ˈkwattro forˈmaddʒi] (four cheese pizza) is a variety of pizza in Italian cuisine that is topped " +
        "with a combination of four kinds of cheese, usually melted together, with (rossa, red) or without (bianca, white) tomato sauce. It is popular " +
        "worldwide, including in Italy,[1] and is one of the iconic items from pizzerias's menus.");
    }}).build();
    String pizza2ID = "97fa5147-bdad-4d74-9a81-f8babc811b19";
    WeaviateObject pizza2 = WeaviateObject.builder().className("Pizza").id(pizza2ID).properties(new HashMap<String, java.lang.Object>() {{
      put("name", "Frutti di Mare");
      put("description", "Frutti di Mare is an Italian type of pizza that may be served with scampi, mussels or squid. It typically lacks cheese, with the seafood being served atop a tomato sauce.");
    }}).build();
    Result<ObjectGetResponse[]> batchImport = client.batch().objectsBatcher().withObjects(pizza1, pizza2).run();
    assertNotNull(batchImport);
    assertNotNull(batchImport.getResult());
    Assert.assertEquals(2, batchImport.getResult().length);
    // create 2 tags
    WeaviateObject tag1 = WeaviateObject.builder().className("Tag").properties(new HashMap<String, java.lang.Object>() {{
      put("name", "vegetarian");
    }}).build();
    WeaviateObject tag2 = WeaviateObject.builder().className("Tag").properties(new HashMap<String, java.lang.Object>() {{
      put("name", "seafood");
    }}).build();
    Result<ObjectGetResponse[]> batchImport2 = client.batch().objectsBatcher().withObjects(tag1, tag2).run();
    assertNotNull(batchImport2);
    assertNotNull(batchImport2.getResult());
    Assert.assertEquals(2, batchImport2.getResult().length);
  }
}
