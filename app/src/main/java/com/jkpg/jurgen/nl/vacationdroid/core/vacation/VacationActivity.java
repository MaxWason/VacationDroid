package com.jkpg.jurgen.nl.vacationdroid.core.vacation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jkpg.jurgen.nl.vacationdroid.DBConnection;
import com.jkpg.jurgen.nl.vacationdroid.R;
import com.jkpg.jurgen.nl.vacationdroid.core.network.APIJsonCall;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Memory;
import com.jkpg.jurgen.nl.vacationdroid.datamodels.Vacation;

import java.util.ArrayList;
import java.util.Arrays;


public class VacationActivity extends AppCompatActivity {

    private GridView gridview=null;
    private VacationAdapter adapter;
    private Context mContext;
    private int vacID;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        Intent intent = getIntent();
        vacID = intent.getIntExtra("id",-1);
        Log.d("IDVACATION", vacID + "");


        setContentView(R.layout.vacation_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewMemory(view);
            }
        });


        gridview = (GridView) findViewById(R.id.gridview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.deleteMemory:
                selectMemoryToDelete();
                break;
            default:
                break;
        }

        return true;
    }


    protected void onStart(){
        super.onStart();
        final Activity a = this;

        //get username from preferences
        SharedPreferences sp = getSharedPreferences("vacation", MODE_PRIVATE);
        name = sp.getString("username", "error");

        DBConnection db = new DBConnection(this);
        Vacation vac = db.getVacationById(vacID);
        if( !name.equals(vac.user)){
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(FloatingActionButton.GONE);
        }

        fillFields();

        TextView vtitle = (TextView)findViewById(R.id.vacationTitle);
        TextView vplace = (TextView)findViewById(R.id.vacationPlace);
        TextView vdate = (TextView)findViewById(R.id.vacationDate);
        TextView vdesc = (TextView)findViewById(R.id.vacationDescription);

        RelativeLayout rl = (RelativeLayout)findViewById(R.id.ChangeButtons);
        rl.setVisibility(RelativeLayout.GONE);

        vtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSomethingPress(v, "title");
            }
        });
        vplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSomethingPress(v,"place");
            }
        });
        vdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSomethingPress(v,"date");
            }
        });
        vdesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSomethingPress(v,"description");
            }
        });


        adapter = new VacationAdapter(mContext, gridview, a, vacID);
        gridview.setAdapter(adapter);


        Button buttonOk= (Button) findViewById(R.id.ButtonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveChanges(v);
            }
        });

        Button buttonCancel= (Button) findViewById(R.id.ButtonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelChanges(v);
            }
        });

        fetchMemories();

    }

    private void fetchMemories(){
        final Activity a = this;
        APIJsonCall memcall = new APIJsonCall("vacations/"+vacID+"/memories", "GET", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    JsonArray arrMemories = obj.getAsJsonArray("list");
                    Log.d("MEMORYLIST", arrMemories.toString());


                    DBConnection db = new DBConnection(a);
                    ArrayList<Memory> memories = new ArrayList<>();
                    Gson gson = new Gson();
                    for(JsonElement el:arrMemories) {
                        Memory m = gson.fromJson(el, Memory.class);
                        m.vacationid = vacID;
                        db.addOrUpdateMemory(m);
                    }
                    adapter.updateView();

                } catch(Exception E) {
                    try {
                        Log.e("WEB ERROR", E.getMessage());
                    } catch (Exception e) {
                        Log.e("WEB ERROR", "No error message");
                    }
                }
            }
        };
        memcall.execute(new JsonObject());
    }

    private void onSomethingPress(View v, String s) {

        final String st=s;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(!s.equals("date")) {
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setMessage("Edit " + st)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            RelativeLayout rl = (RelativeLayout) findViewById(R.id.ChangeButtons);
                            rl.setVisibility(RelativeLayout.VISIBLE);
                            Button okButton = (Button)findViewById(R.id.ButtonOk);
                            okButton.setVisibility(Button.VISIBLE);
                            switch (st) {
                                case "description":
                                    TextView vdesc = (TextView) findViewById(R.id.vacationDescription);
                                    vdesc.setText(input.getText().toString());
                                    return;
                                case "title":
                                    TextView vtitle = (TextView) findViewById(R.id.vacationTitle);
                                    vtitle.setText(input.getText().toString());
                                    return;
                                case "place":
                                    TextView vplace = (TextView) findViewById(R.id.vacationPlace);
                                    vplace.setText(input.getText().toString());
                                    return;
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
        }else{
            LinearLayout layout = new LinearLayout(mContext);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText input = new EditText(mContext);
            input.setHint("From");
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(input);

            final EditText input2 = new EditText(mContext);
            input2.setHint("To");
            input2.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(input2);

            builder.setView(layout);
            builder.setMessage("Edit " + st)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TextView vdate = (TextView) findViewById(R.id.vacationDate);
                            vdate.setText(input.getText().toString()+" to "+input2.getText().toString());

                            RelativeLayout rl = (RelativeLayout) findViewById(R.id.ChangeButtons);
                            rl.setVisibility(RelativeLayout.VISIBLE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
        }
        // Create the AlertDialog object and return it
        builder.show();

    }

    private void SaveChanges(View v){
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.ChangeButtons);
        rl.setVisibility(RelativeLayout.GONE);

        TextView vtitle = (TextView)findViewById(R.id.vacationTitle);
        TextView vplace = (TextView)findViewById(R.id.vacationPlace);
        TextView vdate = (TextView)findViewById(R.id.vacationDate);
        TextView vdesc = (TextView)findViewById(R.id.vacationDescription);

        ArrayList<String> split = new ArrayList<String> (Arrays.asList(vdate.getText().toString().split("\\s+")));
        int from = Integer.parseInt(split.get(0).toString());
        int to = Integer.parseInt(split.get(2).toString());


        DBConnection db = new DBConnection(this);
        Vacation vac = db.getVacationById(vacID);
        Vacation modif = new Vacation(vac.id,
                vtitle.getText().toString(),
                vdesc.getText().toString(),
                vplace.getText().toString(),
                from, to,
                name
                );

        db.addOrUpdateVacation(modif);

        JsonObject modifJSon = new JsonObject();
        modifJSon.addProperty("id",modif.id);
        modifJSon.addProperty("title",modif.title);
        modifJSon.addProperty("description",modif.description);
        modifJSon.addProperty("place",modif.place);
        modifJSon.addProperty("start",modif.start);
        modifJSon.addProperty("end",modif.end);
        modifJSon.addProperty("userId", db.getUserByName(name));

        db.close();

        APIJsonCall vaccall = new APIJsonCall("vacations/" + vac.id , "PUT", this) {
            @Override
            public void JsonCallback(JsonObject obj) {
                try {
                    Log.d("MODIFIED", obj.toString());
                    Toast.makeText(getApplicationContext(), "  Changes Saved  ", Toast.LENGTH_LONG).show();
                } catch (Exception E) {
                    try {
                        Log.e("WEB ERROR", E.getMessage());
                    } catch (Exception ex){
                        Log.e("WEB ERROR", "No error message received!");
                    }
                }
            }
        };
        vaccall.execute(modifJSon);
    }

    private void CancelChanges(View v){
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.ChangeButtons);
        rl.setVisibility(RelativeLayout.GONE);
        fillFields();
        adapter.setNormalClickEvent(gridview);
    }

    private void fillFields(){
        TextView vtitle = (TextView)findViewById(R.id.vacationTitle);
        TextView vplace = (TextView)findViewById(R.id.vacationPlace);
        TextView vdate = (TextView)findViewById(R.id.vacationDate);
        TextView vdesc = (TextView)findViewById(R.id.vacationDescription);

        DBConnection db = new DBConnection(this);
        Vacation v = db.getVacationById(vacID);

        vtitle.setText(v.title);
        vplace.setText(v.place);
        vdate.setText(v.start + " to " + v.end);
        vdesc.setText(v.description);

    }

    private void createNewMemory(View v){
        //get username from preferences
        SharedPreferences sp = getSharedPreferences("vacation", MODE_PRIVATE);
        final String name = sp.getString("username", "error");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText title = new EditText(mContext);
        title.setHint("Title");
        layout.addView(title);

        final EditText desc = new EditText(mContext);
        desc.setHint("Description");
        layout.addView(desc);

        final EditText place = new EditText(mContext);
        place.setHint("Place");
        layout.addView(place);

        final EditText from = new EditText(mContext);
        from.setHint("Time");
        from.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(from);

        builder.setView(layout);
        builder.setMessage("Create a new memory")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (title.getText().toString().matches("") ||
                                desc.getText().toString().matches("") ||
                                place.getText().toString().matches("") ||
                                from.getText().toString().matches("")) {
                            Toast.makeText(getApplicationContext(), "  Fill all the fields  ", Toast.LENGTH_LONG).show();
                        } else {
                            JsonObject newMem = new JsonObject();
                            newMem.addProperty("title", title.getText().toString());
                            newMem.addProperty("description", desc.getText().toString());
                            newMem.addProperty("place", place.getText().toString());
                            newMem.addProperty("time", from.getText().toString());
                            JsonObject posdat = new JsonObject();
                            posdat.addProperty("latitude", 0);
                            posdat.addProperty("longitude", 0);
                            newMem.add("position", posdat);


                            APIJsonCall memcall = new APIJsonCall("vacations/" + vacID + "/memories", "POST", mContext) {
                                @Override
                                public void JsonCallback(JsonObject obj) {
                                    Log.d("MODIFIED", obj.toString());
                                    Toast.makeText(getApplicationContext(), "  Memory created  ", Toast.LENGTH_LONG).show();
                                    fetchMemories();
                                }
                            };
                            memcall.execute(newMem);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Create the AlertDialog object and return it
        builder.show();
    }

    public void selectMemoryToDelete(){
        adapter.setDeletionClickEvent(gridview);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.ChangeButtons);
        rl.setVisibility(RelativeLayout.VISIBLE);

        Button okButton = (Button)findViewById(R.id.ButtonOk);
        okButton.setVisibility(Button.GONE);
    }
}
