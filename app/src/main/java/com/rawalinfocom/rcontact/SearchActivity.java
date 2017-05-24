package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.adapters.AllContactAdapter;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    @BindView(R.id.text_search_count)
    TextView textSearchCount;
    @BindView(R.id.recycle_view_pb_contact)
    RecyclerView recycleViewPbContact;
    @BindView(R.id.text_pb_header)
    TextView textPbHeader;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    ArrayList<Object> objectArrayListContact;
    RContactApplication rContactApplication;
    AllContactAdapter allContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        init();
        onClickEvents();
        displayData();
    }

    private void init() {
        textPbHeader.setTypeface(Utils.typefaceSemiBold(this));
        rlTitle.setVisibility(View.GONE);
        objectArrayListContact = new ArrayList<>();
        rContactApplication = (RContactApplication) getApplicationContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleViewPbContact.setLayoutManager(linearLayoutManager);
        if(rContactApplication.getArrayListAllPhoneBookContacts() != null)
            objectArrayListContact.addAll(rContactApplication.getArrayListAllPhoneBookContacts());
    }

    private void onClickEvents(){
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search.getText().toString().length()>0){
                    search.clearFocus();
                    search.setText("");
                }else{
                    finish();
                    if (!isTaskRoot()) {
                        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                    }
                }
            }
        });
    }

    private void displayData(){
        if(objectArrayListContact!=null && objectArrayListContact.size()>0){
            allContactAdapter =  new AllContactAdapter(SearchActivity.this,objectArrayListContact);
        }


        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if(arg0.length()>3){
                    Pattern numberPat = Pattern.compile("\\d+");
                    Matcher matcher1 = numberPat.matcher(arg0);
                    if (matcher1.find()) {
                        String text =  arg0.toString();
                        allContactAdapter.filter(text);
                        rlTitle.setVisibility(View.VISIBLE);
                        recycleViewPbContact.setAdapter(allContactAdapter);

                    }else{
                        String text = arg0.toString().toLowerCase(Locale.getDefault());
                        allContactAdapter.filter(text);
                        rlTitle.setVisibility(View.VISIBLE);
                        recycleViewPbContact.setAdapter(allContactAdapter);
                    }
                    if(allContactAdapter != null){
                        int count =  allContactAdapter.getSearchCount();
                        if(count>0){
                            textSearchCount.setText(count+"");
                        }
                    }

                }

                if(arg0.length() == 0){
                    recycleViewPbContact.setAdapter(null);
                    rlTitle.setVisibility(View.GONE);
                    textSearchCount.setText("");
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
    }
}
