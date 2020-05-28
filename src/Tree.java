import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class Tree {
    static class DataStruct {
        char type;
        String name = "";
        int average = 0;
    }//end: class DataStruct

    static class Rule {
        String value;
        String decision;
        Rule next;
    }//end: class Rule

    static class V_D {
        String v;
        String d;
        int d_count;
        V_D next;
    }//end: class V_D

    class Node {
        int att_num;
        String[] scope;
        String theDecision;
        Node next;
        Node branches = null;
        Rule r = null;
        V_D vd = null;

        Node(int att_num) {
            this.att_num = att_num;
            if (att_num == 0) scope = null;
            else scope = new String[att_num];
        }//end: Node(int)

        private void toHeap(String[] record) {
            Rule p1 = r;
            while (p1 != null) {
                if ((p1.value.matches(record[att_num])) && (p1.decision.matches(record[record.length - 1]))) {
                    System.out.println("MATCHED RULE");
                    break;
                }
                p1 = p1.next;
            }
            if (p1 == null)                    //r=null   OR    after last Rule at null
            {
                p1 = new Rule();
                System.out.println("RULE");
                p1.value = record[att_num];
                p1.decision = record[record.length - 1];
                if (r == null)
                    p1.next = null;
                else
                    p1.next = r;
                r = p1;

                V_D p2 = vd;
                while (p2 != null) {
                    if (p2.v.matches(p1.value))
                        break;
                    p2 = p2.next;
                }
                if (p2 == null)                //vd=null   OR    after last V_D at null
                {
                    p2 = new V_D();
                    p2.v = p1.value;
                    p2.d = p1.decision;
                    p2.d_count = 1;
                    if (vd == null)
                        p2.next = null;
                    else
                        p2.next = vd;
                    vd = p2;
                } else                        //the V_D founded in vd
                {
                    p2.d_count++;
                    p2.d = p1.decision;
                }
            }

        }//end: void toHeap(String[])

        private int setAverage() {
            BufferedReader dataset;
            String line;
            String[] record = new String[attributes.length];
            int sum = 0;
            int count = 0;
            int i, j;
            char c;
            boolean red_flag;
            try {
                dataset = new BufferedReader(new FileReader(inputFile));
                while ((line = dataset.readLine()) != null) {
                    for (i = 0; i < record.length; i++)
                        record[i] = "";
                    i = 0;
                    j = 0;
                    while (i < line.length()) {
                        if ((c = line.charAt(i++)) == ';')
                            j++;
                        else record[j] += c;
                    }

                    if (scope != null) {
                        red_flag = false;
                        for (i = 0; i < scope.length; i++) {
                            if (attributes[i].type == 'I')
                                record[i] = (Integer.parseInt(record[i]) >= attributes[i].average) ? "TRUE" : "FALSE";
                            if (!scope[i].matches(record[i])) {
                                red_flag = true;
                                break;
                            }
                        }
                        if (red_flag) continue;
                    }

                    sum += Integer.parseInt(record[att_num]);
                    count++;
                }//end of while
                dataset.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            return (sum / count);
        }//end: int setAverage()

        public void clear() {
            r = null;
            vd = null;
        }//end: void clear()

    }//end: class Node


    class LeafNode {
        Node pointer;
        LeafNode next;

        LeafNode(Node n) {
            pointer = n;
        }
    }//end: class LeafNode

    private final String inputFile;
    private final DataStruct[] attributes;
    private final Node root = new Node(0);
    private LeafNode leafnode = null;

    Tree(String inputFile) {
        this.inputFile = inputFile;
        int i, j;
        char c;
        String line = "";
        System.out.println("dataset file name: " + inputFile);

        try {
            BufferedReader dataset = new BufferedReader(new FileReader(inputFile));
            line = dataset.readLine();
            dataset.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        attributes = new DataStruct[(int) line.charAt(0) - 48];
        System.out.println("number of attributes: " + attributes.length);
        i = 3;
        j = 0;
        while (j != attributes.length) {
            attributes[j] = new DataStruct();
            attributes[j].type = line.charAt(i++);            //now i at ':'
            while ((c = line.charAt(++i)) != ';') {
                attributes[j].name = attributes[j].name.concat(String.valueOf(c));
            }
            System.out.println("#" + (j + 1) + "\ttype: " + ((attributes[j].type == 'S') ? "Text" : (attributes[j].type == 'I') ? "Number" : "Decision") + "\t\tname: " + attributes[j].name);
            i++;
            j++;
        }
        System.out.println();
    }//end: Tree(String)

    public void classifier(Node node) {
        if (node == null) node = root;
        if (node.att_num == attributes.length - 1) {
            System.out.println("THIS IS A LEAF NODE ...");
            return;
        }
        System.out.println(">classifier: " + node.att_num);
        BufferedReader dataset;
        String line;
        String[] record = new String[attributes.length];
        int i, j;
        char c;
        boolean red_flag;

        if (attributes[node.att_num].type == 'I')
            attributes[node.att_num].average = node.setAverage();
        System.out.println("Average= " + attributes[node.att_num].average + "\n");

        try {
            dataset = new BufferedReader(new FileReader(inputFile));
            while ((line = dataset.readLine()) != null) {
                for (i = 0; i < record.length; i++)
                    record[i] = "";
                i = 0;
                j = 0;
                while (i < line.length()) {
                    if ((c = line.charAt(i++)) == ';')
                        j++;
                    else record[j] += c;
                }

                if (node.scope != null) {
                    red_flag = false;
                    for (i = 0; i < node.scope.length; i++) {
                        if (attributes[i].type == 'I')
                            record[i] = (Integer.parseInt(record[i]) >= attributes[i].average) ? "TRUE" : "FALSE";
                        if (!node.scope[i].matches(record[i])) {
                            System.out.println("NOT IN SCOPE");
                            red_flag = true;
                            break;
                        }
                    }
                    if (red_flag) continue;
                }

                if (attributes[node.att_num].type == 'I')
                    record[node.att_num] = (Integer.parseInt(record[node.att_num]) >= attributes[node.att_num].average) ? "TRUE" : "FALSE";

                node.toHeap(record);

            }//end of while
            dataset.close();
            System.out.println("%%%%%%%% FILE %%%%%%%% CLOSED %%%%%%%%");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Rule rp = node.r;
        while (rp != null) {
            System.out.print("r.value= " + rp.value);
            System.out.println(" - r.decision= " + rp.decision);
            rp = rp.next;
        }

        V_D p = node.vd;                    //here we can use pointer p.vd itself
        while (p != null) {
            System.out.print("\nvd.v= " + p.v + " - ");
            System.out.print("vd.d= " + p.d + " - ");
            System.out.println("vd.d_count= " + p.d_count);
            Node n = new Node(node.att_num + 1);
            System.out.println("#new node: " + n.att_num);

            i = 0;
            if (node.scope != null) {
                while (i < node.scope.length) {
                    n.scope[i] = node.scope[i];
                    i++;
                }
            }
            n.scope[i] = p.v;

            if (node.branches == null)
                n.next = null;
            else
                n.next = node.branches;
            node.branches = n;

            if (p.d_count != 1) {
                classifier(n);
            } else {
                n.theDecision = p.d;
                LeafNode ln = new LeafNode(n);
                if (leafnode == null)
                    ln.next = null;
                else
                    ln.next = leafnode;
                leafnode = ln;
            }

            p = p.next;
        }
        node.clear();
    }//end: void classifier(Node)

    public void printRules() {
        int i;
        LeafNode ln = leafnode;
        while (ln != null) {
            System.out.print("(");
            for (i = 0; i < ln.pointer.scope.length; i++) {
                System.out.print(attributes[i].name);
                if (attributes[i].type == 'S') {
                    System.out.print("=" + ln.pointer.scope[i]);
                } else {
                    if (ln.pointer.scope[i].equals("TRUE"))
                        System.out.print(">=" + attributes[i].average);
                    else
                        System.out.print("<" + attributes[i].average);
                }
                if (i != ln.pointer.scope.length - 1)
                    System.out.print(", ");
            }
            System.out.println(") ==> " + attributes[attributes.length - 1].name + ": " + ln.pointer.theDecision);

            ln = ln.next;
        }

    }//end: void printRules()

    public void ask(Node node) {
        if (node == null) node = root;
        if (node.branches == null)        //a leaf node
        {
            System.out.println("THE DECISION >> " + attributes[attributes.length - 1].name + ": " + node.theDecision);
            return;
        }

        byte[] s = new byte[50];
        int count = 0;
        System.out.print("value of >> " + attributes[node.att_num].name + "? ");
        try {
            count = System.in.read(s);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        String str = cart.printBytes(s, count);
        if (attributes[node.att_num].type == 'I') {
            str = (Integer.parseInt(str) >= attributes[node.att_num].average) ? "TRUE" : "FALSE";
        }

        Node b = node.branches;
        while (b != null) {
            if (b.scope[node.att_num].matches(str)) {
                ask(b);
                return;
            } else
                b = b.next;
        }

        // now here b = null
        System.out.println("This Answer dose not supported!");

    }//end: void ask()

}//end: class Tree
