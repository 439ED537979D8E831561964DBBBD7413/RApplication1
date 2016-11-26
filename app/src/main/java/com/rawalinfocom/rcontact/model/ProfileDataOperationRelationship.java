package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationRelationship {
    private String relationshipType;
    private String relationshipDetails;
    private String relationshipPublic;

    @JsonProperty("relationship_type")
    public String getRelationshipType() {return this.relationshipType;}

    public void setRelationshipType(String relationshipType) {this.relationshipType =
            relationshipType;}

    @JsonProperty("relationship_details")
    public String getRelationshipDetails() {return this.relationshipDetails;}

    public void setRelationshipDetails(String relationshipDetails) {this.relationshipDetails =
            relationshipDetails;}

    @JsonProperty("relationship_public")
    public String getRelationshipPublic() {return this.relationshipPublic;}

    public void setRelationshipPublic(String relationshipPublic) {this.relationshipPublic = relationshipPublic;}
}
