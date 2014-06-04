
*******CONFIGURACION DEL AMBIENTE*******

//Instalar JAVA JDK1.6.0.33 x86
http://www.oracle.com/technetwork/java/javase/downloads/index.html

//Instalar Maven 3.0.4
http://maven.apache.org/download.html

//Descargar iTextPDF 5.0.2
http://olex.openlogic.com/packages/itext/5.0.2

//Instalar manualmente iText 5.0.2 en el repositorio local de Maven
"[Carpeta de Maven]\mvn" install:install-file -Dfile=[Carpeta del IText]\iText-5.0.2.jar -DgroupId=com.lowagie -DartifactId=itext -Dversion=5.0.2 -Dpackaging=jar

//Instalar manualmente la libreria de plugin de la implementacion de JDK
"[Carpeta de Maven]\mvn" install:install-file -Dfile="[Carpeta de JDK]\jre\lib\plugin.jar" -DgroupId=plugin -DartifactId=plugin -Dversion=1.5 -Dpackaging=jar

//Abrir la carpeta de proyecto

//Editar el archivo pom.xml, colocarle la ruta correcta del keystore.
<configuration>
    <keystore>[Ruta completa del keystore]</keystore>			
	<alias>[Alias del keystore]</alias>
	<storepass>[Password del keystore]</storepass>
	<keypass>[Password del keystore]</keypass>
</configuration>

//Setear la variable JAVA_HOME dentro del cmd de Windows, a la version de java 1.6.0.33.
SET JAVA_HOME=[Carpeta de JDK]

//Buildear el proyecto en entorno de test
"[Carpeta de Maven]\mvn" -e -X clean compile install

//Buildear el proyecto en entorno de producci�n
"[Carpeta de Maven]\mvn" -e -X clean compile install -DFIRMADOR_PROD

//Para buildear en producci�n es necesario grabar las siguientes variables de entorno para configurar el signado del jar:
set FIRMADOR_KEYSTORE = [Carpeta del keystore]
set FIRMADOR_ALIAS = [Alias del keystore] 
set FIRMADOR_STOREPASS = [Password de keystore]
set FIRMADOR_KEYPASS = [Password de keystore]

//Para generar un par de claves de ejemplo para la firma del JAR:
	$JAVA_HOME/bin/keytool -genkey -keystore [KEYSTORE file location] -alias [ALIAS]

	$JAVA_HOME/bin/keytool -selfcert -alias [ALIAS] -keystore [KEYSTORE file location]

//Testear el proyecto
"[Carpeta de Maven]\mvn" -e -X test

Una vez terminada la creaci�n del applet, puede ser publicada en cualquier servidor web.


Configuraci�n de drivers para el firmado por token:

Existen 3 archivos en el proyecto que configura los drivers segun cada tipo de sistema operativo:

app/resource/properties/providers-linux.properties
app/resource/properties/providers-mac.properties
app/resource/properties/providers-windows.properties

cada uno de los 3 archivos contiene el siguiente formato

[nombre]=[path absoluto del driver]

Este es un ejemplo de providers-windows.properties

eToken=c:\\windows\\system32\\eTPKCS11.dll
ASECard=c:\\windows\\system32\\asepkcs.dll

A la hora de buscar los repositorios de certificados, el firmador buscar� cual de los drivers tiene instalado y buscar� los certificados del token cuando sea posible.

Para agregar nuevos drivers, es necesario modificar estos 3 archivos de properties y volver a compilar el proyecto.

Los certificados son verificados segun su issuer, su fecha y OCSP. Los certificados deben cumplir con todas estas validaciones para poder firmar documentos.

