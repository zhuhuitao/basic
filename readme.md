### 常用工具

#### 1，View防止重复点击

```kotlin
    //默认500ms
binding.root.clickNoRepeat {

}
```

```kotlin
//可自定义时间
binding.root.clickNoRepeat(interval = 500, action = {

        })
```

#### 2,监听网络变化
实现： NetworkManager.NetworkHandler接口

初始化：
```kotlin
     //初始化
        val networkHandler = NetworkManager(this,this )
        val isOnline = networkHandler.isOnline
        //开启监听
        networkHandler.start()
        //移除，防止内存泄漏
        networkHandler.stop()
```

#### 3,Bundle 运算符重载
```kotlin
      val bundle = Bundle()
        bundle += "key1" to "value1"
        bundle += "key2" to "value2"
```

#### 4,Intent运算符重载
```kotlin
   val intent = Intent()
        intent += "key1" to "value1"
        intent += "key2" to "value2"
```