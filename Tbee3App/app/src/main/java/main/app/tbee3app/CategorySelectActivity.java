package main.app.tbee3app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CategorySelectActivity extends Activity {
    ArrayList<ArrayList<String>> category_names_atr, category_ids_atr, category_ids_type;
    ArrayList<ArrayList<ArrayList<String>>> category_type_values;
    ArrayList<String> category_names, category_ids,sub_catcount;
    CatlistAdapter2 catlistAdapter;
    GridView gridView;
    TextView extra;
    int main_cat_pos = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.select_category_layout);
        category_names = new ArrayList<String>();
        category_ids = new ArrayList<String>();
        sub_catcount = new ArrayList<>();
        category_names_atr = new ArrayList<>();
        category_ids_atr = new ArrayList<>();
        category_ids_type = new ArrayList<>();
        category_type_values = new ArrayList<>();
        catlistAdapter = new CatlistAdapter2(this,category_names,category_ids);
        gridView = (GridView)findViewById(R.id.cat_select_view);
        gridView.setAdapter(catlistAdapter);

        get_categories("0");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(main_cat_pos==-1)
                    main_cat_pos=i;

                if(sub_catcount.get(i).equals("0")) {
                    Intent intent = getIntent();
                    intent.putExtra("cat_id", category_ids.get(i));
                    intent.putExtra("cat_name", category_names.get(i));
                    intent.putExtra("cat_atr_id", category_ids_atr.get(i));
                    intent.putExtra("cat_atr_names", category_names_atr.get(i));
                    intent.putExtra("cat_atr_types", category_ids_type.get(i));
                    intent.putExtra("cat_atr_values", category_type_values.get(i));
                    setResult(7, intent);
                    finish();
                }
                else{
                    get_categories(category_ids.get(i));
                }
            }
        });
    }

    protected void get_categories(final String cat_ids) {

        String url;
        url = Settings.SERVER_URL + "category-json.php?parent_id="+cat_ids;
        Log.e("url--->", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("response is: ", jsonObject.toString());
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("categories");
                    category_names.clear();
                    category_ids.clear();
                    sub_catcount.clear();
                    category_names_atr.clear();
                    category_ids_atr.clear();
                    category_ids_type.clear();
                    category_type_values.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cat = jsonArray.getJSONObject(i);
                        String cat_name = cat.getString(getResources().getString(R.string.zcat_title));
                        String cat_id = cat.getString("id");
                        String sub_cat_count = cat.getString("substr_cnt");
                        JSONArray attributes = cat.getJSONArray("attributes");
                        Log.e("cat_name", cat_name + "--" + cat_id + attributes.toString());
                        ArrayList<String> temp_names = new ArrayList<>();
                        ArrayList<String> temp_ids = new ArrayList<>();
                        ArrayList<String> temp_types = new ArrayList<>();
                        ArrayList<ArrayList<String>> temp_values = new ArrayList<>();
                        for (int j = 0; j < attributes.length(); j++) {
                            String atr_name = attributes.getJSONObject(j).getString(getResources().getString(R.string.zcat_title));
                            String atr_id = attributes.getJSONObject(j).getString("id");
                            String atr_type = attributes.getJSONObject(j).getString("type");
                            ArrayList<String> atr_values = new ArrayList<>();
                            JSONArray values_array = attributes.getJSONObject(j).getJSONArray("values");
                            for (int k = 0; k < values_array.length(); k++)
                            {
                                atr_values.add(k,values_array.getString(k));
                            }

                            temp_names.add(atr_name);
                            temp_ids.add(atr_id);
                            temp_types.add(atr_type);
                            temp_values.add(atr_values);
                            Log.e("atr_name", atr_name + "==" + atr_id);
                        }
                        category_names.add(cat_name);
                        category_ids.add(cat_id);
                        sub_catcount.add(sub_cat_count);
                            category_names_atr.add(temp_names);
                            category_ids_atr.add(temp_ids);
                            category_ids_type.add(temp_types);
                            category_type_values.add(temp_values);

                            Log.e("added ","to Array");

                        catlistAdapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }

}
