###说明 
本工具是从  https://github.com/astarring/mybatis-generator-gui/releases 修改而来 感兴趣的可以参考原项目
时间匆促为自己项目使用不足很多可以根据自己需求修改
主要提供界面操作用于Excel导出json文件
### 要求
本工具由于使用了Java 8的众多特性，所以要求JDK <strong>1.8.0.60</strong>以上版本，另外<strong>JDK 1.9</strong>暂时还不支持。

### 下载
你可以从本链接下载本工具: https://github.com/astarring/mybatis-generator-gui/releases

### 启动本软件

* 方法一: 自助构建

```bash
    git clone https://github.com/astarring/mybatis-generator-gui
    cd mybatis-generator-gui
    mvn jfx:jar
    cd target/jfx/app/
    java -jar mybatis-generator-gui.jar
```

* 方法二: IDE中运行

Eclipse or IntelliJ IDEA中启动, 找到```com.zzg.mybatis.generator.MainUI```类并运行就可以了

- 方法三：打包为本地原生应用，双击快捷方式即可启动，方便快捷

  如果不想打包后的安装包logo为Java的灰色的茶杯，需要在pom文件里将对应操作系统平台的图标注释放开

```bash
	#<icon>${project.basedir}/package/windows/mybatis-generator-gui.ico</icon>为windows
	#<icon>${project.basedir}/package/macosx/mybatis-generator-gui.icns</icon>为mac
	mvn jfx:native
```
​	另外需要注意，windows系统打包成exe的话需要安装WiXToolset3+的环境；由于打包后会把jre打入安装包，两个平台均100M左右，体积较大请自行打包；打包后的安装包在target/jfx/native目录下

Licensed under the Apache 2.0 License

Copyright 2017 by Owen Zou
