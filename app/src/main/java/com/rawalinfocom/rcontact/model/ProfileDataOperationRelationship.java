package com.rawalinfocom.rcontact.model;

public class ProfileDataOperationRelationship {
    private String relationship_type;
    private String relationship_details;
    private String relationship_public;

    public String getRelationshipType() {return this.relationship_type;}

    public void setRelationshipType(String relationship_type) {this.relationship_type =
            relationship_type;}

    public String getRelationshipDetails() {return this.relationship_details;}

    public void setRelationshipDetails(String relationship_details) {this.relationship_details =
            relationship_details;}

    public String getRelationshipPublic() {return this.relationship_public;}

    public void setRelationshipPublic(String relationship_public) {this.relationship_public = relationship_public;}
}
