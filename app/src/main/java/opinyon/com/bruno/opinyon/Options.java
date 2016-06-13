package opinyon.com.bruno.opinyon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.LinkedHashMap;
import java.util.Map;

import opinyon.com.bruno.opinyon.util.VoteModelIMP;

//TODO criar radiobuttons dinamicos
//TODO atrelar usuário ao voto
//TODO usar mutable
public class Options extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String votationOptions;
    private int count = 0;
    Map<String, Long> optMap = new LinkedHashMap<String, Long>();
    private String cpf;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            votationOptions = extras.getString("votationOptions");
            cpf = extras.getString("cpf");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("votations").child(votationOptions);
        getOptions();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getApplicationContext(),Votations.class);
        startActivity(in);
        finish();
    }

    private void getOptions() {
        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(LinearLayout.VERTICAL);

        ChildEventListener votationListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    optMap.put(dataSnapshot.getKey(), (Long) dataSnapshot.getValue());

                    RadioButton rdbtn = new RadioButton(Options.this);
                    rdbtn.setId(count);
                    rdbtn.setText(dataSnapshot.getKey());
                    rdbtn.setTag(dataSnapshot.getKey());
                    radioGroup.addView(rdbtn);
                    RadioGroup.OnCheckedChangeListener radioList = new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            int selectedId = group.getCheckedRadioButtonId();

                            // find the radiobutton by returned id
                            radioButton = (RadioButton) findViewById(selectedId);


                            optMap.put(radioButton.getTag().toString(), optMap.get(radioButton.getTag()) + 1);
                            addVotes(mDatabase, optMap, radioButton.getTag().toString());
                            onOptionClicked(mDatabase, optMap, radioButton.getTag().toString());
                        }
                    };
                    count = count + 1;
                    radioGroup.setOnCheckedChangeListener(radioList);
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
        ((ViewGroup) findViewById(R.id.radiogroup)).addView(radioGroup);
    }

    private void addVotes(final DatabaseReference postRef, final Map<String, Long> optMap, final String opt) {
        final boolean[] ret = {false};
        //TODO REMOVER VOTOS JA COMPUTADOS
        try {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    // Set value and report transaction success
                    mutableData.setValue(optMap);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void onOptionClicked(final DatabaseReference postRef, final Map<String, Long> optMap, final String opt) {
        final boolean[] ret = {false};
        try {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    VoteModelIMP p = mutableData.getValue(VoteModelIMP.class);
                    if (p == null) {
                        return Transaction.success(mutableData);
                    }

                    p.voters.put(cpf, opt);

                    // Set value and report transaction success
                    mutableData.setValue(p);
                    Intent i = new Intent(getApplicationContext(), Votations.class);
                    i.putExtra("cpf", cpf);
                    startActivity(i);
                    finish();
                    /*Intent i = new Intent(getApplicationContext(), IPChart.class);
                    i.putExtra("votationOptions", votationOptions);
                    i.putExtra("cpf", cpf);
                    startActivity(i);
                    finish();*/
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
