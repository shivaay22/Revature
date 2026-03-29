package HospitalManagmentSystem;

public class Main {

    public static void main(String args[]){
        System.out.println("Welcome to Bhardwaj collective Featured Hospital");

        GeneralMedicineService g1 = new GeneralMedicineService();
        CardiologyService c1 = new CardiologyService();
        PediatricsService p1 = new PediatricsService();
        OrthopedicsService o1 = new OrthopedicsService();

        g1.registerPatient("Abhi");
        g1.registerPatient("Tanmay");

        c1.registerPatient("Nisha");

        p1.registerPatinet("Divya");
        o1.registerPatient("Golu");


        TokenManager m1 = TokenManager.getInstance();

        System.out.println("nextToken: "+ m1.genrateNextToken());
        m1.showTokenStatus();

        TokenManager m2 = TokenManager.getInstance();


        System.out.println("Is singleton Same: " + (m1 == m2));



    }
}
