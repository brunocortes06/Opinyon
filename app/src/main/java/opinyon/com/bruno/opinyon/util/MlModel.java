package opinyon.com.bruno.opinyon.util;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Bruno on 14/06/2016.
 */
public class MlModel {

    public Map<String, String> ml = new LinkedHashMap<String, String>();

    public MlModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public MlModel( Map<String, String> ml) {
        this.ml = ml;
    }
}
