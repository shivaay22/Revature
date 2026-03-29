package HospitalManagmentSystem;

public class PediatricsService {

    public void registerPatinet(String patientName){
        TokenManager manager = TokenManager.getInstance();
        int token = manager.genrateNextToken();

        System.out.println("Patient PediaTrics: " + patientName + "Token: " + token);
    }
}
