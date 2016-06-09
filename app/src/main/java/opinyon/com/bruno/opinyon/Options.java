package opinyon.com.bruno.opinyon;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import opinyon.com.bruno.opinyon.util.EnumOpt;
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

//        vtOpt1 = (RadioButton) findViewById(R.id.rd1);
//        vtOpt2 = (RadioButton) findViewById(R.id.rd2);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("votations").child(votationOptions);
        getOptions();
//        addRadioButtons(3);
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
                    int votOpt = -1;
                    String candi = null;

                    optMap.put(dataSnapshot.getKey(), (Long) dataSnapshot.getValue());

                    RadioButton rdbtn = new RadioButton(Options.this);

                    if(dataSnapshot.getKey() == EnumOpt.nao.getRealName()){
                        votOpt = EnumOpt.nao.getCode();
                    }else if(dataSnapshot.getKey() == EnumOpt.sim.getRealName()){
                        votOpt = EnumOpt.sim.getCode();
                    }else if(dataSnapshot.getKey() == EnumOpt.lula.getRealName()){
                        votOpt = EnumOpt.lula.getCode();
                    }else if(dataSnapshot.getKey() == EnumOpt.aecio.getRealName()){
                        votOpt = EnumOpt.aecio.getCode();
                        candi = dataSnapshot.getKey();
                    }else if(dataSnapshot.getKey() == EnumOpt.bolso.getRealName()){
                        votOpt = EnumOpt.bolso.getCode();
                    }else if(dataSnapshot.getKey() == EnumOpt.marina.getRealName()){
                        votOpt = EnumOpt.marina.getCode();
                    }

                    rdbtn.setId(count);
                    rdbtn.setText(dataSnapshot.getKey());
                    radioGroup.addView(rdbtn);
                    final int votOpt1 = votOpt;
                    final String candidato = candi;
                    RadioGroup.OnCheckedChangeListener radioList = new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            optMap.put(optMap.keySet().toArray()[votOpt1].toString(), optMap.get(candidato) + 1);
                            onOptionClicked(mDatabase, optMap, votOpt1);
                            // Check which radio button was clicked
//                            switch(checkedId) {
//                                case R.id.rd1:
//                                    if (checked) {
//                                        optMap.put(optMap.keySet().toArray()[0].toString(), optMap.get(optMap.keySet().toArray()[0]) + 1);
//                                        onOptionClicked(mDatabase, optMap, 0);
//                                    }
//                                    break;
//                                case R.id.rd2:
//                                    if (checked) {
//                                        optMap.put(optMap.keySet().toArray()[1].toString(), optMap.get(optMap.keySet().toArray()[1]) + 1);
//                                        onOptionClicked(mDatabase, optMap, 1);
//                                    }
//                                    break;
//                            }
                        }
                    };


//                    opt[count] = dataSnapshot.getKey();
//                    optMap.put(dataSnapshot.getKey(), (Long) dataSnapshot.getValue());
//
//                    if (opt[0] !=null) {
//                        if(opt[0].equals(EnumOpt.nao.getRealName())){
//                            vtOpt1.setText(EnumOpt.nao.getShortname());
//                        }else {
//                            vtOpt1.setText(opt[0]);
//                        }
//                    }
//                    if (opt[1] !=null) {
//                        if(opt[1].equals(EnumOpt.sim.getRealName())){
//                            vtOpt2.setText(EnumOpt.sim.getShortname());
//                        }else {
//                            vtOpt2.setText(opt[1]);
//                        }
//                    }
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.rd1:
//                if (checked) {
//                    optMap.put(optMap.keySet().toArray()[0].toString(), optMap.get(optMap.keySet().toArray()[0]) + 1);
//                    onOptionClicked(mDatabase, optMap, 0);
////                        mDatabase.setValue(optMap);
//                }
//                break;
//            case R.id.rd2:
//                if (checked) {
//                    optMap.put(optMap.keySet().toArray()[1].toString(), optMap.get(optMap.keySet().toArray()[1]) + 1);
//                    onOptionClicked(mDatabase, optMap, 1);
////                    mDatabase.setValue(optMap);
//                }
//                break;
//        }
    }

    private void onOptionClicked(DatabaseReference postRef, final Map<String, Long> optMap, final int keyNumber) {
        final boolean[] ret = {false};
        try {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    VoteModelIMP p = mutableData.getValue(VoteModelIMP.class);
                    if (p == null) {
                        return Transaction.success(mutableData);
                    }

                    p.voters.put(cpf, optMap.keySet().toArray()[keyNumber].toString());
                    if(optMap.get(optMap.keySet().toArray()[0]) != null)
                        p.nao = (optMap.get(optMap.keySet().toArray()[0]));
                    if(optMap.get(optMap.keySet().toArray()[1]) != null)
                        p.sim = (optMap.get(optMap.keySet().toArray()[1]));

                    p.lula = (optMap.get(optMap.keySet().toArray()[2]));
                    p.aecio = (optMap.get(optMap.keySet().toArray()[3]));
                    p.bolso = (optMap.get(optMap.keySet().toArray()[4]));
                    p.marina = (optMap.get(optMap.keySet().toArray()[5]));

                    // Set value and report transaction success
                    mutableData.setValue(p);
                    Intent i = new Intent(getApplicationContext(), IPChart.class);
                    i.putExtra("votationOptions", votationOptions);
                    i.putExtra("cpf", cpf);
                    startActivity(i);
                    finish();
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

    public void addRadioButtons(int number) {

        for (int row = 0; row < 1; row++) {
            RadioGroup ll = new RadioGroup(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            for (int i = 1; i <= number; i++) {
                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setId((row * 2) + i);
                rdbtn.setText("Radio " + rdbtn.getId());
                ll.addView(rdbtn);
            }
            ((ViewGroup) findViewById(R.id.radiogroup)).addView(ll);
        }

    }
}
