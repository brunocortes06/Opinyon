package opinyon.com.bruno.opinyon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import opinyon.com.bruno.opinyon.util.VoteModelIMP;

//TODO criar radiobuttons dinamicos
//TODO atrelar usuário ao voto
//TODO usar mutable
public class Options extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String votationOptions;
    private RadioButton vtOpt1;
    private RadioButton vtOpt2;
    private int count = 0;
    private String[] opt = new String[20];
    Map<String, Long> optMap = new LinkedHashMap<String, Long>();
    private String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            votationOptions = extras.getString("votationOptions");
            cpf = extras.getString("cpf");
        }

        vtOpt1 = (RadioButton) findViewById(R.id.rd1);
        vtOpt2 = (RadioButton) findViewById(R.id.rd2);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("votations").child(votationOptions);
        getOptions();
    }

    private void getOptions() {
        ChildEventListener votationListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    opt[count] = dataSnapshot.getKey();
                    optMap.put(dataSnapshot.getKey(), (Long) dataSnapshot.getValue());

                    if (opt[0] !=null)
                        vtOpt1.setText(opt[0]);
                    if (opt[1] != null)
                        vtOpt2.setText(opt[1]);

                    count = count + 1;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addChildEventListener(votationListener);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rd1:
                if (checked) {
                    optMap.put(optMap.keySet().toArray()[0].toString(), optMap.get(optMap.keySet().toArray()[0]) + 1);
                    onOptionClicked(mDatabase, optMap);
//                        mDatabase.setValue(optMap);
                }
                break;
            case R.id.rd2:
                if (checked) {
                    optMap.put(optMap.keySet().toArray()[1].toString(), optMap.get(optMap.keySet().toArray()[1]) + 1);
                    onOptionClicked(mDatabase, optMap);
//                    mDatabase.setValue(optMap);
                }
                break;
        }
    }

    private void onOptionClicked(DatabaseReference postRef, final Map<String, Long> optMap) {
        final boolean[] ret = {false};
        try {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    VoteModelIMP p = mutableData.getValue(VoteModelIMP.class);
                    if (p == null) {
                        return Transaction.success(mutableData);
                    }

                    if (p.voters.containsKey(cpf)) {
                        // Unstar the post and remove self from stars
                        Toast.makeText(Options.this, "Voto já computado", Toast.LENGTH_LONG).show();
                        return Transaction.success(mutableData);
//                    p.stars.remove(getUid());
                    } else {
                        p.voters.put(cpf, true);
                        p.nao = (optMap.get(optMap.keySet().toArray()[0]));
                        p.sim = (optMap.get(optMap.keySet().toArray()[1]));
                    }

                    // Set value and report transaction success
                    mutableData.setValue(p);
//                    mDatabase.setValue(optMap);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
//                Log.d("postTransaction:onComplete:" + databaseError);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
