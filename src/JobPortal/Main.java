package JobPortal;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String args[]){
        String skills[] = {"Java", "SQL", "SpringBoot", "Java", "React", "Docker","AWS"};

        Set<String> st = new HashSet<>();

        Set<String> lt = new LinkedHashSet<>();

        Set<String> tst = new TreeSet<>();

        for(String skill : skills){
            st.add(skill);
            lt.add(skill);
            tst.add(skill);
        }

        System.out.println("Show all Sets");
        LMS l1 = new LMS();
        l1.displaySets(st,lt,tst);

        st.remove("SQL");
        lt.contains("Java");
        tst.size();

        lt.clear();

        System.out.println("all Hashset Data:  "+ st);
        System.out.println("all LinkedList Data: " + lt);
        System.out.println("all TreeSet Data: " + tst);

    }
}
