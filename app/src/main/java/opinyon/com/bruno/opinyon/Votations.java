package opinyon.com.bruno.opinyon;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import opinyon.com.bruno.opinyon.util.EnumOpt;
import opinyon.com.bruno.opinyon.util.VoteModelIMP;

public class Votations extends AppCompatActivity {

    private String cpf;
    private String votationOptions;
    private DatabaseReference mDatabase;
    private int count = 0;
    private boolean voteSelectd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voatations);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cpf = extras.getString("cpf");
        }

        getVotations();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(in);
        finish();
    }

    private void getVotations(){

        final LinearLayout linearlayout = new LinearLayout(this);
        linearlayout.setOrientation(LinearLayout.VERTICAL);
        linearlayout.setGravity(Gravity.CENTER);
        linearlayout.setPadding(16,16,16,16);
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project-3054629283855362897.firebaseio.com/votations");
            ChildEventListener votationListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    LinearLayout linear1 = new LinearLayout(Votations.this);
                    linear1.setOrientation(LinearLayout.VERTICAL);
                    linear1.setGravity(Gravity.CENTER);
                    Button b;
                    b = new Button(Votations.this);
                    b.setText(votationOptions);
                    b.setId(count);
                    b.setTextSize(10);
                    b.setPadding(48, 3, 48, 3);
                    b.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
                    b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    //Aqui atribuo dinamicamente os dados para o botão
                    votationOptions = dataSnapshot.getKey().toString();
                    if(votationOptions.equals(EnumOpt.impeachment.getRealName())){
                        b.setText(EnumOpt.impeachment.getShortname());
                    }else if(votationOptions.equals(EnumOpt.presidential.getRealName())) {
                        b.setText(EnumOpt.presidential.getShortname());
                    }else {
                        b.setText(votationOptions);
                    }

                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams)b.getLayoutParams();
                    ll.gravity = Gravity.CENTER;
                    b.setLayoutParams(ll);

                    linear1.addView(b);
                    linearlayout.addView(linear1);
                    count++;

                    View.OnClickListener clicks=new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            switch(v.getId())
                            {
                                case 0: System.out.println("FIRST");
                                    if(!voteSelectd) {
                                        voteSelectd = true;
                                        checkVote(EnumOpt.impeachment.getRealName());
                                    }
                                    break;

                                case 1: System.out.println("FOURTH");
                                    if(!voteSelectd) {
                                        voteSelectd = true;
                                        checkVote(EnumOpt.presidential.getRealName());
                                    }
                                    break;
                            }
                        }
                    };
                    b.setOnClickListener(clicks);
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
                    // Getting Post failed, log a message
                    Log.w("loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            ref.addChildEventListener(votationListener);
        }catch (Exception e){
            e.printStackTrace();
        }
        this.setContentView(linearlayout);
    }

    private void checkVote(final String votation) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("votations").child(votation);

        ValueEventListener votationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VoteModelIMP vm = dataSnapshot.getValue(VoteModelIMP.class);

                if (vm.voters.containsKey(cpf)) {
                    String voto = vm.voters.get(cpf);
                    createDialog(vm, voto, mDatabase);
                }else{
                    Intent i = new Intent(getApplicationContext(), Options.class);
                    i.putExtra("votationOptions", votation);
                    i.putExtra("cpf", cpf);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(votationListener);
    }

    private void createDialog(final VoteModelIMP vm, final String voto, final DatabaseReference mDatabase){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO Remover voto usando a key(cpf) para saber o voto que ele já havia feito
                if(vm.voters.get(cpf).equals(EnumOpt.nao.getRealName())){
                    vm.nao = vm.nao -1;
                }else if(vm.voters.get(cpf).equals(EnumOpt.sim.getRealName())){
                    vm.sim = vm.sim -1;
                }else if(vm.voters.get(cpf).equals(EnumOpt.bolso.getRealName())) {
                    vm.bolso = vm.bolso - 1;
                }else if(vm.voters.get(cpf).equals(EnumOpt.aecio.getRealName())) {
                    vm.aecio = vm.aecio - 1;
                }else if(vm.voters.get(cpf).equals(EnumOpt.lula.getRealName())) {
                    vm.lula = vm.lula - 1;
                }else if(vm.voters.get(cpf).equals(EnumOpt.marina.getRealName())) {
                    vm.marina = vm.marina - 1;
                }

                vm.voters.remove(cpf);

                mDatabase.setValue(vm);

                Intent i = new Intent(getApplicationContext(), Options.class);
                i.putExtra("votationOptions", votationOptions);
                i.putExtra("cpf", cpf);
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Toast.makeText(Votations.this, "Voto já computado", Toast.LENGTH_LONG).show();
            }
        });
// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setTitle("Você já votou, deseja alterar seu voto?");
        dialog.show();
    }
}
