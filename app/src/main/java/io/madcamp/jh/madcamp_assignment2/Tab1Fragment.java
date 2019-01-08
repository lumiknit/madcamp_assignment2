package io.madcamp.jh.madcamp_assignment2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import com.facebook.AccessToken;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class Tab1Fragment extends Fragment {
    /* --- Constants --- */
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_PICK = 2;
    public static final int REQUEST_CODE_EDIT = 3;
    public static final int REQUEST_CODE_JSON = 4;

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 112;


    /* --- Member Variables --- */
    private int page;
    private Context context;
    private View top;
    public String[] call_or_delete = {"통화","수정","삭제"};

    private ArrayList<Contact> contacts;
    private ArrayList<ListViewAdapter.Item> shownContacts;
    private ListViewAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;




    /* --- Header --- */
    /* TabPagerAdapter에서 Fragment 생성할 때 사용하는 메소드 */
    public static Tab1Fragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        Tab1Fragment fragment = new Tab1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        top = inflater.inflate(R.layout.fragment_tab1, container, false);
        this.context = top.getContext();

        initializeFloatingActionButton();

        final ListView contact_listview = top.findViewById(R.id.contact_listview);

        loadContacts();

        adapter = new ListViewAdapter(context,R.layout.item_text2, shownContacts);
        contact_listview.setAdapter(adapter);

        updateContacts();

        contact_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int shownPosition, long id) {
                final int position = shownContacts.get(shownPosition).index;

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(contacts.get(position).name);
                builder.setItems(call_or_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch(which) {
                            case 0:
                                intent = new Intent("android.intent.action.DIAL",Uri.parse("tel:" + contacts.get(position).phoneNumber));
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(context.getApplicationContext(), EditcontactActivity.class);
                                intent.putExtra("contact_name",contacts.get(position).name);
                                intent.putExtra("contact_number",contacts.get(position).phoneNumber);
                                intent.putExtra("contact_position",position);
                                startActivityForResult(intent, REQUEST_CODE_EDIT);
                                break;
                            case 2:
                                removeContact(contacts.get(position));
                                break;
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        EditText editText = top.findViewById(R.id.searcheditText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                updateContacts();
            }
        });

        swipeRefreshLayout = top.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AccessToken token = AccessToken.getCurrentAccessToken();
                if(LoginHelper.checkRegistered(context)) {
                    Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
                    refresh();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        return top;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    /* --- Floating Action Button --- */
    // isFabOpen: contains whether FloatingActionButton is opened(true) or not(false)
    private boolean isFabOpen = false;
    public void initializeFloatingActionButton() {
        final FloatingActionButton[] fab = new FloatingActionButton[5];

        /* Find every floating action buttons */
        fab[0] = top.findViewById(R.id.fab);
        fab[1] = top.findViewById(R.id.fab1);
        fab[2] = top.findViewById(R.id.fab2);
        fab[3] = top.findViewById(R.id.fab3);
        fab[4] = top.findViewById(R.id.fab4);

        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_CONTACTS},1);
        }

        /* Initialize isFabOpen as Closed */
        isFabOpen = false;

        /* Add onClickListener for every floating action buttons */
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                Intent intent;
                switch(id) {
                    case R.id.fab: anim(); break;
                    case R.id.fab1: openAddContact(); break;
                    case R.id.fab2: openLoadFromContacts(); break;
                    case R.id.fab3: openLoadFromJSON(); break;
                    case R.id.fab4: openClearContacts(); break;
                }
            }

            /* Helper method for animation */
            private void anim() {
                if(isFabOpen) {
                    for(int i = 1; i < fab.length; i++) {
                        Animation a = AnimationUtils.loadAnimation(context, R.anim.fab_close);
                        a.setStartOffset((fab.length - i - 1) * 50);
                        fab[i].startAnimation(a);
                        fab[i].setClickable(false);
                    }
                    isFabOpen = false;
                } else {
                    for(int i = 1; i < fab.length; i++) {
                        Animation a = AnimationUtils.loadAnimation(context, R.anim.fab_open);
                        a.setStartOffset((i - 1) * 50);
                        fab[i].startAnimation(a);
                        fab[i].setClickable(true);
                    }
                    isFabOpen = true;
                }
            }
        };

        for(int i = 0; i < 5; i++) {
            fab[i].setOnClickListener(onClickListener);
        }
    }

    public void openAddContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("휴대폰에 저장된 연락처에서 가져오시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("휴대폰에서 가져오기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                        startActivityForResult(intent, REQUEST_CODE_PICK);
                    }
                })
                .setNegativeButton("새로 만들기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context.getApplicationContext(), AddcontactActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_ADD);
                    }
                });
        builder.create().show();
    }

    public void openLoadFromContacts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("휴대폰에 저장된 모든 연락처를 목록에 추가합니다. 진행하시겠습니까?");
        builder.setTitle("경고!")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearContacts();

                        String[] arrProjection = {
                                ContactsContract.Contacts._ID,
                                ContactsContract.Contacts.DISPLAY_NAME};
                        String[] arrPhoneProjection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                        Cursor clsCursor = context.getContentResolver().query(
                                ContactsContract.Contacts.CONTENT_URI, arrProjection,
                                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", null, null);

                        while(clsCursor.moveToNext()) {
                            String strContactId = clsCursor.getString(0);

                            Cursor clsPhoneCursor = context.getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrPhoneProjection,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + strContactId, null, null);
                            while(clsPhoneCursor.moveToNext()){
                                addContact(new Contact(clsCursor.getString(1),clsPhoneCursor.getString(0)));
                            }
                            clsPhoneCursor.close();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void openLoadFromJSON() {
        Intent intent = new Intent(context.getApplicationContext(), JsoncontactActivity.class);
        intent.putExtra("JSON", packIntoJSON(contacts));
        startActivityForResult(intent, REQUEST_CODE_JSON);
    }

    public void openClearContacts() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());

        builder2.setMessage("휴대폰에 저장된 모든 연락처를 삭제합니다. 진행하시겠습니까?");
        builder2.setTitle("경고!")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearContacts();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert2 = builder2.create();
        alert2.show();
    }


    /* --- ListView --- */
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case REQUEST_CODE_ADD:
                if(resultCode == Activity.RESULT_OK){
                    String contact_name = data.getStringExtra("contact_name");
                    String contact_num = data.getStringExtra("contact_num");
                    if(contact_name == null || contact_num == null) return;
                    addContact(new Contact(contact_name, contact_num));
                }
            break;
            case REQUEST_CODE_EDIT:
                if(resultCode == Activity.RESULT_OK){
                    int position = data.getIntExtra("contact_position", 0);
                    String _id = contacts.get(position)._id;
                    String contact_name = data.getStringExtra("contact_name");
                    String contact_num = data.getStringExtra("contact_num");
                    if(contact_name == null || contact_num == null) return;
                    modifyContact(data.getIntExtra("contact_position", 0), new Contact(_id, contact_name, contact_num));
                }
                break;
            case REQUEST_CODE_PICK:
                if(resultCode == Activity.RESULT_OK) {
                    String phoneNumber = null;
                    String name = null;

                    Uri contactUri = data.getData();
                    Cursor cursor = getContext()
                            .getContentResolver()
                            .query(contactUri, null, null, null, null);

                    if(cursor != null && cursor.moveToFirst()) {
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        phoneNumber = cursor.getString(numberIndex);
                        name = cursor.getString(nameIndex);
                        addContact(new Contact(name, phoneNumber));
                    }
                }
                break;
            case REQUEST_CODE_JSON:
                if(resultCode == Activity.RESULT_OK) {
                    String json = data.getStringExtra("JSON");
                    ArrayList<Contact> newList = unpackFromJSON(json);
                    clearContacts();
                    for(Contact c : newList) {
                        addContact(c);
                    }
                }
                break;
        }
    }


    private void updateContacts() {
        (new SpannableFuzzyFinder(context)).refilterContacts(
                contacts,
                shownContacts,
                ((EditText)top.findViewById(R.id.searcheditText)).getText().toString());
        Collections.sort(shownContacts);
        adapter.notifyDataSetChanged();
    }


    /* Contact List Methods */
    private void addContact(Contact contact) {
        if(!httpPutWithId(contact, false)) return;
        int i;
        for(i = contacts.size() - 1; i >= 0; i--) {
            if(contact.name.equals(contacts.get(i).name)) {
                break;
            }
        }
        if(i >= 0) {
            contacts.get(i).phoneNumber = contact.phoneNumber;
        } else {
            contacts.add(contact);
        }

        updateContacts();
    }

    private void modifyContact(int index, Contact contact) {
        if(!httpPutWithId(contact, true)) return;
        contacts.set(index, contact);
        updateContacts();
    }

    private void removeContact(Contact contact) {
        if(!httpDeleteWithId(contact.name)) return;
        contacts.remove(contact);
        updateContacts();
    }

    private void clearContacts() {
        for(int i = contacts.size() - 1; i >= 0; i--) {
            removeContact(contacts.get(i));
        }
    }

    /* --- Utility Methods --- */
    /* JSON Parser */
    private String packIntoJSON(ArrayList<Contact> arrayList) {
        JSONArray array = new JSONArray();
        for(Contact p : arrayList) {
            array.put(p.toJSONObject(false));
        }
        return array.toString();
    }

    private ArrayList<Contact> unpackFromJSON(String src) {
        return appendFromJSON(new ArrayList<Contact>(), src);
    }

    private ArrayList<Contact> appendFromJSON(ArrayList<Contact> arrayList, String src) {
        try {
            Log.d("Test@JSON_Input", src);
            JSONArray array = new JSONArray(src);
            for(int i = 0; i < array.length(); i++) {
                Contact contact = Contact.fromJSON(array.getJSONObject(i));
                if(contact == null) {
                    throw new JSONException("Malformed JSON");
                }
                arrayList.add(contact);
                Log.d("Test@aFJ", "" + contact._id);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private ArrayList<Contact> unpackFromJSON2(String src) {
        ArrayList<Contact> arrayList = null;
        try {
            arrayList = new ArrayList<Contact>();
            Log.d("Test@JSON_Input", src);
            JSONObject obj = new JSONObject(src);
            JSONArray array = obj.getJSONArray("contacts");
            for(int i = 0; i < array.length(); i++) {
                Contact contact = Contact.fromJSON(array.getJSONObject(i));
                if(contact == null) {
                    throw new JSONException("Malformed JSON");
                }
                arrayList.add(contact);
                Log.d("Test@aFJ", "" + contact._id);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    /* Network Helpers */
    private void loadContacts() {
        contacts = new ArrayList<>();
        shownContacts = new ArrayList<>();
        httpGetWithId();
    }

    public void refresh() {
        if(AccessToken.getCurrentAccessToken() == null) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        swipeRefreshLayout.setRefreshing(true);
        httpGetWithId();
    }


    private void httpError() {
        Log.d("Test@Retrofit", "Failed");
        Toast.makeText(context, "Failed to load", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }


    public interface HttpGetWithIdService {
        @GET("api/contacts/{id}")
        Call<ResponseBody> getUserRepositories(@Path("id") String _id);
    }

    private boolean httpGetWithId() {
        if(!LoginHelper.checkRegistered(getActivity())) return false;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("Test@GET", "Built");

        HttpGetWithIdService service = retrofit.create(HttpGetWithIdService.class);

        String userId = AccessToken.getCurrentAccessToken().getUserId();

        Call<ResponseBody> request = service.getUserRepositories(userId);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Test@Retrofit", "Responsed");
                try {
                    String src = response.body().string();
                    Log.d("Test@RetrofitResult", src);
                    ArrayList<Contact> newList = unpackFromJSON2(src);
                    Log.d("Test@RetrofitResult", "N = " + newList.size());
                    contacts.clear();
                    Log.d("Test@RetrofitResult", "Cleared");
                    contacts.addAll(newList);
                    Log.d("Test@RetrofitResult", "Added");
                    updateContacts();
                    Log.d("Test@RetrofitResult", "Updated");
                } catch(Exception e) { e.printStackTrace(); }
                // Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                httpError();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return true;
    }

    public interface HttpPostWithIdService {
        @POST("api/contacts/{id}")
        Call<ResponseBody> getUserRepositories(@Path("id") String _id, @Body RequestBody params);
    }

    private void httpPostWithId(final Contact contact) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("Test@POST", "Built");

        HttpPostWithIdService service = retrofit.create(HttpPostWithIdService.class);

        if(!LoginHelper.checkRegistered(context)) return;
        String userId = AccessToken.getCurrentAccessToken().getUserId();

        RequestBody params = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                contact.toJSON(false));

        Call<ResponseBody> request = service.getUserRepositories(userId, params);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Test@Retrofit", "Responsed");
                try {
                    Log.d("Test@Retrofit", response.body().string());
                } catch(Exception e) { e.printStackTrace(); }
                // Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                httpError();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public interface HttpPutWithIdService {
        @PUT("api/contacts/{id}")
        Call<ResponseBody> getUserRepositories(@Path("id") String _id, @Body RequestBody params);
    }

    private boolean httpPutWithId(final Contact contact, boolean includeId) {
        if(!LoginHelper.checkRegistered(context)) return false;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("Test@PUT", "Built");

        HttpPutWithIdService service = retrofit.create(HttpPutWithIdService.class);
        String userId = AccessToken.getCurrentAccessToken().getUserId();

        includeId = false;
        String s = contact.toJSON(includeId);

        RequestBody params = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                s);
        Log.d("Test@includeId", "" + includeId + ":");
        Log.d("Test@includeId", "" + includeId + ":" + s);

        Call<ResponseBody> request = service.getUserRepositories(userId, params);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Test@Retrofit", "Responsed");
                try {
                    String body = response.body().string();
                    Log.d("Test@Retrofit", body);
                    contact.loadFromJSON(new JSONObject(body));
                } catch(Exception e) { e.printStackTrace(); }
                // Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                httpError();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return true;
    }

    public interface HttpDeleteWithIdService {
        @DELETE("api/contacts/{id}/{name}")
        Call<ResponseBody> getUserRepositories(@Path("id") String _id, @Path("name") String name);
    }

    private boolean httpDeleteWithId(final String name) {
        if(!LoginHelper.checkRegistered(context)) return false;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("Test@DELETE", "Built");

        HttpDeleteWithIdService service = retrofit.create(HttpDeleteWithIdService.class);

        String userId = AccessToken.getCurrentAccessToken().getUserId();
        Call<ResponseBody> request = service.getUserRepositories(userId, name);
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Test@Retrofit", "Responsed");
                try {
                    int i;
                    for(i = 0; i < contacts.size(); i++) {
                        if(contacts.get(i).name.equals(name)) {
                            break;
                        }
                    }
                    if(i < contacts.size()) {
                        contacts.remove(i);
                        updateContacts();
                    }
                } catch(Exception e) { e.printStackTrace(); }
                // Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                httpError();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return true;
    }
}
