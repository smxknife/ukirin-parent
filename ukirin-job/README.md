# Ukirin Job Project

## 背景

在某个项目开发中，需要用到分布式任务调度引擎，在调查了现有的资源后，ElasticJob映入眼帘，但是随着更加深入的学习了解后，
总感觉ElasticJob在定制和扩展上存在一定的限制，但是不可否认ElasticJob自身非常优秀，所以有了参考ElasticJob来自己造轮子 

--- Ukirin Job

## 快速启动

**注：目前处于开发中，项目还没有发布，所以如果使用需要将项目克隆到本地打包编译安装**

maven引入：
```
<dependency>
    <groupId>org.ukirin.job</groupId>
    <artifactId>ukirin-job-ram</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.ukirin.job</groupId>
    <artifactId>ukirin-job-Infrastructure-quartz</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

java代码：

```
public class MyJob implements Job {

	@Override
	public void execute(ShardingContext shardingContext, LocalDateTime fireTime, LocalDateTime triggerTime, Map<String, Object> parameters) {

		System.out.printf("===================================\r\n" +
				"    Thread: %s\r\n" +
				"    ExecutionContext: %s\r\n" +
				"    ShardingContext: %s\r\n" +
				"    fireTime: %s\r\n" +
				"    triggerTime: %s\r\n" +
				"    parameters: %s\r\n",
				Thread.currentThread().getName(),
				shardingContext.getExecutionContext(), shardingContext, fireTime, triggerTime, parameters);
	}
}

public class Main {
	public static void main(String[] args) throws InterruptedException {
		RamJobRegistry registry = RamJobRegistry.getRegistry();
		JobDefinition jobDefinition = new JobDefinition("myJob", MyJob.class.getName(),
				"*/5 * * * * ?", 3, false);

		new RamJobScheduler("ramDemo", registry).init().start();
		new RamJobScheduler("ramDemo2", registry).init().start();
		registry.registerJob(jobDefinition);
		
		TimeUnit.SECONDS.sleep(10);

		registry.trigger(new JobTrigger("myJob", LocalDateTime.now().minusYears(1)));
	}
}
```

上面的代码即为一个完整的demo，可以本地测试

**Job：**

实现Job接口，在execute方法里面实现相关业务逻辑
参数说明：
> * shardingContext: 分片上下文，里面包含了任务分片执行的信息
> * fireTime: 任务被触发的时间
> * triggerTime: JobTrigger指定的业务时间（如果triggerTime不为null，该任务只会触发一次）
> * parameters: 触发器携带的临时参数

**RamJobRegistry：**

JobRegistry的内存实现，是注册中心

**JobDefinition：**

JobDefinition是Job的定义，用于注册到JobRegistry

**RamJobScheduler：**

JobScheduler的内存实现，是调度器

**JobTrigger：**

JobTrigger触发器，用于触发任务执行，所有的任务都是通过JobTrigger来进行触发

## 项目架构

### 架构层次

![系统架构图](/img/ukirin-job.svg)

#### Trigger层（触发器）

负责触发任务的执行。所有的任务执行都是通过JobTrigger来进行触发

#### Job层（执行作业）

Job层分为Job接口和JobDefinition

**Job接口**

上面已经说过，这是真正的业务实现接口

**JobDefinition**

Job的元数据定义，用于向注册中心注册Job

` registry.registerJob(jobDefinition) `

#### Registry层（注册中心）

**JobRegistry接口**

注册中心接口的抽象，可以分为不同的实现方式，如RamJobRegistry

可以注册JobDefinition、Instance（JobScheduler）,也可以触发JobTrigger

#### Scheduler层（调度器）

**JobScheduler接口**

JobScheduler核心接口，用来监听Registry中JobDefinition的注册和JobTrigger的触发。

JobScheduler在构造的时候必须将Registry作为参数传入，然后分别调用（可链式调用）init()和start()两个方法。
在init方法中负责向Registry中注册Watcher（监听器），用来监听Registry中JobDefinition和JobTrigger的变化。

**Instance接口**

JobScheduler也是一个Instance(实例), 可以理解为集群中的一个节点。
当有多个JobScheduler同时注册到同一个Registry时，这时候需要有一个Leader的Instance。
只有成为Leader之后才可以监听Trigger，因为Leader负责分片，将JobTrigger指定的任务在哪些Instance（JobScheduler）中执行。

**InstanceSelectedStrategy、MinimumLoadInstanceSelectedStrategy**

Scheduler与Instance有关，这里就引入了Instance的相关概念，比如Instance的负载情况，以及在进行分片时应该选择哪些Instance来执行，
这里提供了简单的InstanceStatus（负载统计，目前比较简单，后面会抽象到子类实现）、InstanceSelectedStrategy(实例选择策略，可自定义策略实现)。
默认提供了基于InstanceStatus的最小负载选择策略的实现MinimumLoadInstanceSelectedStrategy

#### Sharding层（分片）

**JobShardingStrategy、AverageJobShardingStrategy、ShardingContext**

真正触发开始分片的是在Scheduler中进行的，该层提供了分片策略接口（JobShardingStrategy）和分片上下文（ShardingContext）。
默认的分片策略是平均分配（AverageJobShardingStrategy），也可以自定义分配策略，在JobScheduler中指定即可

#### Controller层（控制器）

**JobController接口**

控制器的核心接口。目前提供了init、start、JobListener列表、TriggerListener列表、trigger，以及Job的一些相关方法（还待完善...）

JobController完全受控于JobScheduler，强关联，生命周期随着JobScheduler一起，在JobScheduler的init方法调用JobController的init方法，
在JobScheduler的start方法调用JobController的start方法。

init负责控制器的初始化，start负责控制器的启动，另外提供了模板方法doInit和doStart，便于子类扩展。

trigger方法是用来接收JobTrigger来进行具体任务触发执行的方法。到底如何执行，交由底层doTrigger的具体实现

**JobListener、LogJobListener**

Job执行的监听器，分为执行前触发和执行后触发，两个监听方法。默认提供了LogJobListener监听器，方便Job日志输出。
监听器采用SPI的方式动态加载，在JobController的init方法中负责将所有的JobListener加载到JobListener接口列表中。
但是，具体什么时候会触发是交由底层设施层触发。

**TriggerListener、LogTriggerListener**

Trigger触发的监听器，分为开始触发、触发完成以及触发失败三个方法。默认提供了LogTriggerListener监听器，方便Job日志输出。
监听器采用SPI的方式动态加载，在JobController的init方法中负责将所有的TriggerListener加载到TriggerListener接口列表中。
但是，同样的，具体何时触发由底层设施层触发。

**Infrastructure接口**

Infrastructure，即为上面说的设施层的接口。
该接口只有一个方法：负责提供JobController对象，所以，并没有单独分离到独立的层次。
同样采用SPI技术，加载所有的底层设施（JobController的具体实现），如果有多个实现，且没有动态指定时，默认采用第一个

#### Executor层（执行器）

**JobExecutor接口**

JobExecutor负责Job的代理执行，可以在job执行时做一些其他操作，如失败转移、错过重试等各种额外的操作提供方便。
默认的SimpleJobExecutor是最简单的实现，没有任何额外的操作。

JobExecutor被JobController调度，同样的可以在构建JobController时自定义JobExecutor

-----

至此，整个架构层次已经看完，大概的流程如下：

**启动流程：**
```
1. 构建JobRegistry
2. 根据JobRegistry构建JobScheduler
3. 构建JobController
4. 构建JobExecutor
5. JobScheduler初始化
6. 注册JobDefinition和JobTrigger监听
7. JobController初始化
8. JobScheduler启动
9. JobController启动
```

**任务注册流程：**
```
1. 构建JobDefinition
2. 使用JobRegistry注册JobDefinition
3. JobScheduler（Leader）监听到JobDefinition变化，开始进行分片
4. JobRegistry根据分片结果生成JobTrigger，下发给各个Instance（即JobScheduler）
5. JobScheduler接收到JobTrigger后，交由JobController执行
6. JobController将JobTrigger，交给底层设施层进行真正的任务触发
```

**任务触发流程：**
```
1. 构建JobTrigger
2. 由JobRegistry进行触发，如果注册中心中没有该任务注册，则不进行任何操作
3. 如果存在已注册的任务，则从 "任务注册流程" 的第4步开始执行
```

### 代码模块

```
|-> ukirin-job
|---> ukirin-job-core
|---> ukirin-job-infrastructure-quartz
|---> ukirin-job-ram
|---> ukirin-job-zookeepr (开发中，代码还没有放出来)
```

#### ukirin-job-core

该模块定义了整个ukirin-job的api，也是规范。
定义了上面架构层次，以及相互配合，配合完成任务调度。

提供了各种接口方便扩展。

#### ukirin-job-infrastructure-quartz

这就是上面控制器中提到了设施层的具体实现，这里采用quartz作为底层真正的实现。在这里控制任务以什么方式执行、什么时候进行监听器触发等等。

如果不想使用quartz，还可以自定义实现，完成JobController的相关实现即可。通过这种方式可以实现规范与实现解耦，可以随意替换底层具体的实现方式。

#### ukirin-job-ram

如果说ukirin-job-infrastructure-quartz是底层实现，那么该模块相当于上层实现，即注册中心和调度器的具体实现。

这里采用RAM以内存的方式进行简单实现，可以方便开发和测试

#### ukirin-job-zookeeper

虽然这部分代码没开放出来，但是大体的功能如ukirin-job-ram类似，是基于zookeeper作为注册中心的实现，更适合生产环境。

期待后续......

----
介绍完整个项目后，可以发现通过ukirin-job-core模块，可以对注册中心以及底层设施进行自由组合，灵活方便。


