package Lambda;

import StudentArr.Student;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String args[]){

        List<SMS> st = new ArrayList<>();

        st.add(new SMS(1, "Amit", "java", 85, 22));
        st.add(new SMS(1, "Amit", "python", 25, 22));
        st.add(new SMS(1, "Amit", "Java", 574, 22));
        st.add(new SMS(1, "Amit", "c++", 45, 22));
        st.add(new SMS(1, "Amit", "javaScript", 89, 22));

        System.out.println("All Students: ");
        st.forEach(allSt -> System.out.println(allSt));

        st.stream()
                .filter(s -> s.getCourse().equals("java"))
                .forEach(System.out::println);

        st.stream()
                .filter(s -> s.getMarks() > 80)
                .forEach(System.out::println);





    }
}
