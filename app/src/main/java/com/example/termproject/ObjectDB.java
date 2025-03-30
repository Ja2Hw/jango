package com.example.termproject;

import android.provider.BaseColumns;

public final class ObjectDB {
    private ObjectDB(){

    }
    public static class ObjectTable implements BaseColumns{
        public static final String TABLE_NAME = "Object";
        public static final String COLUMN_NAME_TITLE = "ObjectName";
        public static final String COLUMN_NAME_CONTENTS = "ObjectMemo";
        public static final String COLUMN_NAME_DEAD= "ObjectDead";
        public static final String COLUMN_NAME_COUNT="ObjectCount";
        public static final String COLUMN_NAME_TYPE="ObjectType";
        public static final String COLUMN_NAME_IMAGE="Image";
        public static final String COLUMN_JANGO_ID="JangoID";
        public static final String COLUMN_JANGO_NAME="JangoName";
    }
}
