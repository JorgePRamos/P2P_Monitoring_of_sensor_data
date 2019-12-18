import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int[] add = IntStream.rangeClosed(1000,1050).toArray();
        int a =1;
        /*
        new Node().nodeRun(add[a++],add[a++]);
        new Node().nodeRun(add[a++],add[a++]);
        new Node().nodeRun(add[a++],add[a++]);
        new Node().nodeRun(add[a++],add[a++]);
        new Node().nodeRun(add[a++],add[a]);
*/
        //new ProcessBuilder().command("javac /Users/josemartin/Library/Mobile Documents/com~apple~CloudDocs/Ingenieria Informatica/Distributed Systems/P2P-2Homework/src/Node.java");

        Runtime.getRuntime().exec(Node.nodeRun(add[a++],add[a++]));
        Runtime.getRuntime().exec(Node.nodeRun(add[a++],add[a++]));
        Runtime.getRuntime().exec(Node.nodeRun(add[a++],add[a++]));


        /*
        Node.class.getMethod("nodeRun", int.class, int.class).invoke(new Main(), add[a++],add[a++]);
        Node.class.getMethod("nodeRun", int.class, int.class).invoke(new Main(), add[a++],add[a++]);
        Node.class.getMethod("nodeRun", int.class, int.class).invoke(new Main(), add[a++],add[a++]);
        Node.class.getMethod("nodeRun", int.class, int.class).invoke(new Main(), add[a++],add[a++]);
        Node.class.getMethod("nodeRun", int.class, int.class).invoke(new Main(), add[a++],add[a++]);
    */
    }
}
