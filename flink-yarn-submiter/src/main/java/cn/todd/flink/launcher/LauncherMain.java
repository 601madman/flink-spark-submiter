/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package cn.todd.flink.launcher;

import cn.todd.flink.entity.JobParamsInfo;
import cn.todd.flink.executor.StandaloneExecutor;
import cn.todd.flink.executor.YarnJobClusterExecutor;
import cn.todd.flink.executor.YarnSessionClusterExecutor;
import cn.todd.flink.log.RunningLog;

import java.util.List;
import java.util.Properties;

/**
 *
 *  Flink任务本地idea提交
 *  Date: 2020/6/14
 *
 * @author todd5167
 */

public class LauncherMain {

    public static String runFlinkJob(JobParamsInfo jobParamsInfo, String execMode) throws Exception {
        String yarnApplicationId = "";
        switch (execMode) {
            case "yarnSession":
                yarnApplicationId = new YarnSessionClusterExecutor(jobParamsInfo).exec();
                break;
            case "yarnPerjob":
                yarnApplicationId = new YarnJobClusterExecutor(jobParamsInfo).exec();
                break;
            case "standalone":
                new StandaloneExecutor(jobParamsInfo).exec();
                break;
            default:
                throw new RuntimeException("Unsupported operating mode, yarnSession,yarnPer");
        }
        return yarnApplicationId;
    }

    public static void main(String[] args) throws Exception {

//        System.setProperty("java.security.krb5.conf", "/Users/maqi/tmp/hadoopconf/cdh514/krb5.conf");

        // 可执行jar包路径
        String runJarPath = "/Users/maqi/code/ClustersSubmiter/exampleJars/flink-kafka-reader/flink-kafka-reader.jar";
        // 任务参数
        String[] execArgs = new String[]{"-jobName", "flink110Submit", "--topic", "mqTest01", "--bootstrapServers", "172.16.8.107:9092"};
        // 任务名称
        String jobName = "Flink perjob submit";
        // flink 文件夹路径
        String flinkConfDir = "/Users/maqi/tmp/flink/flink-1.10.0/conf";
        // flink lib包路径
        String flinkJarPath = "/Users/maqi/tmp/flink/flink-1.10.0/lib";
        //  yarn 文件夹路径
        String yarnConfDir = "/Users/maqi/tmp/hadoopconf/195";
        //  作业依赖的外部文件，例如：udf jar , keytab
        String[] dependFile = new String[]{"/Users/maqi/tmp/flink/flink-1.10.0/README.txt"};
        // 任务提交队列
        String queue = "c";
        // flink任务执行模式
        String execMode = "yarnPerjob";
        // yarnsession appid配置
        Properties yarnSessionConfProperties = null;
        // savepoint 及并行度相关
        Properties confProperties = new Properties();
        confProperties.setProperty("parallelism", "1");

        JobParamsInfo jobParamsInfo = JobParamsInfo.builder()
                .setExecArgs(execArgs)
                .setName(jobName)
                .setRunJarPath(runJarPath)
                .setDependFile(dependFile)
                .setFlinkConfDir(flinkConfDir)
                .setYarnConfDir(yarnConfDir)
                .setConfProperties(confProperties)
                .setYarnSessionConfProperties(yarnSessionConfProperties)
                .setFlinkJarPath(flinkJarPath)
                .setQueue(queue)
                .build();

        String applicationId = runFlinkJob(jobParamsInfo, execMode);

        Thread.sleep(20000);
        List<String> logsInfo = new RunningLog().getRollingLogBaseInfo(jobParamsInfo, applicationId);
        logsInfo.forEach(System.out::println);
    }
}
