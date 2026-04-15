package HospitalManagmentSystem;

public class OrthopedicsService {

    public void registerPatient(String patientName){
        TokenManager manager = TokenManager.getInstance();
        int token = manager.genrateNextToken();

        System.out.println("Patient Orthipedic: " + patientName + "Token : " + token);
    }
}
