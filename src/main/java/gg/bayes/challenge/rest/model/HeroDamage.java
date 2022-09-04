package gg.bayes.challenge.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class HeroDamage {
    String target;
    @JsonProperty("damage_instances")
    @SerializedName("damage_instances")
    Long damageInstances;
    @JsonProperty("total_damage")
    @SerializedName("total_damage")
    Long totalDamage;
}
