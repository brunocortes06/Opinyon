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
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

import opinyon.com.bruno.opinyon.util.EnumOpt;
import opinyon.com.bruno.opinyon.util.MlModel;
import opinyon.com.bruno.opinyon.util.VoteModelIMP;

public class Options extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseML;
    private String votationOptions;
    private int count = 0;
    Map<String, Long> optMap = new LinkedHashMap<String, Long>();
    Map<String, String> nameMap = new LinkedHashMap<String, String>();
    private String cpf;
    private RadioButton radioButton;
    private MlModel ml;

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

        getML();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getApplicationContext(),Votations.class);
        startActivity(in);
        finish();
    }

    private void getOptions() {
        try {
            final RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setOrientation(LinearLayout.VERTICAL);

            ChildEventListener votationListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        optMap.put(dataSnapshot.getKey(), (Long) dataSnapshot.getValue());

                        RadioButton rdbtn = new RadioButton(Options.this);
                        rdbtn.setId(count);
                        rdbtn.setText(ml.ml.get(dataSnapshot.getKey()));
//                        if (dataSnapshot.getKey().equals(EnumOpt.sim.getRealName())) {
//                            rdbtn.setText(EnumOpt.sim.getShortname());
//                        } else if (dataSnapshot.getKey().equals(EnumOpt.nao.getRealName())) {
//                            rdbtn.setText(EnumOpt.nao.getShortname());
//                        } else if (dataSnapshot.getKey().equals(EnumOpt.aecio.getRealName())) {
//                            rdbtn.setText(EnumOpt.aecio.getShortname());
//                        } else if (dataSnapshot.getKey().equals(EnumOpt.lula.getRealName())) {
//                            rdbtn.setText(EnumOpt.lula.getShortname());
//                        } else if (dataSnapshot.getKey().equals(EnumOpt.marina.getRealName())) {
//                            rdbtn.setText(EnumOpt.marina.getShortname());
//                        } else if (dataSnapshot.getKey().equals(EnumOpt.bolso.getRealName())) {
//                            rdbtn.setText(EnumOpt.bolso.getShortname());
//                        }else if (dataSnapshot.getKey().equals(EnumOpt.moro.getRealName())) {
//                            rdbtn.setText(EnumOpt.moro.getShortname());
//                        }
                        RadioGroup.LayoutParams params
                                = new RadioGroup.LayoutParams(Options.this, null);
                        params.setMargins(10, 80, 10, 0);
                        rdbtn.setLayoutParams(params);
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
                            }
                        };
                        count = count + 1;
                        radioGroup.setOnCheckedChangeListener(radioList);
                    } catch (Exception e) {
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addVotes(final DatabaseReference postRef, final Map<String, Long> optMap, final String opt) {
        //TODO REMOVER VOTOS JA COMPUTADOS
        try {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    try {
                        VoteModelIMP p = mutableData.getValue(VoteModelIMP.class);
                        if (p == null) {
                            return Transaction.success(mutableData);
                        }
                        //TODO tentar subistituir isso por um MAP
//                        for (int i = 0; i < optMap.size(); i++) {
                            p.nao = optMap.get(EnumOpt.nao.getRealName());
                            p.sim = optMap.get(EnumOpt.sim.getRealName());
                            p.aecio = optMap.get(EnumOpt.aecio.getRealName());
                            p.lula = optMap.get(EnumOpt.lula.getRealName());
                            p.marina = optMap.get(EnumOpt.marina.getRealName());
                            p.bolso = optMap.get(EnumOpt.bolso.getRealName());
                            p.moro = optMap.get(EnumOpt.moro.getRealName());
//                            p.presidential.put(optMap.keySet().toArray()[i].toString(), optMap.get(optMap.keySet().toArray()[i]));
//                        }

                        p.voters.put(cpf, opt);
                        // Set value and report transaction success
                        mutableData.setValue(p);

                        Intent i = new Intent(getApplicationContext(), IPChart.class);
                        i.putExtra("cpf", cpf);
                        i.putExtra("votationOptions", votationOptions);
                        startActivity(i);
                        finish();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
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

    private void getML() {
        try {
            mDatabaseML = FirebaseDatabase.getInstance().getReference();

            ValueEventListener votationListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ml = dataSnapshot.getValue(MlModel.class);

                    if (ml.ml.size() > 0) {
                        getOptions();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabaseML.addValueEventListener(votationListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
