package ThreadDemo;

public class Test {
    public static void main(String args[]){

        World w1 = new World();
        w1.start();
        for(int i=0;i<10000;i++){
            System.out.println("Hello");
        }
    }
}
