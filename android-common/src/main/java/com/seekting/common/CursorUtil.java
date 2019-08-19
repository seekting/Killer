package com.seekting.common;

import android.database.Cursor;

public class CursorUtil {

    public static String toMarkDownString(Cursor cursor) {
        if (cursor == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        String[] names = cursor.getColumnNames();
        stringBuilder.append("|");
        for (String name : names) {
            stringBuilder.append(name + "|");
        }
        stringBuilder.append("\n");


        stringBuilder.append("|");
        for (String name : names) {
            stringBuilder.append("---|");
        }
        stringBuilder.append("\n");


        while (cursor.moveToNext()) {
            stringBuilder.append("|");
            for (String name : names) {
                int index = cursor.getColumnIndex(name);
                int type = cursor.getType(index);
                Object value = null;
                switch (type) {
                case Cursor.FIELD_TYPE_NULL:
                    value = "NULL";
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    value = cursor.getInt(index);
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    value = cursor.getFloat(index);
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    value = cursor.getString(index);
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    byte[] bytes = cursor.getBlob(index);
                    int size = bytes == null ? 0 : bytes.length;
                    value = "byte[" + size + "]";
                    break;
                }
                stringBuilder.append(value + "|");

            }
            stringBuilder.append("\n");

        }
        return stringBuilder.toString();
    }
}
