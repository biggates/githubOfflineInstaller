# GitHub Windows离线安装包制作

### 下载制作安装包
直接启动 `Fetcher`,然后在命令行中输入需要制作安装包的路径即可。 

`Fetcher` 会将最新版本进行下载。如果只想获取所有相关文件的下载路径，然后使用其他方式，如下载工具下载，则可以修改`Fetcher`的工作模式为`SHOW_URL`：
```
private Mode mode = Mode.SHOW_URL;
```
在`SHOW_URL`模式下，只会打印出需要下载文件的链接。注意：文件会分两个目录。除了启动入口：`GitHub.application`文件在 github 安装根目录下，其他所有文件应该在：`github安装目录/Application Files/GitHub_{版本号(逗号换成了下划线)}`

### 实际操作中的问题
由于图省事，很多 HTTP 的参数都直接硬编码了。可能有影响的内容有：

```java
RequestConfig config = RequestConfig.custom()
    .setConnectTimeout(120 * 1000)
    .setSocketTimeout(10 * 60 * 1000)
    .setDecompressionEnabled(true).build();
builder.setMaxConnPerRoute(20);
builder.setMaxConnTotal(50);
builder.setDefaultRequestConfig(config);
```

版本 `3.3.1.0` 大概有 70 个文件。根据实际情况可以调整这些参数以获得比较好的效果。

另外有几个文件比较大，会遇到被服务器端断开的情况。在这个情况下还是把原始 URL 拷出来放迅雷下载比较方便。

### 真麻烦，我要直接下载
如果需要直接下载`v3.0.9.0`版本
* 可以直接到release中下载: https://github.com/gavincook/githubOfflineInstaller/releases/tag/3.0.9.0
* 如果下载速度慢，可以使用百度云盘的下载地址：http://pan.baidu.com/s/1eRqx0nK

如果需要直接下载`v3.1.1.4`版本
* 可以直接到release中下载: https://github.com/gavincook/githubOfflineInstaller/releases/tag/3.1.1.4
* 如果下载速度慢，可以使用百度云盘的下载地址：http://pan.baidu.com/s/1pLeT3YF

### 下载以后
直接运行下载回来的 `GitHub.application` 就可以安装。由于不是从原始地址安装，因此可能需要卸载掉之前已经安装好的版本。

### 未来的版本
由于已经采用了异步的 HTTP 请求，并且那几个大文件真的是没有办法直接下载回来。如果没有大问题的话应该不会更新新版本。
如果由于某些情况做更新，可能会在以下几个方面做改善：

- 代码的可读性，虽然现在就只有一个文件是比较方便，但是分拆成多个 class 会更直观一些对吧
- 命令行参数
- 计数的一些小问题
- 单独把无法下载的文件的 URL 列出来会不会好一点？
- 断点续传功能
