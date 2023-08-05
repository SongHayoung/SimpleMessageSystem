## Simple Message System
>This is a system designed for message delivery management and scheduled transmission in a distributed environment. The system focuses on processing a large volume of messages accurately at specific times while preventing message duplication and maintaining stability and availability.
>
>Internally, the system implements a distributed lock using unique IDs to prevent message duplication and utilizes a high-performance timing wheel for efficient scheduling, ensuring precise and efficient message delivery.
>
>Furthermore, the worker application incorporates a task redistribution mechanism managed by an external monitoring application to enhance reliability. The worker application records heartbeat signals periodically, and the monitoring application uses this data to detect abnormally terminated applications and redistribute tasks to other applications.
>
>The primary objective of this project is to enhance the accuracy and reliability of message processing while improving system stability and performance. It aims to create an optimized system that can handle the complexity of distributed systems while providing high availability and efficiency.

![](https://cdn.jsdelivr.net/gh/SongHayoung/image@master/uPic/Untitled-2023-08-05-17281.png)

### Table of Contents
- [Section](#sections)
  - [How to De-dubplicate message](#how-to-de-dubplicate-message)
  - [Implementing high performance scheduler](#implementing-high-performance-scheduler)
  - [Detecting and redistributing tasks from unstable instances](#detecting-and-redistributing-tasks-from-unstable-instances)

## Sections
### How to De-dubplicate message

The mechanism to prevent duplicate messages in various scenarios, such as Offset commit failure in a message queue, is controlled through a distributed lock. A unique ID is generated in conjunction with Redis and Lua scripting to implement this distributed lock.

When a message is successfully delivered at its scheduled time, the distributed lock switches the message's state to "delivered" and performs necessary resource cleanup.

If a message fails to be delivered successfully, a retry mechanism is triggered. The message's state is changed to "retry," and after a certain period of time, a retry attempt is made. This retry mechanism is designed to prevent the thundering herd problem, where multiple messages attempt retries simultaneously and overload the system. To achieve this, the retry attempts are distributed with some time offset and additional jitter values.

In summary, the distributed lock, implemented using Redis and Lua scripting, effectively manages message delivery and prevents duplicates in various situations. It also incorporates a retry mechanism to handle failed deliveries while mitigating the thundering herd problem through time offset and jitter. Relevant resources are properly cleaned up during the process.
### Implementing high performance scheduler

The mechanism to process a large number of messages accurately at specific times relies on the implementation of batch processing and a timing wheel. Batch processing is used to allocate messages that have reached their execution threshold to worker applications. These worker applications, equipped with a unique timing wheel buffer, handle message delivery at relatively precise times.

During batch processing, messages are distributed to worker applications based on their execution threshold. Each worker application operates with its own timing wheel, which serves as a buffer for handling message deliveries at specific times. The timing wheel operates as an internal concurrent data structure, preventing concurrency issues.

Additionally, to support graceful shutdown, the application ensures that any scheduled data within the timing wheel is redistributed back to the message queue when the application is terminated. This mechanism allows for a smooth and orderly shutdown process, preventing the loss of pending tasks.

In summary, the combination of batch processing and the timing wheel mechanism ensures accurate and timely message delivery, efficient handling of large volumes of messages, and supports graceful shutdown in the event of application termination.

![](https://cdn.jsdelivr.net/gh/SongHayoung/image@master/uPic/image-20230805180950263.png)
### Detecting and redistributing tasks from unstable instances

The worker application is equipped with a task redistribution mechanism managed by an external monitoring application to handle termination scenarios like SIGKILL. This mechanism operates based on heartbeat signals. The worker application periodically records heartbeat signals, and the monitoring process uses the data from these heartbeats to detect any abnormally terminated applications.

When an application is detected as abnormally terminated, the monitoring service identifies the tasks that were being processed by that application. Subsequently, it redistributes these tasks to other healthy applications for further processing.

The heartbeat-based monitoring ensures that the system is resilient to unexpected terminations and provides a failover mechanism to redistribute tasks, maintaining uninterrupted message processing. By leveraging this approach, the distributed system can uphold high availability and reliability, even in the face of application failures.

![](https://cdn.jsdelivr.net/gh/SongHayoung/image@master/uPic/%E1%84%8C%E1%85%A6%E1%84%86%E1%85%A9%E1%86%A8%20%E1%84%8B%E1%85%A5%E1%86%B9%E1%84%8B%E1%85%B3%E1%86%B7-2023-08-05-1805.png)
