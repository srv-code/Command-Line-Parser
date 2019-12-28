import java.util.Scanner;
import util.text.parser.cmdline.CommandLineParser;


public class Tester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CommandLineParser parser = new CommandLineParser();
        String input;

        System.out.println("[Press 'exit' to exit]");
        while(true) {
            System.out.print("input>  ");
            if((input=sc.nextLine()).equals("exit"))
                break;

            for(String splits[] : parser.parse(input, 0)) {
                for(String split : splits)
                    System.out.print("'"+ split +"' ");
                System.out.println();
            }
        }
    }
}