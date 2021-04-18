package com.kashpirovich.qrscanner;

import java.util.List;

public class Cinema2Class {
    public int id;
    public String name;
    public String address;
    public int block_flag;
    public String nameEng;
    public String addressEng;

    public Cinema2Class() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public class Root {
        public boolean success;
        public List<Cinema2Class> data;
        public String message;

        public Root() {
        }

        public boolean isSuccess() {
            return success;
        }

        public List<Cinema2Class> getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }

    }
}
