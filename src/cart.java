import java.io.IOException;

public class cart {
    public static String printBytes(byte[] s, int count) {
        String str = "";
        for (int i = 0; i < count - 1; i++)
            str += (char) s[i];

        return str;
    }//end: String printBytes(bytes[],int)

    public static void main(String[] args) {
        if (args.length == 1) {
            Tree tree = new Tree(args[0]);
            tree.classifier(null);

            System.out.print("\n--------------------------------------\n");
            System.out.print("Classification Rules");
            System.out.println("\n--------------------------------------\n");

            tree.printRules();
            System.out.print("\n*** TREE ARE READY ***\n");

            byte[] s = new byte[50];
            int count = 0;
            while (true) {
                System.out.print("Do you want to ask the tree? (Y=Yes) otherwise (EXIT) ");
                try {
                    count = System.in.read(s);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                if (printBytes(s, count).matches("Y") || printBytes(s, count).matches("y"))
                    tree.ask(null);
                else
                    break;
            }

            System.exit(0);

        } else {
            System.out.println("cart [dataset_file]");
        }
    }//end: main()

}//end: class cart
