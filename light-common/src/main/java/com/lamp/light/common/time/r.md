### 时间包
#### 时间API
1. java.util.Date 体系
2. java.util.LocalDateTime 体系

#### 操作时间记录
问题： 每次按照下面的编写代码了
```java
long time = System.currentTimeMillis();
String tmp = "";
long executeTime = System.currentTimeMillis() - time;
```
希望:
使用一个对象，让操作变的更加简单。请扩展自己的思维，强化了这个对象的功能

提示：
1. 多个任务
2. 时间单位。毫秒，微秒
3. for
4. 使用的集合是什么。

#### 时间取整 与补充
问题： 在数据操作的时候，经常需要对数据进行取整
```txt
2023.09.24 18:50:12
秒取整：2023.09.24 18:50:00
分取整：2023.09.24 18:00:00

public LocalDateTime qu(LocalDateTime time , TimeUitl time){}

查询当天的数据
2023.09.24 18:50:12
2023.09.24 00:00:00
2023.09.24 23:59:59
```
希望：
"简单且方便"的方式，支持Date对象，LocalDateTime

提示：
1. LocalDateTime 请看这个对象



#### 在两个时间取值
问题：在数据操作的时候，经常需要对数据按照时间进行整理，所以需要去连个时间之间值
```text

A：2023.09.24 18:50:12  B：2023.09.24 20:50:12  
按照小时取差值是 2 ，

按照分钟取差值是 121 ，
    2023.09.24 18:50:00
    2023.09.24 18:51:00
    2023.09.24 18:52:00
    ......
    2023.09.24 20:51:00
public LocalDateTime qu(LocalDateTime time , LocalDateTime two ,TimeUitl time){}
```
希望：
简单的方式。请扩展自己的思维，强化了这个对象的功能

提示：
1. 使用简单的方式，可以使用一套API。
2. 传递指定差距值
3. 相差具体的单位值
4. 值的格式