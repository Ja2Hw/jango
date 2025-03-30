package com.example.termproject;

import android.provider.BaseColumns;

public final class JangoDB {
    private JangoDB(){

    }
    public static class JangoTable implements BaseColumns{
        public static final String TABLE_NAME = "Jango";
        public static final String COLUMN_NAME_TITLE = "JangoName";
        public static final String COLUMN_NAME_CONTENTS = "Memo";
        public static final String COLUMN_NAME_IMAGE="Image";
    }
}
