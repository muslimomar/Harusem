package com.example.william.harusem.models;

import java.io.Serializable;

public class Attachment implements Serializable {

    public enum Type {

        AUDIO(0),
        VIDEO(1),
        IMAGE(2),
        DOC(3),
        LOCATION(4),
        OTHER(5);

        private int code;

        Type(int code) {
            this.code = code;
        }

        public static Type parseByCode(int code) {
            Type[] valuesArray = Type.values();
            Type result = null;
            for (Type value : valuesArray) {
                if (value.getCode() == code) {
                    result = value;
                    break;
                }
            }
            return result;
        }

        public int getCode() {
            return code;
        }
    }


}