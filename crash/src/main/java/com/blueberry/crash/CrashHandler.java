/*
 * Copyright (c) 2017. blueberry
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blueberry.crash;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by blueberry on 9/18/2017.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Context mContext;
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    private volatile static CrashHandler sINSTANCE;

    /**
     * 是否为debug
     */
    private boolean isDebug = true;

    /**
     * 是否写入本地
     */
    private boolean writeLocal = true;

    /**
     * 本地的目录
     */
    private File parentFile;

    private GlobalErrorHandler mDebugErrorHandler;
    private GlobalErrorHandler mReleaseErrorHandler;

    public static CrashHandler getInstance() {
        if (sINSTANCE == null) {
            synchronized (CrashHandler.class) {
                if (sINSTANCE == null) {
                    sINSTANCE = new CrashHandler();
                }
            }
        }
        return sINSTANCE;
    }

    public CrashHandler initialize(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context not be null");
        }
        this.mContext = context;

        return this;
    }

    public CrashHandler isDebug(boolean debug) {
        this.isDebug = debug;
        return this;
    }

    public CrashHandler setParentFile(File file) {
        this.parentFile = file;
        return this;
    }

    public CrashHandler isWriteLocal(boolean write) {
        this.writeLocal = write;
        return this;
    }

    public CrashHandler setDebugErrorHandler(GlobalErrorHandler handler) {
        this.mDebugErrorHandler = handler;
        return this;
    }

    public CrashHandler setReleaseErrorHandler(GlobalErrorHandler handler) {
        this.mReleaseErrorHandler = handler;
        return this;
    }

    public void start() {
        if (mContext == null) {
            throw new IllegalArgumentException("must set context!");
        }
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private CrashHandler() {
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        String header = getLogHeader(t);
        if (writeLocal) {
            writeLocalFile(e, header);
        }
        if (isDebug) {
            if (null != mDebugErrorHandler) {
                // 如果是debug,继续闪退
                mDebugErrorHandler.handlerError(header, e);
            } else {
                mUncaughtExceptionHandler.uncaughtException(t, e);
            }
        } else {
            if (null != mReleaseErrorHandler) {
                mReleaseErrorHandler.handlerError(header, e);
            }
        }
    }

    private void writeLocalFile(Throwable e, String header) {
        PrintWriter printWriter = null;
        String filename = mSimpleDateFormat.format(new Date()) + "-error.txt";
        File defaultParentFile = new File(mContext.getExternalFilesDir(null), "error-logs");
        if (!defaultParentFile.exists()) {
            defaultParentFile.mkdirs();
        }
        try {
            File file = new File(parentFile != null
                    ? parentFile :
                    defaultParentFile
                    , filename);
            if (!file.getParentFile().exists()) {
                file.mkdirs();
            }
            printWriter = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(
                                    file)));
            printWriter.print(header);
            e.printStackTrace(printWriter);
            printWriter.flush();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            close(printWriter);
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * log's header 。
     *
     * @param t
     * @return
     */
    private String getLogHeader(Thread t) {
        String time = mSimpleDateFormat.format(new Date());
        String device = Build.DEVICE;
        String product = Build.PRODUCT;
        String ABI = Build.CPU_ABI;
        String model = Build.MODEL;
        String androidVersion = Build.VERSION.SDK_INT + "";
        String threadName = t.getName();


        return String.format(
                "------------------------------------ \n" +
                        "time:%s\n" +
                        "device:%s \n" +
                        "product:%s \n" +
                        "ABI:%s \n" +
                        "model:%s \n" +
                        "androidVersion:%s\n" +
                        "threadName:%s \n" +
                        "------------------------------------ \n",
                time, device, product, ABI, model, androidVersion, threadName);
    }
}
