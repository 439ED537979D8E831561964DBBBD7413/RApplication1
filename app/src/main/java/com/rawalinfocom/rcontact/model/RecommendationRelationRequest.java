//package com.rawalinfocom.rcontact.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import java.util.ArrayList;
//
///**
// * Created by user on 29/11/16.
// */
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
////@JsonInclude(JsonInclude.Include.NON_DEFAULT)
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class RecommendationRelationRequest {
//
//    private Integer rrmToPmId;
//    private RelationUserProfile relationUserProfile = new RelationUserProfile();
//    private ArrayList<RelationRecommendation> friendRelationList = new ArrayList<>();
//    private ArrayList<RelationRecommendation> familyRelationList = new ArrayList<>();
//    private ArrayList<RelationRecommendation> businessRelationList = new ArrayList<>();
//
//    @JsonProperty("toProfile")
//    public RelationUserProfile getRelationUserProfile() {
//        return relationUserProfile;
//    }
//
//    public void setRelationUserProfile(RelationUserProfile relationUserProfile) {
//        this.relationUserProfile = relationUserProfile;
//    }
//
//    @JsonProperty("friend")
//    public ArrayList<RelationRecommendation> getFriendRelationList() {
//        return friendRelationList;
//    }
//
//    public void setFriendRelationList(ArrayList<RelationRecommendation> friendRelationList) {
//        this.friendRelationList = friendRelationList;
//    }
//
//    @JsonProperty("family")
//    public ArrayList<RelationRecommendation> getFamilyRelationList() {
//        return familyRelationList;
//    }
//
//    public void setFamilyRelationList(ArrayList<RelationRecommendation> familyRelationList) {
//        this.familyRelationList = familyRelationList;
//    }
//
//    @JsonProperty("business")
//    public ArrayList<RelationRecommendation> getBusinessRelationList() {
//        return businessRelationList;
//    }
//
//    public void setBusinessRelationList(ArrayList<RelationRecommendation> businessRelationList) {
//        this.businessRelationList = businessRelationList;
//    }
//
//    @JsonProperty("rrm_to_pm_id")
//    public Integer getRrmToPmId() {
//        return rrmToPmId;
//    }
//
//    public void setRrmToPmId(Integer rrmToPmId) {
//        this.rrmToPmId = rrmToPmId;
//    }
//}
