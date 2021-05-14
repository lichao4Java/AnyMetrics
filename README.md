# 介绍

![image.png](./README-imgs/logo.png)

AnyMetrics - 面向开发人员、声明式的 Metrics 采集与监控系统，可以对结构化与非结构化、有界数据与无界数据进行采集，通过对采集数据进行提取、过滤、逻辑运算等处理后将结果存储流行的监控系统或存储引擎中（如 Prometheus、ES）从而搭建起完整的监控体系，同时结合 grafana 完成数据的可视化


数据的采集、提取、过滤、存储等均以配置的方式驱动，无需额外的开发，对应到 AnyMetrics 中分别是对数据源、收集规则、收集器进行配置，基于这些配置 AnyMetrics 会以管道的方式自动完成从数据采集到数据存储的全部工作

对于有界数据的任务，AnyMetrics 会以固定的频率从数据源中拉取数据，AnyMetrics 中内置了 MySQL 类型的有数据源，对于无界数据的任务，AnyMetrics 会以一个时间窗口为时间单位从数据源中批量拉取数据，AnyMetrics 中内置了 Kafka 类型的无界数据源

AnyMetrics 的数据源可以是任何系统，比如可以把 HTTP 请求结果当作数据源、也可以把 ES 的检索结果当作数据源

通过对数据源的原始数据进行提取和过滤可以完成从非结构数据变成结构化数据的目的，AnyMetrics 中内置了JSON、正则表达式和 Spring EL 表达式3种数据收集与过滤规则（Filter）

- 通过JSON Filter可以完成对JSON数据格式的提取和过滤
- 通过Regular Filter可以完成对数据的提取和过滤
- 通过Spring EL Filter可以完成对原始数据以及以上Filter的处理之后的数据进行的逻辑运算等操作

Filter 可以单独使用，也可以组合起来使用，AnyMetrics 会将所有 Filter 以 FilterChain 的方式依次执行

当对数据完成了提取和过滤后，下一步就需要将数据按照指定的方式存储到目标系统中，AnyMetrics 中内置了 Prometheus 收集器，通过定义 Metrics ，可以将数据推送到 Prometheus 的 PushGateway 中

AnyMetrics 的收集器可以将数据推送到任何系统，比如 MySQL、ES 甚至推送给一个 WebHook


不论是在收集规则配置还是收集器配置中，均可以使用变量配置，来完成动态配置的替换，变量数据来源于正则表达式 Filter

- 在Regular Filter中通过定义如 _(.*)_ 方式可以得到名为 _$1_ 的变量
- 在JSON Filter中会将 _key_ 作为变量名，如数据格式为 {'id' : 1}的一条数据，经过处理后会产生 变量名为 _id_ ,变量值为 _1_ 的变量
- 在Spring EL Filter中会对数据进行逻辑运算，不满足条件的数据将被过滤掉
- 在需要进行逻辑运算时可以使用Spring EL表达式，通过在变量前加 _#_ 号引用变量，如 _#$1_、_#ID_
- 在其它Filter或者收集器的地方可以使用 _key_ 、 _$1_ 方式引用变量

这样就满足从数据提取到数据的再次组装或者运算操作，具体什么配置可以支持变量或者 Spring EL 表达式取决于收集规则和收集器的具体实现，AnyMetrics 内置的 Spring EL Filter 中的 expression 配置以及 Prometheus 收集器中的 value 配置均支持 Spring EL 表达式以及变量(_#key_)、Prometheus 收集器中的 labels 配置支持变量(_key_)

AnyMetrics 采用插件式的设计方式，不论是数据源、收集规则还是收集器均可以实现拓展，即时是 AnyMetrics 中以及内置的插件也是采用对等的方式实现的，加载和使用什么插件完全取决与声明的配置


# 架构
![image.png](./README-imgs/image%20(5).jpg)


# 技术栈

```text
SpringBoot + Nacos + Vue + ElementUI
```

# 安装

**启动之前需要安装用到的依赖**


**安装 nacos（必选）**

https://nacos.io/zh-cn/docs/quick-start.html

**安装 prometheus（可选）**

https://github.com/prometheus/prometheus

**安装 pushgateway（可选）**

https://github.com/prometheus/pushgateway

# 启动

**启动 AnyMeitris**
```jshelllanguage
1 mvn clean package
2 cd boot/target
3 java -Dnacos.address=nacos.ip:8848 -Dnacos.config.dataId=AnyMetricsConfig -Dnacos.config.group=config.app.AnyMetrics -Dauto=true -jar AnyMetrics-boot.jar 
```

启动参数说明
```text
通过 nacos.address 参数指定nacos地址
通过 nacos.config.dataId 参数指定配置在nacos的dataId，默认值为 AnyMetricsConfig
通过 nacos.config.group 参数指定配置在nacos的group，默认值为 DEFAULT
通过 auto 参数控制任务是否自启动，默认值为 false

```
启动后访问 http://localhost:8080/index.html


# 如何配置


#### 1、选择任务类型
![image.png](./README-imgs/image%20(6).png)


#### 2.1、有界数据
![image.png](./README-imgs/image%20(7).png)
选择调度间隔，单位：秒


#### 2.2、选择数据源
![image.png](./README-imgs/image%20(8).png)
选择数据源为 mysql（目前仅支持了 mysql），并完善相关配置

#### 3.1、无界数据 
![image.png](./README-imgs/image%20(9).png)
输入时间窗口，单位：秒


#### 3.2、选择数据源
![image.png](./README-imgs/image%20(10).png)
选择 kafka 为数据源（目前仅支持了 kafka ），并完善相关配置

#### 4、收集规则
![image.png](./README-imgs/image%20(11).png)
filters 支持 regular 和 el 2种类型，在 regular 中使用括号的方式提取需要的变量，多个变量以 $1、$2 ... $N 的方式命名，在 el 中可以使用 _#$1 _变量用来做运算

#### 5、收集器
![image.png](./README-imgs/image%20(12).png)
选择 prometheus（目前仅支持了prometheus）并完善 metrics 相关配置信息，type 支持 gauge、counter、histogram 类型，labels 支持 _$1_ 变量，value 支持 Spring EL 表达式变量运算

# 运行任务


#### 1、启动任务
![image.png](./README-imgs/image%20(13).png)
点击 Start 按钮启动任务


#### 2、查看运行日志
![image.png](./README-imgs/image%20(14).png)
点击 Logs Tab 查询任务运行日志


#### 3、iframe
![image.png](./README-imgs/image%20(15).png)
点击 iframe Tab 可以把外部系统嵌入到任务中，如将 grafana 的 dashboard 链接嵌入到系统中展示


#### 4、停止任务
![image.png](./README-imgs/image%20(16).png)
点击 Stop 按钮停止任务




# 示例


## 例1：APM监控 - 采集所有的执行时间超过3秒的慢链路并配置报警策略

#### 1、设置kafka为数据源，从kafka中读取trace日志
```json
{
    "groupId":"anymetrics_apm_slow_trace",
    "kafkaAddress":"192.168.0.1:9092",
    "topic":"p_bigtracer_metric_log",
    "type":"kafka"
}
```
#### 
#### 2、设置收集规则


调用日志是结构化的数据，如：
```json
1617953102329,operation-admin-web,10.8.60.41,RESOURCE_MYSQL_LOG,com.yxy.operation.dao.IHotBroadcastEpisodesDao.getNeedOnlineList,1,1,0,0,1
```
因此第一步采用正则对数据进行提取、过滤，对应的正则为：
```json
(.*?),(.*?),(.*?),(.*?),(.*?),(.*?),(.*?),(.*?),(.*?),(.*?)
```
提取后的数据为：
```json
{
  "$4":"RESOURCE_MYSQL_LOG",
  "$5":"com.yxy.operation.dao.IHotBroadcastEpisodesDao.getNeedOnlineList",
  "$6":"1",
  "$7":"1",
  "$10":"1",
  "$8":"0",
  "$9":"0",
  "$1":"1617953102329",
  "$2":"operation-admin-web",
  "$3":"10.8.60.41"
}
```
得到了10个变量，从$1 到 $10，由于监控的是超过3秒的慢链路，因此我们只需要收集 RT 超过3秒的日志数据，所以需要定一个逻辑运算表达式 Filter，对应的 EL 表达式为：
```json
(new java.lang.Double(#$10) / #$6) > 3000
```
其中 #$10 是链路的总响应时间，#$6 是接口的总调用次数， 先通过 #$10 / #$6 运算得到平均 RT，然后通过 (#$10 / #$6) > 3000 过滤出3秒以上的慢链路数据


根据上面的2种收集规则得到完整的配置为：
```json
{
    "timeWindow": 40,
    "kind": "stream",
    "filters": [
        {
            "expression": "(.*?)\u0001,(.*?)\u0001,(.*?)\u0001,(.*?)\u0001,(.*?)\u0001,(.*?)\u0001,(.*?)\u0001,(.*?)\u0001,(.*?)\u0001,(.*?)\n",
            "type": "regular"
        },
        {
            "expression": "(new java.lang.Double(#$10) / #$6) > 3000",
            "type": "el"
        }
    ]
}
```


#### 3、设置收集器
把数据收集到 promethus 中
```json
{
    "pushGateway": "192.168.0.1:9091",
    "metrics": [
        {
            "help": "anymetrics_apm_slow_trace",
            "labelNames": [
                "application",
                "type",
                "endpoint"
            ],
            "name": "anymetrics_apm_slow_trace",
            "type": "gauge",
            "value": "new java.lang.Double(#$10) / #$6",
            "labels": [
                "$2",
                "$4",
                "$5"
            ]
        }
    ],
    "type": "prometheus",
    "job": "anymetrics_apm_slow_trace"
}
```
需要定义 promethus 的 metrics，名称是 anymetrics_apm_slow_trace，类型为 gauge，lableNames 使用 application、type、endpoint，分别对应变量 $2、$4、$5，因为收集的是响应时间RT，因此 value 为 #$10 / #$6

其中 #$10 是链路的总响应时间，#$6 是接口的总调用次数，通过 #$10 / #$6 运算得到平均 RT


#### 4、配置告警与可视化


**4.1 可视化**


打开 Grafana，创建一个 Panel，选择数据源为 promethus，图标类型为 Graph，在 Metrics 中输入 PromQL 语法 anymetrics_apm_slow_trace{}
![image.png](./README-imgs/image%20(17).png)
关于 PromQL 可以参考 [https://www.cnblogs.com/kevincaptain/p/10508628.html](https://www.cnblogs.com/kevincaptain/p/10508628.html)

**4.2 配置Grafana告警**


在 Panel 中选择 Alert Tab，定义告警规则，如：
![image.png](./README-imgs/image%20(18).png)


**Evaluate every 1m For 1m**
第一个 1m 表示以1分钟的频率检查是否满足报警条件
第二个 1m 表示满足 Conditions 时报警状态先从 OK 变为 Pending，此时还不会触发报警，但 Pending 状态会持续 1分钟，在 Pending 状态时，如果下次检查依再次满足条件，则触发报警


**Conditions**
设置 Max() OF query(A,5m,now) IS ABOVE 3000 表示对比过去5分钟内最高的值是否超过了3000


**NoData & Error Handing**
用来设置获取数据超时或者没有数据时需不需要触发报警


**Notifications**
配置 Send to 报警渠道，可以是钉钉，Email等


**一个比较完整的 APM 监控组合如下：**

通过对调用链日志采集不同维度的 metrics 完成链路RT、项目错误数、项目平均RT、链路RT分布 等多维度的监控

![image.png](./README-imgs/image%20(20).png)




## 例2：可视化展示注册用户总数


#### 1、设置mysql为数据源，根据sql查询用户总数
```json
{
    "password": "root",
    "jdbcurl": "jdbc:mysql://192.168.0.1:3306/user",
    "type": "mysql",
    "sql": "select count(1) from user",
    "username": "root"
}
```
#### 2、设置收集规则
根据 sql 查询出来的结果，提取出 count(1)，使用正则表达式收集规则：
```json
{
    "kind": "schedule",
    "interval": 5,
    "filters": [
        {
            "expression": "\\{\\\"count\\(1\\)\\\":(.*)\\}",
            "type": "regular"
        }
    ]
}
```
#### 3、设置收集器
把数据收集到 promethus 中
```json
{
    "pushGateway": "192.168.0.1:9091",
    "metrics": [
        {
            "help": "anymetrics_member_count",
            "name": "anymetrics_member_count",
            "type": "gauge",
            "value": "#$1"
        }
    ],
    "type": "prometheus",
    "job": "anymetrics_member_count"
}
```
需要定义 promethus 的 metrics，名称是 anymetrics_member_count，类型为 gauge，因为只需要收集用户总数，因此不需要定义lables和labelNames，value 为 #$1


#### 4、配置可视化
打开 Grafana，创建一个 Panel，选择数据源为 promethus，图标类型为 Graph，在 Metrics 中输入 PromQL 语法 anymetrics_member_count{}
![image.png](./README-imgs/image%20(19).png)


## 例3：Nginx 日志监控

#### Nginx请求延时监控、Nginx状态码监控

#### 1、设置kafka为数据源，消费nginx的access_log日志
```json
{
    "groupId":"anymetrics_nginx",
    "kafkaAddress":"192.168.0.1:9092",
    "topic":"nginx_access_log",
    "type":"kafka"
}
```

#### 2、设置收集规则
假设nginx的log_format配置如下：
```json
log_format  main  '"$http_x_forwarded_for" $remote_addr - $remote_user [$time_local] $http_host "$request" ' '$status $body_bytes_sent "$http_referer" ' '"$http_user_agent" $upstream_addr $request_method $upstream_status $upstream_response_time';
```
因此第一步采用正则Filter对数据进行提取、过滤，对应的正则为：

```json
(.*?)\\s+(.*?)\\s+-(.*?)\\s+\\[(.*?)\\]\\s+(.*?)\\s+\\\"(.*?)\\s+(.*?)\\s+(.*?)\\s+\\\"?(\\d+)\\s+(\\d+)\\s\\\"(.*?)\\\"\\s+\\\"(.*?)\\\"\\s+(.*?)\\s+(.*?)\\s+(\\d+)\\s+(.*)",
```

提取后的数据为：

```json
$1:"-"
$2:192.168.198.17
$3: -
$4:13/Apr/2021:10:48:14 +0800
$5:dev.api.com
$6:POST
$7:/yxy-api-gateway/api/json/yuandouActivity/access
$8:HTTP/1.1"
$9:200
$10:87
$11:http://192.168.0.1:8100/yxy-edu-web/coursetrialTemp?id=123
$12:Mozilla/5.0 (iPhone; CPU iPhone OS 13_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)Mobile/15E148 MicroMessenger/7.0.11(0x17000b21)NetType/WIFI Language/zh_CN
$13:10.8.43.18:8080
$14:POST
$15:200
$16:0.005
```
一共得到了16个变量，对照log_format我们可以分别知道每个变量的含义

完整收集规则配置为：
```json
{
    "timeWindow": 30,
    "kind": "stream",
    "filters": [
        {
            "expression": "(.*?)\\s+(.*?)\\s+-(.*?)\\s+\\[(.*?)\\]\\s+(.*?)\\s+\\\"(.*?)\\s+(.*?)\\s+(.*?)\\s+\\\"?(\\d+)\\s+(\\d+)\\s\\\"(.*?)\\\"\\s+\\\"(.*?)\\\"\\s+(.*?)\\s+(.*?)\\s+(\\d+)\\s+(.*)",
            "type": "regular"
        }
    ]
}
```

#### 3、设置收集器
收集每个请求响应时间以及请求的状态码，并把数据存储到 promethus 中

```json
{
    "pushGateway": "192.168.0.1:9091",
    "metrics": [
        {
            "help": "nginx_log_host_status",
            "labelNames": [
                "host",
                "status"
            ],
            "name": "nginx_log_host_status",
            "type": "gauge",
            "value": "1",
            "labels": [
                "$5",
                "$9"
            ]
        },
        {
            "help": "nginx_log_req_rt (seconds)",
            "labelNames": [
                "host",
                "endpoint"
            ],
            "name": "nginx_log_req_rt",
            "type": "gauge",
            "value": "new java.lang.Double(#$16)",
            "labels": [
                "$5",
                "$7"
            ]
        }
    ],
    "type": "prometheus",
    "job": "anymetrics_nginx_log"
}
```
#### 4、配置可视化
##### Nginx状态码监控

打开 Grafana，创建一个 Panel，选择数据源为 promethus，图标类型为 Graph，在 Metrics 中输入 PromQL 语法 nginx_log_host_status{}
![image.png](./README-imgs/image%20(21).png)

##### Nginx请求延时监控

打开 Grafana，创建一个 Panel，选择数据源为 promethus，图标类型为 Graph，在 Metrics 中输入 PromQL 语法 nginx_log_req_rt{}
![image.png](./README-imgs/image%20(22).png)


### 完整的demo配置如下

[APM-慢链路监控](./demo/APM-慢链路监控.json)

[APM-Nginx日志监控](./demo/Nginx日志监控.json)

[APM-可视化展示用户总数](./demo/可视化展示用户总数.json)


# Q&A

## 如何采集 APM 日志?
**如果系统中有调用链追踪系统，可以使用使用调用链日志，或者是通过定义拦截器对目标方法进行日志打印，定义好日志格式，可以直接按行打印，或者在内存中聚合后按固定频率打印，日志一般包含RT 延时、error/success 次数、endpoint、application 等关键数据**


## 日志如何收集到 kafka？
**可以使用 filebeat 将 nginx 的 access_log、tomcat 的应用日志收集到 kafka**


## 目前支持了哪些数据源？
**有界数据目前支持 mysql、http；无界数据目前支持 kafka**

## 目前支持了哪些收集器？
**目前仅支持 prometheus，使用 pushgateway 方式将数据推送到 prometheus 中**

## 已经使用了 Skywalking、Zipkin 这类调用链追踪系统还需要使用 AnyMetrics 吗？
**2者不冲突，调用链追踪系统是收集链路的调用关系和 APM 指标数据，AnyMetrics 不光可以使用结构化的调用链追踪系统的指标日志作为数据源来监控系统，同时也可以将应用系统中非结构化的日志作为数据源来监控系统运行情况，如监控一些 Exception 事件，也可以对数据库的表数据进行可视化展示或者监控，同时 AnyMetrics 支持跨平台系统，任何系统产生的日志都可以作为数据源用于监控系统运行情况**