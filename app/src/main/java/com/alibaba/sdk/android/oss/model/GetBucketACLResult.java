package com.alibaba.sdk.android.oss.model;

public class GetBucketACLResult extends OSSResult {
    private CannedAccessControlList bucketACL;
    private Owner bucketOwner = new Owner();

    public void setBucketOwner(String ownerName) {
        this.bucketOwner.setDisplayName(ownerName);
    }

    public String getBucketOwner() {
        return this.bucketOwner.getDisplayName();
    }

    public void setBucketOwnerID(String id) {
        this.bucketOwner.setId(id);
    }

    public String getBucketOwnerID() {
        return this.bucketOwner.getId();
    }

    public void setBucketACL(String bucketACL2) {
        this.bucketACL = CannedAccessControlList.parseACL(bucketACL2);
    }

    public String getBucketACL() {
        return this.bucketACL.toString();
    }
}
