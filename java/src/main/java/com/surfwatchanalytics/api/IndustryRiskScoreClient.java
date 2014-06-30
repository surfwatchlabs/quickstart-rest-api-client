package com.surfwatchanalytics.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class IndustryRiskScoreClient {

    private static final String API_BASE_URL = "https://www.surfwatchanalytics.com:443/v2";
        
    public static void main( String[] args ) {
        
        Client restClient = ClientBuilder.newClient();
        WebTarget target = restClient.target( API_BASE_URL )
                .path( "/industryRiskScores" )
                .queryParam( "yesterday", "true" );
        
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add( "app_id", "your_app_id" );
        headers.add( "app_key", "your_app_key" );

        System.out.println( "Request : GET, url : " + API_BASE_URL + "/industryRiskScores" );
        String response = target
                .request( MediaType.APPLICATION_JSON )  // alternately set "Content-Type" header
                .headers( headers )
                .get( String.class );
                
        // print response
        System.out.println( "Response from /yesterday resource : " + response );
        
        
        // deserialize to objects, could be done in conjunction with Jersey client
        GsonBuilder gb = new GsonBuilder();
        gb.setFieldNamingPolicy( FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES );
        // handle Joda DateTime object
        gb.registerTypeAdapter( DateTime.class, new DateTimeDeserializer() );
        Gson gson = gb.create();
        
        List<IndustryRiskScore> scores = gson.fromJson( response, new TypeToken<List<IndustryRiskScore>>(){}.getType() );
        System.out.println( "Found " + scores.size() + " industry risk scores from yesterday." );
        
        // do something useful with result
        for( IndustryRiskScore irs : scores ) {
            if( irs.getIndustryRisk() > 50 ) {
                StringBuilder sb = new StringBuilder();
                sb.append( "Found a risk score over 50 : " );
                sb.append( "industry=" );
                sb.append( irs.getIndustryDescription() );
                sb.append( ", riskScore=" );
                sb.append( irs.getIndustryRisk() );
                sb.append( ", analyticDay=" );
                sb.append( irs.getAnalyticDay() );
                System.out.println( sb.toString() );
            }
        }
        
        System.out.println( "Well that was fun!" );
    }

    class IndustryRiskScore {
        
        // most fields omitted for brevity
        @SerializedName( "analytic_day" )
        private DateTime analyticDay;

        @SerializedName( "industry_id" )
        private Integer industryId;

        @SerializedName( "industry_description" )
        private String industryDescription;

        @SerializedName( "industry_risk" )
        private Float industryRisk;

        public DateTime getAnalyticDay() {
            return analyticDay;
        }

        public void setAnalyticDay(DateTime analyticDay) {
            this.analyticDay = analyticDay;
        }

        public Integer getIndustryId() {
            return industryId;
        }

        public void setIndustryId(Integer industryId) {
            this.industryId = industryId;
        }

        public String getIndustryDescription() {
            return industryDescription;
        }

        public void setIndustryDescription(String industryDescription) {
            this.industryDescription = industryDescription;
        }

        public Float getIndustryRisk() {
            return industryRisk;
        }

        public void setIndustryRisk(Float industryRisk) {
            this.industryRisk = industryRisk;
        }

    }
    
    static class DateTimeDeserializer implements JsonDeserializer<DateTime> {
        @Override
        public DateTime deserialize( JsonElement json, Type typeOfT, JsonDeserializationContext context)  throws JsonParseException {
            return new DateTime( json.getAsString(), DateTimeZone.UTC );
        }
    }
    
}
