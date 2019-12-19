# Maskinporten
Testklient for Maskinporten


```
audience=<Identifier of the idporten-oidc-provider instance you want to use, i.e. for ver2 env:  https://oidc-ver2.difi.no/idporten-oidc-provider/>
resource=<The intended audience for token. If included, the value will be transparantly set as the aud-claim in the access token>
token.endpoint=<URL to the token endpoint to use i.e. for ver2 env: https://oidc-ver2.difi.no/idporten-oidc-provider/token>
scopes.endpoint=<URL to the scopes endpoint to use i.e. for ver2 env: https://integrasjon-ver2.difi.no/scopes>
clients.endpoint=<URL to the clients endpoint to use i.e. for ver2 env: https://integrasjon-ver2.difi.no/clients>
keys.endpoint=<URL to the keys endpoint to use i.e. for ver2 env: https://integrasjon-ver2.difi.no/clients/oidc_svv_api/jwks>

keystore.file=<path to your keystore file holding your virksomhetssertifikat / keypair>
keystore.password=<keystore password>
keystore.alias=<alias for your virksomhetssertifikat's key>
keystore.alias.password=<alias password>
```

## Usage

To build and run use:

```
mvn package

java -jar target\maskinporten-1.0-SNAPSHOT.jar <property file name>

```

