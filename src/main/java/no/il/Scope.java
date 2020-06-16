package no.il;

import no.il.dto.JWT;
import no.il.dto.TokenResponse;
import no.il.utils.HttpCaller;
import no.il.utils.Print;
import no.il.utils.PropertiesReader;



public class Scope extends Authentication{

    public Scope(PropertiesReader props) {
        super(props);
    }

    public void goScopes() {
        if (props.getScopeList()) {
            listScopes();
        }

        if (props.getScopeAdd()) {
            addScopeAccess();
        }

        if (props.getScopeAccess()) {
            listScopeAccess();
        }

        if (props.getScopeDelete()) {
            deleteScopeAccess();
        }
    }

    /**
     * Retrieve all scopes registered to the clients org.nr.
     * Requeire a client who has access to the scope: idporten:scopes.read
     */
    public void listScopes() {
        Print.out("\n\n===== Getting all scopes defined by clients company =====");
        Print.out("Client ID: "+props.getAdminUser());
        Print.out("---------------------------------------------");

        try {
            TokenResponse tokenResponse = super.getAdminAccessTokenFromMaskinporten(props.getAdminUser(), "idporten:scopes.read");

            Print.out("\n\nStep 3: Calling Maskinporten to retrieve all scopes");
            Print.out("Scopes endpoint: "+ props.getScopeListEndpoint());

            String scopes = HttpCaller.execute(tokenResponse.getAccess_token(), props.getScopeListEndpoint(),"GET");
            Print.JSON("***** Scopes retrieved from Maskinporten *****", scopes);

        } catch(Exception e) {
            e.printStackTrace();
        }
        Print.out("---------------------------------------------\n\n");
    }

    public void listScopeAccess() {
        Print.out("\n\n===== Getting access list for one scope =====");
        Print.out("Client ID: "+props.getAdminUser());
        Print.out("Scope: "+props.getScopeAccessId());
        Print.out("---------------------------------------------");

        try {
            TokenResponse tokenResponse = super.getAdminAccessTokenFromMaskinporten(props.getAdminUser(), "idporten:scopes.read");

            Print.out("\n\nStep 3: Calling Maskinporten to retrieve access list for scope");
            Print.out("Scope access endpoint: "+props.getScopeAccessEndpoint()+props.getScopeAccessId());

            String accessList = HttpCaller.execute(tokenResponse.getAccess_token(), props.getScopeAccessEndpoint()+props.getScopeAccessId(),"GET");
            Print.JSON("***** Access list retrieved from Maskinporten *****", accessList);

        } catch(Exception e) {
            e.printStackTrace();
        }
        Print.out("---------------------------------------------\n\n");
    }


    public void addScopeAccess() {
        Print.out("\n\n===== Adding access to scope for orgnr =====");
        Print.out("Client ID: "+props.getAdminUser());
        Print.out("Endpoint: "+props.getScopeAddEndpoint());
        Print.out("Scope: "+props.getScopeAddId());
        Print.out("Orgnr: "+props.getScopeAddOrgnr());
        Print.out("---------------------------------------------");

        try {
            TokenResponse tokenResponse = super.getAdminAccessTokenFromMaskinporten(props.getAdminUser(), "idporten:scopes.write");

            Print.out("\n\nStep 3: Calling Maskinporten to add access to scope");
            Print.out("Add access endpoint: "+props.getScopeAddEndpoint()+props.getScopeAddOrgnr()+"?scope="+props.getScopeAddId());

            String accessAddedRespone = HttpCaller.execute(tokenResponse.getAccess_token(),props.getScopeAddEndpoint()+props.getScopeAddOrgnr()+"?scope="+props.getScopeAddId(),"PUT");
            Print.JSON("***** Response from Maskinporten after adding access *****", accessAddedRespone);

        } catch(Exception e) {
            e.printStackTrace();
        }
        Print.out("---------------------------------------------\n\n");
    }

    public void deleteScopeAccess() {
        Print.out("\n\n===== Deleting access to a scope for a given orgnr =====");
        Print.out("Client ID: "+props.getAdminUser());
        Print.out("Endpoint: "+props.getScopeDeleteEndpoint());
        Print.out("Scope: "+props.getScopeDeleteId());
        Print.out("Orgnr: "+props.getScopeDeleteOrgnr());
        Print.out("---------------------------------------------");

        try {
            TokenResponse tokenResponse = super.getAdminAccessTokenFromMaskinporten(props.getAdminUser(), "idporten:scopes.write");

            Print.out("\n\nStep 3: Calling Maskinporten to delet access to scope");
            Print.out("Add access endpoint: "+props.getScopeDeleteEndpoint()+props.getScopeDeleteOrgnr()+"?scope="+props.getScopeDeleteId());

            String accessAddedRespone = HttpCaller.execute(tokenResponse.getAccess_token(),props.getScopeDeleteEndpoint()+props.getScopeDeleteOrgnr()+"?scope="+props.getScopeDeleteId(),"DELETE");
            Print.JSON("***** Response from Maskinporten after adding access *****", accessAddedRespone);

        } catch(Exception e) {
            e.printStackTrace();
        }
        Print.out("---------------------------------------------\n\n");
    }


}
