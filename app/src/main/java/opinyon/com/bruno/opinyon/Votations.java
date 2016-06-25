package opinyon.com.bruno.opinyon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
    private AdView adView;
    private LinearLayout linearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voatations);

        getAds();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cpf = extras.getString("cpf");
        }

        getVotations();

        showAds();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(in);
        finish();
    }

    private void getVotations(){
        linearlayout = new LinearLayout(this);
        //linearlayout.addView(adView);
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
                    linear1.setGravity(Gravity.CENTER_HORIZONTAL);
                    Button b;
                    b = new Button(Votations.this);
                    b.setTextColor( getResources().getColor(R.color.colorTextButtons));
                    b.setText(votationOptions);
                    b.setTextSize(TypedValue.COMPLEX_UNIT_SP,20F);

                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        b.setBackgroundDrawable( getResources().getDrawable(R.drawable.buttonshape) );
                    } else {
                        b.setBackground( getResources().getDrawable(R.drawable.buttonshape));
                    }
                    b.setId(count);
                    b.setPadding(48, 3, 48, 3);
//                    b.setTypeface(Typeface.SERIF,Typeface.BOLD_ITALIC);
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
                    ll.setMargins(0, 30, 0, 0);
                    ll.gravity = Gravity.CENTER;
                    b.setTransformationMethod(null);
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
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("votations").child(votation);

            ValueEventListener votationListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    VoteModelIMP vm = dataSnapshot.getValue(VoteModelIMP.class);

                    if (vm.voters.containsKey(cpf)) {
                        String voto = vm.voters.get(cpf);
                        createDialog(vm, voto, mDatabase);
                    } else {
                        Intent i = new Intent(getApplicationContext(), Options.class);
                        i.putExtra("votationOptions", votation);
                        i.putExtra("cpf", cpf);
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabase.addValueEventListener(votationListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createDialog(final VoteModelIMP vm, final String voto, final DatabaseReference mDatabase) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO Remover voto usando a key(cpf) para saber o voto que ele já havia feito
                    if (vm.voters.get(cpf).equals(EnumOpt.nao.getRealName())) {
                        vm.nao = vm.nao - 1;
                    } else if (vm.voters.get(cpf).equals(EnumOpt.sim.getRealName())) {
                        vm.sim = vm.sim - 1;
                    } else if (vm.voters.get(cpf).equals(EnumOpt.bolso.getRealName())) {
                        vm.bolso = vm.bolso - 1;
                    } else if (vm.voters.get(cpf).equals(EnumOpt.aecio.getRealName())) {
                        vm.aecio = vm.aecio - 1;
                    } else if (vm.voters.get(cpf).equals(EnumOpt.lula.getRealName())) {
                        vm.lula = vm.lula - 1;
                    } else if (vm.voters.get(cpf).equals(EnumOpt.marina.getRealName())) {
                        vm.marina = vm.marina - 1;
                    } else if (vm.voters.get(cpf).equals(EnumOpt.moro.getRealName())) {
                        vm.moro = vm.moro - 1;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showAds() {
        LinearLayout linear2 = new LinearLayout(Votations.this);
        linear2.setOrientation(LinearLayout.VERTICAL);
        linear2.addView(adView);
        linearlayout.addView(linear2);
    }

    private void getAds() {
        //        AdView mAdView = (AdView) findViewById(R.id.adView);
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-9461541042807906/6778824808");
        //adView.setAdUnitId("ca-app-pub-9461541042807906~4104560001");
        adView.setAdSize(AdSize.BANNER);

        AdRequest adRequest = new AdRequest.Builder()
              //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
//        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
