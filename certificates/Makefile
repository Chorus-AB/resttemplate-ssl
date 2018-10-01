###
#
#   This Makefile was used in generation of the example stores used in tests and is provided as reference.
#
#   It is a modified version of the Makefile found at :
#        https://github.com/eugenp/tutorials/tree/master/spring-security-x509/keystore
#   The file is accompanied with a nice article about x509 authentication: https://www.baeldung.com/x-509-authentication-in-spring-security
#
###

PASSWORD=changeit
CLIENT_PASSWORD=changeit
KEYSTORE=keystore.jks
HOSTNAME=localhost
CLIENTNAME=cid

# CN = Common Name
# OU = Organization Unit
# O  = Organization Name
# L  = Locality Name
# ST = State Name
# C  = Country (2-letter Country Code)
# E  = Email
DNAME_CA='CN=ca,OU=example.com,O=Example,L=Stockholm,ST=Stockholm,C=SE'
# For server certificates, the Common Name (CN) must be the hostname
DNAME_HOST='CN=$(HOSTNAME),OU=example.com,O=Example,L=Stockholm,ST=Stockholm,C=SE'
DNAME_CLIENT='CN=$(CLIENTNAME),OU=example.com,O=Example,L=Stockholm,ST=Stockholm,C=SE'
TRUSTSTORE=truststore.jks
CLIENT_TRUSTSTORE=client_truststore.jks

all: 
	cat Makefile	

create-all:	clean create-keystore add-host create-truststore add-client

create-keystore:
	# Generate a certificate authority (CA)
	keytool -genkey -alias ca -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass $(PASSWORD) \
	    -validity 3650 -dname $(DNAME_CA) \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD) -storetype pkcs12
	# Prepare client truststore
	keytool -export -alias ca -file ca.crt -rfc \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)
	keytool -import -trustcacerts -noprompt -alias ca -file ca.crt \
	    -keystore $(CLIENT_TRUSTSTORE) -storepass $(CLIENT_PASSWORD) -storetype pkcs12

add-host:
	# Generate a host certificate
	keytool -genkey -alias $(HOSTNAME) \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass $(PASSWORD) \
	    -validity 3650 -dname $(DNAME_HOST) \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)

	# Generate a host certificate signing request
	keytool -certreq -alias $(HOSTNAME) -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -file "$(HOSTNAME).csr" \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)

	# Generate signed certificate with the certificate authority
	keytool -gencert -alias ca \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "$(HOSTNAME).csr" -outfile "$(HOSTNAME).crt" -rfc \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)

	# Import signed certificate into the keystore
	keytool -import -trustcacerts -alias $(HOSTNAME) \
	    -file "$(HOSTNAME).crt" \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)

	# Import signed certificate into client truststore
	keytool -import -trustcacerts -alias $(HOSTNAME) -file "$(HOSTNAME).crt" \
	    -keystore $(CLIENT_TRUSTSTORE) -storepass $(CLIENT_PASSWORD) -storetype pkcs12

export-authority:
	# Export certificate authority
	keytool -export -alias ca -file ca.crt -rfc \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)


create-truststore: export-authority
	# Import certificate authority into a new truststore
	keytool -import -trustcacerts -noprompt -alias ca -file ca.crt \
	    -keystore $(TRUSTSTORE) -storepass $(PASSWORD) -storetype pkcs12

add-client:
	# Generate client certificate
	keytool -genkey -alias $(CLIENTNAME) \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass $(PASSWORD) \
	    -validity 3650 -dname $(DNAME_CLIENT) \
	    -keystore $(TRUSTSTORE) -storepass $(PASSWORD) -storetype pkcs12

	# Generate a host certificate signing request
	keytool -certreq -alias $(CLIENTNAME) -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -file "$(CLIENTNAME).csr" \
	    -keystore $(TRUSTSTORE) -storepass $(PASSWORD)

	# Generate signed certificate with the certificate authority
	keytool -gencert -alias ca \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "$(CLIENTNAME).csr" -outfile "$(CLIENTNAME).crt" -rfc \
	    -keystore $(KEYSTORE) -storepass $(PASSWORD)

	# Import signed certificate into the truststore
	keytool -import -trustcacerts -alias $(CLIENTNAME) \
	    -file "$(CLIENTNAME).crt" \
	    -keystore $(TRUSTSTORE) -storepass $(PASSWORD)

	# Export private certificate for importing into a browser
	keytool -importkeystore -srcalias $(CLIENTNAME) \
	    -srckeystore $(TRUSTSTORE) -srcstorepass $(PASSWORD) \
	    -destkeystore "$(CLIENTNAME).p12" -deststorepass $(CLIENT_PASSWORD) -destkeypass $(CLIENT_PASSWORD) -deststoretype PKCS12

clean:
	# Remove generated artifacts
	find . \( -name "$(CLIENTNAME)*" -o -name "$(HOSTNAME)*" -o -name "$(KEYSTORE)" -o -name "$(TRUSTSTORE)" -o -name ca.crt \) -type f -exec rm -f {} \;
