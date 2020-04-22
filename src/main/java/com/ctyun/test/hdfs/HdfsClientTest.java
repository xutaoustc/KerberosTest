package com.ctyun.test.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.*;
import java.util.Properties;


// 用-Djava.security.krb5.conf=xxx传入krb5参数文件
// -Dsun.security.krb5.debug=true  开启debug
// HADOOP_JAAS_DEBUG 开启debug
public class TestClient {
    public static void main(String[] args) throws IOException {
        String principal = args[0];
        String keytabPath = args[1];
        String hdfsConf = args[2];

        Configuration conf = new Configuration();
        Properties props = new Properties();
        props.load(TestClient.class.getResourceAsStream( "/" + hdfsConf));
        for (String key : props.stringPropertyNames()) {
            conf.set(key, props.getProperty(key));
        }
        conf.set("hadoop.security.authentication","kerberos");

        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(principal, keytabPath);

        FileSystem fs = FileSystem.get(conf);
        FileStatus[] files  = fs.listStatus(new Path("/"));
        for (int i = 0; i < files.length; i++) {
            try{
                if(files[i].isDirectory()){
                    System.out.println("D " + files[i].getPath()
                            + ", owner:" + files[i].getOwner());
                }else if(files[i].isFile()){
                    System.out.println("F " + files[i].getPath()
                            + ", owner:" + files[i].getOwner()
                            + ", length:" + files[i].getLen());
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
