package org.cambridge.eltpoc.model;

import io.realm.RealmObject;

/**
 * Created by jlundang on 6/10/15.
 */
public class CLMSCourse extends RealmObject {
    private String TYPE = "clms#course"; // This should be final
    private int nid;
    private String name;
    private String url;
    private String productName;
    private String productLogo;

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
}
