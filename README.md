# metrics-aws
Codahale Metrics instrumentation over AWS Java SDKs

This is a result of HackDay which took place in Schibsted Tech Poland. Motivation behind that project was lack of libraries which instrument various AWS services. Until HackDay we had been using custom code for each place we use AWS Java SDK. This produces code duplication. Also names of metrics were not consistent throughout the services. In initial state of our project we decided to use Codahale Metrics and publish statistics to DataDog.

In order to use:
- checkout code
- build locally (install/deploy)
- add maven/gradle dependency to your project


#Sample code:

```java
public AmazonSQS sqsClient(MetricRegistry metricRegistry) {
  // Create AmazonSQS Service. Do not forget about tuning of timeouts and other connection options.
  AmazonSQS sqsClient = new AmazonSQSClient(new BasicAWSCredentials("XXXX", "XXX"));

  // Wrap SQS Client with instrumented proxy.
  return InstrumentedAwsClientFactory.instrument(sqsClient, metricRegistry, "awsmetris.test");
}
```

```java
public AmazonS3 s3Client(MetricRegistry metricRegistry) {
  // Create AmazonS3 Service client. Do not forget about tuning of timeouts and other connection options.
  AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials("XXXX", "XXXX"));

  // Wrap SQS Client with instrumented proxy.
  return InstrumentedAwsClientFactory.instrument(s3Client, metricRegistry, "awsmetrics.test");
}
```

_This kind of wrapping works also for other AWS SDK Clients (DynamoDB, Lambda, EC2 etc.)_

Execution of any method defined in interface AmazonSQS or AmazonS3 will produce following metrics:

```


```


