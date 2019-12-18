import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        int[] add = IntStream.rangeClosed(1000,1050).toArray();
        int a =0;
        new Node().nodeRun(add[a++],add[a++]);
        new Node().nodeRun(add[a++],add[a++]);
        new Node().nodeRun(add[a++],add[a++]);
        new Node().nodeRun(add[a++],add[a++]);
        new Node().nodeRun(add[a++],add[a]);


    }
}
