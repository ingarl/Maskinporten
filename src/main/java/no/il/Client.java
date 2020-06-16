package no.il;

import no.il.dto.TokenResponse;
import no.il.utils.HttpCaller;
import no.il.utils.Print;
import no.il.utils.PropertiesReader;

public class Client extends Authentication{

    public Client(PropertiesReader props) {
        super(props);
    }

    public void goClient() {
        if (props.getClientUserRead() != null) {
            listClients();
        }
    }


    /**
     * Retrieve all clients registered to the clients org.nr.
     * Require a client who has access to the scope: idporten:dcr.read
     */
    public void listClients() {
        Print.out("\n\n===== Getting all clients registered by company =====");
        Print.out("Client ID: "+props.getClientUserRead());
        Print.out("---------------------------------------------");
        try {
            TokenResponse tokenResponse = super.getAdminAccessTokenFromMaskinporten(props.getClientUserRead(), "idporten:dcr.read");

            Print.out("\n\nStep 3: Calling Samarbeidsportalen to retrieve all clients");
            Print.out("Client endpoint: "+ props.getClientsEndpoint());

            String certs = HttpCaller.execute(tokenResponse.getAccess_token(), props.getClientsEndpoint(),"GET");
            Print.JSON("***** Clients retrieved from Maskinporten *****", certs);

        } catch(Exception e) {
            e.printStackTrace();
        }
        Print.out("---------------------------------------------\n\n");
    }
}
