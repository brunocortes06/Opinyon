package opinyon.com.bruno.opinyon.util;


/**
 * Created by Bruno on 14/06/2016.
 */
public class MlModel {

    public Long sim;
    public Long nao;
    public Long aecio;
    public Long bolso;
    public Long lula;
    public Long marina;

    public MlModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public MlModel(Long sim, Long nao, Long aecio, Long bolso, Long lula, Long marina) {
        this.sim = sim;
        this.nao = nao;
        this.aecio = aecio;
        this.bolso = bolso;
        this.lula = lula;
        this.marina = marina;
    }
}
