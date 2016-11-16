package com.example.android.inventory.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.R;
import com.example.android.inventory.data.InventoryContract.Entry;

/**
 * Created by cheah on 25/10/16.
 */

public class InventoryCursorAdaptor extends CursorAdapter{
    Context context;
    public InventoryCursorAdaptor(Context context,Cursor c){
        super(context,c,0);
        this.context = context;
    }

    @Override
    public View newView(Context context,Cursor cursor,ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view,final Context context,Cursor cursor){
        final TextView txtCatId = (TextView)view.findViewById(R.id.txtCatId);
        final TextView txtCatPartName = (TextView)view.findViewById(R.id.txtCatPartName);
        final TextView txtCatQty = (TextView)view.findViewById(R.id.txtCatQty);
        final TextView txtCatUnitPrice = (TextView)view.findViewById(R.id.txtCatUnitPrice);
        Button btnCatSell = (Button)view.findViewById(R.id.btnCatSell);

        int indexName = cursor.getColumnIndex(Entry._ID);
        if(indexName!=-1){
            txtCatId.setText(cursor.getString(indexName));
        }
        int indexDesc = cursor.getColumnIndex(Entry.COLUMN_PART_NAME);
        if(indexDesc!=-1){
            txtCatPartName.setText(cursor.getString(indexDesc));
        }
        int indexQty = cursor.getColumnIndex(Entry.COLUMN_QUANTITY);
        if(indexQty!=-1){
            txtCatQty.setText(cursor.getString(indexQty));
        }
        int indexPrice = cursor.getColumnIndex(Entry.COLUMN_UNIT_PRICE);
        if(indexPrice!=-1){
            txtCatUnitPrice.setText(cursor.getString(indexPrice));
        }

        btnCatSell.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String strQty = txtCatQty.getText().toString().trim();
                int intQty = Integer.parseInt(strQty);
                if(intQty>0){
                    intQty--;
                    txtCatQty.setText(intQty+"");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Entry.COLUMN_PART_NAME,txtCatPartName.getText().toString().trim());
                    contentValues.put(Entry.COLUMN_QUANTITY,txtCatQty.getText().toString().trim());
                    contentValues.put(Entry.COLUMN_UNIT_PRICE,txtCatUnitPrice.getText().toString().trim());

                    String strId = txtCatId.getText().toString().trim();
                    int intId = Integer.parseInt(strId);
                    context.getContentResolver().insert(ContentUris.withAppendedId(InventoryContract.CONTENT_URI,intId),contentValues);
                }else{
                    Toast.makeText(context,context.getString(R.string.not_enough_qty),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
