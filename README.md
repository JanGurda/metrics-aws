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

Execution of methods (AmazonSQS::sendMessage and AmazonS3::listBuckets) produces following metrics:

```json
{
  "awsmetrics.test.AmazonSQS.sendMessage.meanRate": 0.07343771637752684,
  "awsmetrics.test.AmazonS3.listBuckets.fiveMinuteRate": 0.1430617874373131,
  "awsmetrics.test.AmazonS3.listBuckets.oneMinuteRate": 0.10421967882953519,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.95thPercentile": 678,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.75thPercentile": 575,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.mean": 318,
  "awsmetrics.test.AmazonS3.listBuckets.meanRate": 0.0739140737580647,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.max": 775,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.median": 129,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.stdDev": 121,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.min": 115,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.mean": 107,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.75thPercentile": 67,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.95thPercentile": 286,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.max": 779,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.999thPercentile": 779,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.999thPercentile": 775,
  "awsmetrics.test.AmazonSQS.sendMessage.count": 14,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.98thPercentile": 678,
  "awsmetrics.test.AmazonSQS.sendMessage.fiveMinuteRate": 0.14080862843823982,
  "awsmetrics.test.AmazonS3.listBuckets.fifteenMinuteRate": 0.1768831100872197,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.99thPercentile": 779,
  "awsmetrics.test.AmazonS3.listBuckets.serviceError[type: AmazonS3Exception, status: 403, errorCode: InvalidAccessKeyId]": 3,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.median": 59,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.99thPercentile": 775,
  "awsmetrics.test.AmazonS3.listBuckets.count": 14,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.min": 56,
  "awsmetrics.test.AmazonSQS.sendMessage.fifteenMinuteRate": 0.1759192523035507,
  "awsmetrics.test.AmazonSQS.sendMessage.snapshot.98thPercentile": 555,
  "awsmetrics.test.AmazonSQS.sendMessage.oneMinuteRate": 0.09681263386846231,
  "awsmetrics.test.AmazonS3.listBuckets.snapshot.stdDev": 242
}
```


