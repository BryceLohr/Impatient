/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
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

package impatient;

import java.io.Console;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.GenericOptionsParser;

import cascading.flow.Flow;
import cascading.flow.FlowDef;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.operation.Identity;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;

import cascading.hcatalog.HCatTap;

public class Main
  {
  public static void main( String[] args ) throws Exception
    {
    JobConf jobConf = new JobConf();
    String[] otherArgs = new GenericOptionsParser( jobConf, args ).getRemainingArgs();

    /*
    Console c = System.console();
    if (c != null) {
      c.readLine("Pausing to check temp dir... Hit enter to continue.");
    } else {
      System.err.println("ERROR: Couldn't get console reference.");
      System.exit(1);
    }
    */

    // System.out.println("Input path: " + otherArgs[0] + " Output path: " + otherArgs[1]);
    // System.out.println("Conf tmpjars:");
    // System.out.println(conf.get("tmpjars"));
    // System.out.println("Classpath:");
    // System.out.println(System.getProperty("java.class.path"));

    ArrayList<URLClassLoader> cls = new ArrayList<URLClassLoader>();
    URLClassLoader cl1 = (URLClassLoader) Thread.currentThread().getContextClassLoader();
    ClassLoader tmp;
    do {
      cls.add(cl1);
      tmp = cl1.getParent();
      if (tmp != null && tmp instanceof URLClassLoader) {
        cl1 = (URLClassLoader) tmp;
      }
    } while (tmp != null);

    System.out.println("URLs in class loader chain (children -> parent):");

    URL[] clUrls;
    for (URLClassLoader cl2 : cls) {
      clUrls = cl2.getURLs();
      for (URL url : clUrls) {
        System.out.println(url);
      }
    }

    // Properties properties = conf.getProps();
    Properties properties = AppProps.appProps()
      .setName( "Simple cascading.hive test" )
      .setJarClass( Main.class )
      .buildProperties( jobConf );

    // AppProps.setApplicationJarClass( properties, Main.class );
    HadoopFlowConnector flowConnector = new HadoopFlowConnector( properties );

    HCatTap source = new HCatTap("sample_07");
    Hfs output = new Hfs(new TextDelimited(false, "\t"), "output/sample_07_delim", SinkMode.REPLACE);
    Each pipe = new Each("test", new Identity(new Fields("code", "description", "total_emp", "salary")));
    Flow flow = flowConnector.connect(source, output, pipe);
    flow.complete();
    }
  }
