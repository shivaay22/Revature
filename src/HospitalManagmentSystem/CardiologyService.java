package HospitalManagmentSystem;

public class CardiologyService {

    public void registerPatient(String patientName){
        TokenManager manager = TokenManager.getInstance();
        int token = manager.genrateNextToken();

        System.out.println("Patient Cardio: " + patientName + "Token: " + token);
    }
}
