# metrics-aws
Codahale Metrics instrumentation over AWS Java SDKs

This is a result of HackDay which took place in Schibsted Tech Poland. Motivation behind that project was lack of libraries which instrument various AWS services. Until HackDay we had been using custom code for each place we use AWS Java SDK. This produces code duplication. Also names of metrics were not consistent throughout the services. In initial state of our project we decided to use Codahale Metrics and publish statistics to DataDog.

In order to use:
- checkout code
- build locally (install/deploy)
- add maven/gradle dependency to your project


Here is sample code:
```java
public AmazonSQS sqsClient(MetricRegistry metricRegistry) {
  // Create AmazonSQS Service. Do not forget about tuning of timeouts and other connection options.
  AmazonSQS sqsClient = new AmazonSQSClient(new BasicAWSCredentials("XXXX", "XXX"));

  // Wrap SQS Client with instrumented proxy.
  return InstrumentedAwsClientFactory.instrument(sqsClient, metricRegistry, "awsmetris.test");
}
```


