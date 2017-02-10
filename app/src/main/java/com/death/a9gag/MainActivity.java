package com.death.a9gag;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    static int count = 0;
    JokesAdapter adapter;
    RecyclerView realmRecyclerView;
    Realm realm;
    FloatingActionButton button;
    private ProgressDialog pDialog;
    public int gridCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(MainActivity.this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("NEW")
                .deleteRealmIfMigrationNeeded()
                .build();
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        realm = Realm.getInstance(realmConfiguration);

        button = (FloatingActionButton) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeJsonObjectRequest();
            }
        });
        loadAdapter();
    }

    public void loadAdapter() {
        RealmResults<JokesModel> AllItems = realm.where(JokesModel.class).findAll();
        if (AllItems != null) {
            adapter = new JokesAdapter(MainActivity.this, AllItems, false);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(gridCount, StaggeredGridLayoutManager.VERTICAL);
            realmRecyclerView = (RecyclerView) findViewById(R.id.loadcontainer);
            realmRecyclerView.setAdapter(adapter);
            realmRecyclerView.setLayoutManager(staggeredGridLayoutManager);
            realmRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            Log.e("LOADING", "LOADED");
        } else {
            Log.e("NULL", "NULL");
        }
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void makeJsonObjectRequest() {
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://api.icndb.com/jokes/random", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(getClass().getSimpleName(), response.toString());

                try {
                    JSONObject phone = response.getJSONObject("value");
                    Log.e("Values:", phone.toString());
                    String id = phone.getString("id");
                    String joke = phone.getString("joke");
                    Log.e(id, joke);
                    if (!checkIfExists(id)) {
                        realm.beginTransaction();
                        JokesModel model = realm.createObject(JokesModel.class);
                        model.setId(id);
                        model.setJoke(joke);
                        realm.commitTransaction();
                        Log.e("INSERTED", "INSERTED");
                        loadAdapter();
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(getClass().getSimpleName(), "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public boolean checkIfExists(String id) {

        RealmQuery<JokesModel> query = realm.where(JokesModel.class)
                .equalTo("id", id);
        return query.count() != 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        count++;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        count++;
        if (count % 2 != 0) {
            item.setIcon(R.drawable.ic_rect);
            gridCount = 2;
            loadAdapter();
        } else {
            item.setIcon(R.drawable.ic_grid);
            gridCount=1;
            loadAdapter();
        }
        return super.onOptionsItemSelected(item);
    }
}
