package cod;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import static jdk.nashorn.internal.objects.NativeArray.map;

public class firstFollow {

    static HashMap<String, List<String>> h = new HashMap();
    static Vector<String> terms = new Vector<String>();
    static Vector<String> non = new Vector<String>();
    static HashMap<String, List<String>> first = new HashMap();
    static HashMap<String, List<String>> follow = new HashMap();

    public static void main(String args[]) throws FileNotFoundException {

        FileInputStream in = new FileInputStream("grammar.txt");
        Scanner sc = new Scanner(in);

        String line;

        while (sc.hasNext()) {
            line = sc.nextLine();
            List<String> l = new ArrayList<String>();
            String items[] = line.split(" -> ");
            if (h.containsKey(items[0])) {
                l = h.get(items[0]);

            }
            l.add(items[1]);
            h.put(items[0], l);
        }

        Set<String> s = h.keySet();

        for (String i : s) {
            System.out.print(i + " -> ");
            terms.add(i);
            List<String> l = h.get(i);
            for (int j = 0; j < l.size(); j++) {
                System.out.print(l.get(j) + " \\ ");
            }
            System.out.println();
        }

        for (String i : s) {
            List<String> l = h.get(i);
            for (int j = 0; j < l.size(); j++) {
                String temp[] = l.get(j).split(" ");
                for (int k = 0; k < temp.length; k++) {
                    if (!terms.contains(temp[k]) && !non.contains(temp[k])) {
                        non.add(temp[k]);
                    }
                }
            }

        }

        //non terminals
        System.out.println("NON TERMINALS:");
        for (int i = 0; i < non.size(); i++) {
            System.out.println(non.get(i));
        }
        //find first
        for (int i = 0; i < terms.size(); i++) {
            firstFind(terms.get(i));
        }

        System.out.println();
        System.out.println("First");
        for (int i = 0; i < terms.size(); i++) {
            List<String> l = first.get(terms.get(i));
            System.out.print(terms.get(i) + " : ");
            for (int j = 0; j < l.size(); j++) {
                System.out.print(l.get(j) + " , ");

            }
            System.out.println();
        }

        //find follow
        // adding end to start symbol
        List<String> temp = new ArrayList<String>();
        temp.add("$");
        follow.put("E", temp);

        // till no new addition occurs in follow
        int flag = 0;
        while (true) {
            flag = 0;
            for (int i = 0; i < terms.size(); i++) {
                flag = followFind(terms.get(i));
            }
            if (flag == 0) {
                break;
            }
        }

        System.out.println();
        System.out.println("Follow");
        for (int i = 0; i < terms.size(); i++) {
            List<String> l = follow.get(terms.get(i));
            System.out.print(terms.get(i) + " : ");
            for (int j = 0; j < l.size(); j++) {
                System.out.print(l.get(j) + " , ");

            }
            System.out.println();
        }
    }

    public static void firstFind(String s) {

        List<String> l = h.get(s);
        List<String> m = null;
        if (first.containsKey(s)) {
            m = first.get(s);
        } else {
            m = new ArrayList<String>();
        }

        for (int i = 0; i < l.size(); i++) {
            String t = l.get(i);
            String temp[] = t.split(" ");
            int cnt = 0;
            if (non.contains(temp[0]) && !m.contains(temp[0]) && !m.equals("ε")) {
                m.add(temp[0]);
            } else if (temp[0].equals("ε")) {
                cnt++;
            } else if (terms.contains(temp[0])) {
                for (int j = 0; j < temp.length; j++) {
                    firstFind(temp[j]);

                    List<String> t2 = first.get(temp[j]);
                    for (int p = 0; p < t2.size(); p++) {
                        String hl = t2.get(p);
                        if (!m.contains(hl) && !hl.equals("ε")) {
                            m.add(hl);
                        }
                    }
                    if (first.get(temp[j]).contains("ε")) {
                        cnt++;
                    } else {
                        break;
                    }

                }
                if (cnt == temp.length) {
                    m.add("ε");
                }
            }
        }
        first.put(s, m);
    }

    public static int followFind(String s) {

        int flag = 0;
        List<String> m = null;
        if (follow.containsKey(s)) {
            m = follow.get(s);
        } else {
            m = new ArrayList<String>();
        }

        Set<String> key = h.keySet();
        for (String i : key) {
            List<String> l = h.get(i);
            for (int j = 0; j < l.size(); j++) {
                String temp[] = l.get(j).split(" ");

                for (int k = 0; k < temp.length; k++) {
                    if (temp[k].equals(s)) {
                        // rule A -> aBb
                        if (k < temp.length - 1) {
                            //if non terminal
                            if (non.contains(temp[k + 1])) {
                                if (!m.contains(temp[k + 1])) {
                                    m.add(temp[k + 1]);
                                    flag = 1;
                                }
                            } else { // if terminal
                                List<String> tt = first.get(temp[k + 1]);
                                for (int yy = 0; yy < tt.size(); yy++) {
                                    if (!m.contains(tt.get(yy)) && !tt.get(yy).equals("ε")) {
                                        m.add(tt.get(yy));
                                        flag = 1;
                                    }
                                }

                                // A -> aBb and first of b contains ε
                                List<String> tt3 = first.get(temp[k + 1]);
                                for (int yy = 0; yy < tt3.size(); yy++) {
                                    if (tt3.get(yy).equals("ε") && follow.containsKey(i)) {
                                        List<String> tt2 = follow.get(i);
                                        for (int yy2 = 0; yy2 < tt2.size(); yy2++) {
                                            if (!m.contains(tt2.get(yy2)) && !tt2.get(yy2).equals("ε")) {
                                                m.add(tt2.get(yy2));
                                                flag = 1;
                                            }
                                        }
                                    }
                                }
                            }

                        } else {

                            // rule A -> aB
                            if (follow.containsKey(i)) {
                                List<String> tt = follow.get(i);
                                for (int yy = 0; yy < tt.size(); yy++) {
                                    if (!m.contains(tt.get(yy)) && !tt.get(yy).equals("ε")) {
                                        m.add(tt.get(yy));
                                        flag = 1;
                                    }
                                }
                            }

                        }

                    }
                }
            }

        }
        follow.put(s, m);
        return flag;
    }
}
