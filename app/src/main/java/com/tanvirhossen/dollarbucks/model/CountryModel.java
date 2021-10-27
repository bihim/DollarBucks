package com.tanvirhossen.dollarbucks.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryModel {

    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("registry")
    @Expose
    private String registry;
    @SerializedName("countrycode")
    @Expose
    private String countrycode;
    @SerializedName("countryname")
    @Expose
    private String countryname;
    @SerializedName("asn")
    @Expose
    private Asn asn;
    @SerializedName("spam")
    @Expose
    private Boolean spam;
    @SerializedName("tor")
    @Expose
    private Boolean tor;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public Asn getAsn() {
        return asn;
    }

    public void setAsn(Asn asn) {
        this.asn = asn;
    }

    public Boolean getSpam() {
        return spam;
    }

    public void setSpam(Boolean spam) {
        this.spam = spam;
    }

    public Boolean getTor() {
        return tor;
    }

    public void setTor(Boolean tor) {
        this.tor = tor;
    }

    public class Asn {

        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("route")
        @Expose
        private String route;
        @SerializedName("start")
        @Expose
        private String start;
        @SerializedName("end")
        @Expose
        private String end;
        @SerializedName("count")
        @Expose
        private String count;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

    }

}
