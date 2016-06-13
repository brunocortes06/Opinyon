package opinyon.com.bruno.opinyon.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bruno on 13/06/2016.
 */
public class PresidentialModel {
    public Map<String, Long> presidential = new HashMap<>();
//    public Map<String, String> voters = new HashMap<>();

    public PresidentialModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public PresidentialModel(Map<String, Long> presidential) {
        this.presidential = presidential;
//        this.voters = voters;
    }
}