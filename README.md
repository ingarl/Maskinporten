# Maskinporten testclient
This project is a testclient for Maskinporten. The testclient have three usages,
1. Create an access_token with a specific scope
2. Administrate Maskinporten scopes
3. Administrate Maskinporten users and crypto keys

To use this testclient you must first:
* Create a "virksomhetssertifikat" and put the private key and the certificate in a Java KeyStore 
* Create a Maskinporten user with access to one or more of the following scope:
  1. idporten:dcr.[read|write] - For administration of users
  2. idporten:scopes.[read|write] - For administration of scopes
  3. [any] - Create an access_token for a specific API

### Client configuration
To use the testclient you need a property file holding your client configuration:

```
#Set this attribute to true if you want the access_token be shown in a readable format
prettyPrintResult=[true|false]

audience=<Identifier of the idporten-oidc-provider instance you want to use, i.e. for ver2 env:  https://oidc-ver2.difi.no/idporten-oidc-provider/>
resource=<The intended audience for token. If included, the value will be transparantly set as the aud-claim in the access token>

# Uncomment to retrieve an access_token, for the provided user and scope, to be used for an API call.
#token.endpoint=<URL to the token endpoint to use i.e. for ver2 env: https://oidc-ver2.difi.no/idporten-oidc-provider/token>
#accesstoken.user=<client id>
#accesstoken.scope=<scope>

# Uncomment to retrieve all scopes the company has access to.
# The provided user must have access to the scope: idporten:scopes.read
#scopes.endpoint=<URL to the scopes endpoint to use i.e. for ver2 env: https://integrasjon-ver2.difi.no/scopes>
#scope.read.user=<client id>

# Uncomment to retrieve all public certificates registered to the provided client.
# The provided user must have access to the scope: idporten:dcr.read
#certificate.endpoint=<URL to the keys endpoint to use i.e. for ver2 env: https://integrasjon-ver2.difi.no/clients/oidc_svv_api/jwks>
#certificate.read.user=<client id>

# Uncomment to retrieve all clients the company has access to.
# The provided user must have access to the scope: idporten:dcr.read
#clients.endpoint=<URL to the clients endpoint to use i.e. for ver2 env: https://integrasjon-ver2.difi.no/clients>
#client.read.user=<client id>

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

