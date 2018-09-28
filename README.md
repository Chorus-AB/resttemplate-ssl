# SSL For RestTemplate

This is a simple autoconfiguration for RestTemplate when client certificates are needed.

###When enabled, will inject the following beans into the context:
* ***ClientHttpRequestFactory* clientHttpsRequestFactory** - A HttpRequestFactory configured to use client certificate 
* ***RestTemplate* sslRestTemplate** - A rest template using clientHttpsRequestFactory

##Configuration:

| Property | Description | Default |
|---|---|---|
|restclient.ssl.enabled|Turns autoconfiguration on or off|true|
|restclient.ssl.cncheck|Hostname verification|true|
|restclient.ssl.forall|Injects a RestTemplateCustomizer into the RestTemplateBuilder so that all RestTemplate instances created with builder use the *clientHttpsRequestFactory*|false|
|restclient.ssl.truststore.file|Path to truststore file (containing CA's or trusted server certificates). Use an explicit "file:" prefix to enforce an absolute file path.|*null*|
|restclient.ssl.truststore.password|Password for truststore|*null*|
|restclient.ssl.truststore.type|Type of keystore|PKCS12|
|restclient.ssl.keystore.file|Path to keystore file containing client certificate. Use an explicit "file:" prefix to enforce an absolute file path.|*null*|
|restclient.ssl.keystore.password|Password for keystore|*null*|
|restclient.ssl.keystore.type|Type of keystore|PKCS12|
|restclient.ssl.keystore.alias|Keystore alias for client certificate|"1"|
|restclient.ssl.supportedprotocols|Comma separated list of supported protocols|"TLSv1.2"|
|restclient.ssl.supportedciphersuites|Comma separated list of supported cipher suites. If not set, will fallback to default java suites|*null*|

## License

 **Copyright (C) 2018 Chorus AB**

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.