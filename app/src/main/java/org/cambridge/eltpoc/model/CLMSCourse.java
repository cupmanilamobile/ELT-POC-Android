package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CLMSCourse extends RealmObject {
    @Expose
    private String TYPE = "clms#course";
    @PrimaryKey
    @Expose
    private int nid;
    @Expose
    private String name;
    @Expose
    private String url;
    @Expose
    @SerializedName("product-name")
    private String productName = "";
    @Expose
    @SerializedName("product-logo")
    private String productLogo = "";
    private RealmList<CLMSClass> classes;

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductLogo() {
        return productLogo;
    }

    public void setProductLogo(String productLogo) {
        this.productLogo = productLogo;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        // Do nothing
    }

    public RealmList<CLMSClass> getClasses() {
        return classes;
    }

    public void setClasses(RealmList<CLMSClass> classes) {
        this.classes = classes;
    }
}