/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract;
import com.example.android.inventory.data.InventoryContract.Entry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ImageView imageParts;
    private Spinner spinnerPartName;
    private EditText editQuantity,editUnitPrice;
    private TextView txtTotal;
    private Uri currentUri;
    private static final int URL_LOADER = 0;
    private boolean hasEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        imageParts = (ImageView) findViewById(R.id.imageParts);
        spinnerPartName = (Spinner) findViewById(R.id.spinnerPartName);
        editQuantity = (EditText) findViewById(R.id.editQuantity);
        editUnitPrice = (EditText) findViewById(R.id.editUnitPrice);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        Button btnOrder = (Button)findViewById(R.id.buttonOrder);

        setupSpinner(spinnerPartName);

        editQuantity.addTextChangedListener(mEditListener);
        editUnitPrice.addTextChangedListener(mEditListener);

        spinnerPartName.setOnTouchListener(mTouchListener);
        editQuantity.setOnTouchListener(mTouchListener);
        editUnitPrice.setOnTouchListener(mTouchListener);

        btnOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                orderParts();
            }
        });

        Intent i = getIntent();
        currentUri = i.getData();
        if(currentUri!=null){
            this.setTitle(R.string.edit);
        }else{
            this.setTitle(R.string.add);
            this.invalidateOptionsMenu();
        }
        getSupportLoaderManager().initLoader(URL_LOADER,null,this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id,Bundle args){
        if(currentUri!=null){
            return new CursorLoader(
                    this,   // Parent activity context
                    currentUri,        // Table to query
                    null,     // Projection to return
                    null,            // No selection clause
                    null,            // No selection arguments
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,Cursor data){
        if(data.moveToFirst()){
            int nameColumnIndex = data.getColumnIndex(Entry.COLUMN_PART_NAME);
            int qtyColumnIndex = data.getColumnIndex(Entry.COLUMN_QUANTITY);
            int priceColumnIndex = data.getColumnIndex(Entry.COLUMN_UNIT_PRICE);

            if(nameColumnIndex!=-1){
                spinnerPartName.setSelection(convertSpinner(data.getString(nameColumnIndex)));
            }
            if(qtyColumnIndex!=-1){
                editQuantity.setText(data.getString(qtyColumnIndex));
            }
            if(priceColumnIndex!=-1){
                editUnitPrice.setText(data.getString(priceColumnIndex));
            }
            showTotal();
            setImage(spinnerPartName.getSelectedItemPosition());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){

    }
    private void setupSpinner(Spinner spinner) {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_parts_name, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        spinner.setAdapter(genderSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                setUnitPrice(position);
                setImage(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });
    }
    private int convertSpinner(String str) {
        if(str.equalsIgnoreCase(getString(R.string.engine_oil))){
            return 0;
        }else if(str.equalsIgnoreCase(getString(R.string.oil_filter))){
            return 1;
        }else if(str.equalsIgnoreCase(getString(R.string.head_light))){
            return 2;
        }else if(str.equalsIgnoreCase(getString(R.string.brake_pad))){
            return 3;
        }else if(str.equalsIgnoreCase(getString(R.string.absorber))){
            return 4;
        }else{
            return 5;
        }
    }

    @Override
    public void onBackPressed(){
        if(!hasEdited){
            super.onBackPressed();
            return;
        }
        showUnsavedChangesDialog();
    }
    private TextWatcher mEditListener = new TextWatcher(){
        @Override
        public void beforeTextChanged(CharSequence s,int start,int count,int after){

        }

        @Override
        public void onTextChanged(CharSequence s,int start,int before,int count){

        }

        @Override
        public void afterTextChanged(Editable s){
            showTotal();
        }
    };
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            hasEdited = true;
            return false;
        }
    };
    private void setImage(int position){
        switch(position){
            case 0:
                imageParts.setImageResource(R.drawable.engine_oil);
                break;
            case 1:
                imageParts.setImageResource(R.drawable.oil_filter);
                break;
            case 2:
                imageParts.setImageResource(R.drawable.head_light);
                break;
            case 3:
                imageParts.setImageResource(R.drawable.brake_pad);
                break;
            case 4:
                imageParts.setImageResource(R.drawable.absorber);
                break;
            default:
                imageParts.setImageResource(R.drawable.tire);
        }
    }
    private void setUnitPrice(int position){
        switch(position){
            case 0:
                editUnitPrice.setText("99.99");
                break;
            case 1:
                editUnitPrice.setText("19.99");
                break;
            case 2:
                editUnitPrice.setText("399.00");
                break;
            case 3:
                editUnitPrice.setText("65.00");
                break;
            case 4:
                editUnitPrice.setText("599.00");
                break;
            default:
                editUnitPrice.setText("290.00");
        }
    }
    private void orderParts(){
        try{
            if(!saveSuccessful()){
                return;
            }
            String msg =
                    getString(R.string.part_name)+""+spinnerPartName.getSelectedItem().toString()+"\n"+
                            getString(R.string.part_name)+""+editQuantity.getText().toString().trim()+"\n"+
                            getString(R.string.part_name)+""+editUnitPrice.getText().toString().trim()+"\n"+
                            getString(R.string.part_name)+""+txtTotal.getText().toString().trim();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name)+" "+getString(R.string.order));
            intent.putExtra(Intent.EXTRA_TEXT, msg);

            startActivity(intent);
        }catch(Exception e){
            Toast.makeText(this,getString(R.string.failed_to_order)+": "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private boolean sanityCheck(){
        String qty = editQuantity.getText().toString().trim();
        if(TextUtils.isEmpty(qty)){
            Toast.makeText(this,getString(R.string.qty_empty),Toast.LENGTH_LONG).show();
            return false;
        }
        String price = editUnitPrice.getText().toString().trim();
        if(TextUtils.isEmpty(price)){
            Toast.makeText(this,getString(R.string.price_empty),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private void showTotal(){
        try{
            String strQty = editQuantity.getText().toString().trim();
            String strPrice = editUnitPrice.getText().toString().trim();

            if(TextUtils.isEmpty(strQty) || TextUtils.isEmpty(strPrice)){
                txtTotal.setText("0.00");
                return;
            }

            Integer qty = Integer.parseInt(strQty);
            Float price = Float.parseFloat(strPrice);

            if(qty<1){
                qty = 1;
                editQuantity.setText(qty+"");
                Toast.makeText(this,getString(R.string.min_qty)+": "+qty,Toast.LENGTH_LONG).show();
            }else if(qty>99){
                qty = 99;
                editQuantity.setText(qty+"");
                Toast.makeText(this,getString(R.string.max_qty)+": "+qty,Toast.LENGTH_LONG).show();
            }


            float total = qty*price;
            txtTotal.setText(String.format("%.2f", total));
        }catch(NumberFormatException e){
            e.printStackTrace();
        }
    }
    private void showUnsavedChangesDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_save_exit);
        builder.setPositiveButton(R.string.discard,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                if (dialog != null) {
                    dialog.dismiss();
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_alert);
        builder.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                if (dialog != null) {
                    dialog.dismiss();
                }
                delete();
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private boolean saveSuccessful() {
        if(!sanityCheck()){
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Entry.COLUMN_PART_NAME,spinnerPartName.getSelectedItem().toString());
        contentValues.put(Entry.COLUMN_QUANTITY,editQuantity.getText().toString().trim());
        contentValues.put(Entry.COLUMN_UNIT_PRICE,editUnitPrice.getText().toString().trim());
        if(currentUri==null){
            this.getContentResolver().insert(InventoryContract.CONTENT_URI,contentValues);
            return true;
        }
        this.getContentResolver().insert(currentUri,contentValues);
        return true;
    }
    private void delete() {
        if(currentUri==null){
           return;
        }
        this.getContentResolver().delete(currentUri,null,null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if(saveSuccessful()){
                    finish();
                    return true;
                }
                break;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                deleteDialog();
                break;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if(hasEdited){
                    showUnsavedChangesDialog();
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}