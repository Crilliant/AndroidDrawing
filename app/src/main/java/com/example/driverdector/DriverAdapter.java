package com.example.driverdector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DriverAdapter {  // 用于数据库增删
    DriverDbHelper helper;
    public DriverAdapter(Context context)
    {
        helper = new DriverDbHelper(context);
    }

    public void insertData(float temperature_high, float temperature_low, int blood_pressure_high, int blood_pressure_low, int o2, int heart_rate)
    {
        SQLiteDatabase db = helper.getWritableDatabase();//写tables
        db.execSQL("insert into "+helper.TABLE1_NAME + "(high,low) values (" + temperature_high + "," + temperature_low + ")");
        db.execSQL("insert into "+helper.TABLE2_NAME + "(high,low) values (" + blood_pressure_high + "," + blood_pressure_low + ")");
        db.execSQL("insert into "+helper.TABLE3_NAME + "(value) values (" + o2 + ")");
        db.execSQL("insert into "+helper.TABLE4_NAME + "(value) values (" + heart_rate + ")");
        db.close();
    }

    // 删除在某日期之前的记录(不包括deleteDate)
    // argu : tableNum为体温（1）、血压（2）、血氧(3)、心率(4);
    public void deleteBeforeDate(int tableNum, Date deleteDate) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        String date=ft.format(deleteDate);

        SQLiteDatabase db = helper.getWritableDatabase();
        switch (tableNum) {
            case 1://temperature_table
                db.execSQL("delete from " + DriverDbHelper.TABLE1_NAME + "  where logtime < ?",
                        new String[]{date});
                break;

            case 2://blood_pressure_table
                db.execSQL("delete from " + DriverDbHelper.TABLE2_NAME + "  where logtime < ?",
                        new String[]{date});
                break;

            case 3://blood_o2_table
                db.execSQL("delete from " + DriverDbHelper.TABLE3_NAME + "  where logtime < ?",
                        new String[]{date});
                break;
            case 4://heart_rate_table
                db.execSQL("delete from " + DriverDbHelper.TABLE4_NAME + "  where logtime < ?",
                        new String[]{date});
                break;
            default:
                throw new IllegalArgumentException("table num from 1 to 4");
        }
    }

    // 按周查询数据。
    // return  list（起始为周日），和List date（引用传参)
    // argu : tableNum为体温（1）、血压（2）、血氧(3)、心率(4);
    //        today为今天日期;
    //        if 查询体温、血压，isHigh=true->查询High, 0->low
    public List queryByWeek(List<String> date,
                            int tableNum, Date today, boolean isHigh){  // TODO: 添加返回list<time> date
        //获得今天周几，0-Sun, 1-Mon,...6-Sat
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(today);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        weekDay--;
        date=new ArrayList<>();

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE, -weekDay);
        Date theMonday=calendar.getTime();
        String startDate=ft.format(theMonday);

        //查询出本周的数据
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        switch (tableNum){
            case 1:// temperature_table
                if(isHigh){
                    cursor = db.rawQuery("select high, logtime from " + DriverDbHelper.TABLE1_NAME + "  where logtime >= ?",
                        new String[]{startDate});}
                else{
                    cursor = db.rawQuery("select low, logtime from " + DriverDbHelper.TABLE1_NAME + "  where logtime >= ?",
                            new String[]{startDate});
                }
                break;

            case 2:// blood_pressure_table
                if(isHigh){
                    cursor = db.rawQuery("select high, logtime from " + DriverDbHelper.TABLE2_NAME + "  where logtime >= ?",
                            new String[]{startDate});}
                else{
                    cursor = db.rawQuery("select low, logtime from " + DriverDbHelper.TABLE2_NAME + "  where logtime >= ?",
                            new String[]{startDate});
                }
                break;

            case 3:// blood_o2_table
                cursor = db.rawQuery("select value, logtime from " + DriverDbHelper.TABLE3_NAME + "  where logtime >= ?",
                        new String[]{startDate});
                break;

            case 4:// heart_rate_table
                cursor = db.rawQuery("select value, logtime from " + DriverDbHelper.TABLE4_NAME + "  where logtime >= ?",
                        new String[]{startDate});
                break;

            default:
                throw new IllegalArgumentException("the tableNum should between 1 and 4");
        }

        // 如果是体温，就返回float数组
        if(tableNum==1){
            List<Float>result = new ArrayList<>();
            if(cursor!=null)
            {
                while (cursor.moveToNext())
                {
                    float tmp=cursor.getFloat(0);
                    result.add(tmp);
                    date.add(cursor.getString(1));
                }
            }
            cursor.close();
            return result;
        }
        // 如果不是体温，就返回int数组
        else{
            List<Integer>result=new ArrayList<>();
            if(cursor!=null)
            {
                while (cursor.moveToNext())
                {
                    int tmp = cursor.getInt(0);
                    result.add(tmp);
                    date.add(cursor.getString(1));
                }
            }
            cursor.close();
            return result;
        }
    }

    // 用于创建database, tables
    static class DriverDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "driverDatebase";    // Database Name
        private static final int DATABASE_Version = 1;   // Database Version
        private static final String TABLE1_NAME = "temperature_table";
        private static final String TABLE2_NAME = "blood_pressure_table";
        private static final String TABLE3_NAME = "blood_o2_table";
        private static final String TABLE4_NAME = "heart_rate_table";
        private Context context;

        public DriverDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);  //建库
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {   // 建表

            // 体温，浮点值
            String create_table1 = "create table " + TABLE1_NAME
                    + "( logtime  datetime not null default (date('now')) primary key,"
                    + "high REAL, "
                    + "low REAL);";
            // 血压
            String create_table2 = "create table " + TABLE2_NAME
                    + "( logtime  datetime not null default (date('now')) primary key,"
                    + "high integer, "
                    + "low integer);";

            // 血氧
            String create_table3 = "create table " + TABLE3_NAME
                    + "( logtime  datetime not null default (date('now')) primary key,"
                    + "value integer);";
            // 心率
            String create_table4 = "create table " + TABLE4_NAME
                    + "( logtime  datetime not null default (date('now')) primary key,"
                    + "value integer);";

            db.execSQL(create_table1);
            db.execSQL(create_table2);
            db.execSQL(create_table3);
            db.execSQL(create_table4);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE "+TABLE1_NAME);
            db.execSQL("DROP TABLE "+TABLE2_NAME);
            db.execSQL("DROP TABLE "+TABLE3_NAME);
            db.execSQL("DROP TABLE "+TABLE4_NAME);

            onCreate(db);
        }
    }

}
