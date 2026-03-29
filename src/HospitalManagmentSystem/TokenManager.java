package HospitalManagmentSystem;

public class TokenManager {

    private static TokenManager instance;
    private int currentTokenNumer;
    final private String hospitalName;

    private TokenManager(){
        this.currentTokenNumer = 0;
        this.hospitalName = "Bhardwaj collective-featured Hospital";
        System.out.println("Token Manager initilized for: " + hospitalName);
    }

    public static TokenManager getInstance(){
        if(instance == null){
            instance = new TokenManager();
        }
        return instance;
    }

    public int genrateNextToken(){
        return currentTokenNumer+=1;
    }

    public String getHospitalName(){
        return hospitalName;
    }

    public void showTokenStatus(){
        System.out.println("Hospital Name: " + hospitalName);
        System.out.println("Last issued Token: " + currentTokenNumer);
        System.out.println("Next Token: " + (currentTokenNumer + 1));
    }
}
