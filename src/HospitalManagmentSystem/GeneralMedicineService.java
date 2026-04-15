package HospitalManagmentSystem;

public class GeneralMedicineService {

    public void registerPatient(String patientName){
        TokenManager manager = TokenManager.getInstance();
        int token = manager.genrateNextToken();

        System.out.println("Patient Genral: " + patientName + "Token: " + token);
    }
}
