Running with JDK 11 caused issues with Maven 1.5, instead replaced the following with:
In <properties>, replace the following:

   	<maven.compiler.source>1.5</maven.compiler.source>
    	<maven.compiler.target>1.5</maven.compiler.target> 


with:

   	<maven.compiler.source>11</maven.compiler.source>
    	<maven.compiler.target>11</maven.compiler.target>




And added the following:
in <build>, added the following:

        <plugin>
        	<groupId>org.apache.maven.plugins</groupId>
          	<artifactId>maven-compiler-plugin</artifactId>
          	<version>3.8.0</version>
          	<configuration>
             		<source>11</source>
             		<target>11</target>
          	</configuration>
	</plugin>





