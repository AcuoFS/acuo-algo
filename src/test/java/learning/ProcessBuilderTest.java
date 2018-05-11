package learning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessBuilderTest {

    public static void main(String[] args) {

        //Map<String, String> environmentVariables = new HashMap<>();
        //environmentVariables.put("PATH","/home/hicham/.sdkman/candidates/maven/3.3.9/bin:/home/hicham/.sdkman/candidates/java/current/bin:/home/hicham/bin:/home/hicham/.zplug/bin:/bin:/home/hicham/bin:/home/hicham/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/usr/games:/usr/local/games:/snap/bin");
        //environmentVariables.put("CLASSPATH","file:/home/hicham/projects/acuo/acuo-algo/target/classes/:file:/home/hicham/.m2/repository/org/renjin/renjin-maven-plugin/0.9.2643/renjin-maven-plugin-0.9.2643.jar:file:/home/hicham/.m2/repository/org/renjin/renjin-core/0.9.2643/renjin-core-0.9.2643.jar:file:/home/hicham/.m2/repository/org/renjin/renjin-appl/0.9.2643/renjin-appl-0.9.2643.jar:file:/home/hicham/.m2/repository/org/renjin/renjin-blas/0.9.2643/renjin-blas-0.9.2643.jar:file:/home/hicham/.m2/repository/org/renjin/renjin-nmath/0.9.2643/renjin-nmath-0.9.2643.jar:file:/home/hicham/.m2/repository/org/renjin/renjin-math-common/0.9.2643/renjin-math-common-0.9.2643.jar:file:/home/hicham/.m2/repository/org/renjin/renjin-lapack/0.9.2643/renjin-lapack-0.9.2643.jar:file:/home/hicham/.m2/repository/org/apache/commons/commons-math/2.2/commons-math-2.2.jar:file:/home/hicham/.m2/repository/org/renjin/gcc-runtime/0.9.2643/gcc-runtime-0.9.2643.jar:file:/home/hicham/.m2/repository/com/github/fommil/netlib/core/1.1.2/core-1.1.2.jar:file:/home/hicham");

        List<String> command = new ArrayList<>();
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
        command.add(path);
        command.add("-cp");
        command.add(classpath);
        //command.add("java");
        command.add("org.renjin.maven.test.TestExecutor");


        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command);
        //processBuilder.environment().putAll(environmentVariables);
        processBuilder.redirectErrorStream(true);

        try {
            Process start = processBuilder.start();
            System.out.println(start);
            BufferedReader br=new BufferedReader(
                    new InputStreamReader(
                            start.getInputStream()));
            String line;
            while((line=br.readLine())!=null){
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
