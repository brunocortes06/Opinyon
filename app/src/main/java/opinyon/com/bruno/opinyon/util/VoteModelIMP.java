package opinyon.com.bruno.opinyon.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bruno on 06/06/2016.
 */
public class VoteModelIMP {

    public Long sim;
    public Long nao;
    public Map<String, String> voters = new HashMap<>();

    public VoteModelIMP() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public VoteModelIMP(Long sim, Long nao) {
        this.sim = sim;
        this.nao = nao;
    }
}
