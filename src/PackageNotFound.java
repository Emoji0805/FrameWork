package Utils;

import java.lang.Exception;

public class PackageNotFound extends Exception {
    
    public PackageNotFound(){
        super();
    }

    public PackageNotFound(String erreur){
        super(erreur);
    }
}
