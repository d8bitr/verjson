Verjson is a specialized Java/JSON serialization-library that allows evolvable data-structures on already serialized object-graphs by using transformation and validation steps between versions.

![Verjson](https://github.com/galan/verjson/raw/master/resources/verjson.png)

= Problem Verjson is solving: 
When you have to send an object-graph from one process to another, you have to serialize the data into an exchange-format that can be read by the receiver. There are plenty of possiblities in the Java space to perform such a task, eg. native Java serialization, XML (XStream, ..) , JSON (Gson, Jackson, ..), SOAP, and more (Avro, Proto, ..). These solutions work great if the object-graph does not change. Some libraries offer version-support such as adding fields - but not removing, TODO.
But what if complex real-world changes happen, and you want to keep your object-model code clean? If you want to remove fields, rename fields, create a Collection from a previously comma-seperated String, change a field to a custom type, or other changes?
Of course you can shutdown your system and deploy the next version of your software/models. But what about the in-flight messages that still are in queues or stored in the old format - and can therefor not be processed (might throw exceptions when trying)?

= Solution
With Verjson each serialized object-graph includes its version-number along other meta-data. Verjson utilizes Jackson to generate JSON as intermediate-format. When the structure of the object-graph changes, a simple `Transformation` class has to be implemented that performs the steps to transform from one version to another. The transformation is applied directly on the serialized JSON, which has some benefits such as performance, memory-footprint, avoid code-redundancy, etc.. To ensure correctness the transformed output can be validated against an optional JSON-Schema.

Let's take a quick look on a short example to illustrate the process (source-code).

We have a simple bean that should be serialized:
```java
public class ExampleBean {
	String text;
	Long number;
}
```

First we create a Verjson instance that contains the configuration and manages the serialization. In this case there is no configuration, since this is the first version which requires no transformations (validation is optional, see other examples):
```java
Verjson<ExampleBean> verjson = Verjson.create(ExampleBean.class, null);
```

Now we can serialize/deserialize ExampleBean objects:
```java
ExampleBean bean = new ExampleBean();
bean.text = "Hello";
bean.number = 42L;

String serializedBean = verjson.write(bean);
ExampleBean deserializedBean = verjson.read(serializedBean);
```

The requirements change and we to have a list instead of a single String field. We don't want to loose data and have the content of the "text" field as first element. Furthermore the name of the "number" field should be changed to "counter". We change the ExampleBean (there is no requirement to keep the old version copied to somewhere), it looks now like this:
```java
public class ExampleBean {
	List<String> texts;
	Long counter;
}
```

In order to transform old data we have to write a `Transformation` class (`Transformations` contains methods that should be static imported):
```java
public class Transformation1 extends Transformation {
	@Override
	protected void transform(JsonNode node) {
		obj(node).put("texts", createArray(remove(obj(node), "text")));
		rename(obj(node), "number", "counter");
	}
}
```

Then we create the configuration for Verjson by sublcassing `Versions`. This configuration class will grow over time, new versions/transformations, validation will be added. Custom serializer/deserializer and polymorph types will be registered here too.
```java
public class ExampleBeanVersions extends Versions {
	@Override
	public void configure() {
		add(1L, new Transformation1());
	}
}
```

The modified example that could read serialized object from version 1 and 2 looks like this:
```java
Verjson<ExampleBean> verjson = Verjson.create(ExampleBean.class, new ExampleBeanVersions());

ExampleBean bean = new ExampleBean();
bean.texts = Lists.newArrayList("Hello");
bean.counter = 42L;

String serializedBean = verjson.write(bean);
ExampleBean deserializedBean = verjson.read(serializedBean);
```

= Possible use-cases:
* messaging (eg. in-flight messages)
* long-term storage
* producer/consumer scenarios
* data-processing in general
* ... (tell me how you use it)

= Features
* Serializes/Deserializes into JSON (Jackson based)
* Custom transformation between versions
* Type-safe
* Support for polymorph types
* Support for custom-type Serializer/Deserializer
* Each version can be validated using JSON-Schema
* Versions can be omitted
* One line per serialized object (appendable)
* Thread-safe

