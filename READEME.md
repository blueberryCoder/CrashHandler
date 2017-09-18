# CrashHandler

## 用法

```java
 CrashHandler.getInstance()
                .initialize(this) // 初始化，设置Context
                .isWriteLocal(true) // 设置是否在debug模式下，将异常信息写入本地
                .setParentFile(null) // 设置异常信息保存的本地目录，设置为null表示：
                                    // "在/sdcard/Android/包名/files/error-logs"目录下
                .isDebug(true) // 设置是否为debug
                    
                .setDebugErrorHandler(new GlobalErrorHandler() {  // 设置debug下的异常全局回调
                    @Override
                    public void handlerError(String header, Throwable throwable) {
                       ...
                    }
                })
                .setReleaseErrorHandler(new GlobalErrorHander)(){
                    ...
                }
                .start();
```

```
Copyright (c) 2017. blueberry
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0
     
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```