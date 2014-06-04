package ar.gob.onti.firmador.controler.conection;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import ar.gob.onti.firmador.model.PropsConfig;

/**
 * 
 * @author ocaceres
 * 
 */
public class HttpFileUploader extends HttpFileConnection {
	public HttpFileUploader() {
		super();
	}

	/**
	 * Le agrega la mensaje e error que le aparecera al usuario toda la
	 * informacion necesario para saber el motivo del error
	 * 
	 * @param mensajeDeError
	 * @param errOpera
	 * @param e
	 */
	private void cargarMensajeDeError(String errorOperacion, Exception e) {
		String signError = errorOperacion;
		if (e.getMessage() != null) {
			signError += "Mensaje JVM: " + e.getMessage();
		}
		setHttpFileError(signError);
	}

	/**
	 * Una vez firmado digitalmente el documento pdf, en el último paso envía
	 * mediante un post el archivo pdf firmado con los siguientes parámetros que
	 * pueden ser usados por su aplicación para verificar la validez de la firma
	 * del documento.
	 * 
	 * md5_file.- Hash md5 del archivo descargado a la carpeta temporal para ser
	 * firmado. md5_fileSigned.- Hash md5 del archivo pdf firmado digitalmente.
	 * serialCert.- Serial del certificado con el que se firmo el archivo
	 * hashCert.- Hash del certificado codigo.- este parámetro es un código
	 * interno que puede ser usado por su aplicación para identificar el
	 * documento o el tipo de documento a ser firmado, este código es devuelto
	 * sin ser modificado, en el post de subida del archivo. El post debe
	 * devolver “OK” en caso que de que las validaciones de seguridad del
	 * archivo con los parámetros recibidos sean exitosas, en caso contrario
	 * deberá devolver el error ocurrido, que será mostrado al usuario.
	 * 
	 * @param fileName
	 *            nombre del archiv a enviar
	 * @param boundary
	 *            para separea el vio del post
	 * @param codigo
	 *            codigo que se recibio como parametro del applet
	 * @param md5Values
	 *            vector de hash md5 del archivo firmado y del descargado
	 * @param certValues
	 *            vector de hash md5 del certificado con el que se firmo el
	 *            arcjhivo pdf
	 * @return
	 * @throws IOException
	 */
	public boolean doUpload(String fileName, PropsConfig myProps,
			String codigo, String objetoDominio, String tipoArchivo)
			throws IOException {
		String errorBuffer = "";

		try {
			HttpClient client = configureSSLHandling();
			HttpPost post = new HttpPost(myProps.getUploadURL());
			MultipartEntity entity = new MultipartEntity();
			entity.addPart("idDominio", new StringBody(objetoDominio));
			entity.addPart("tipoDeArchivo", new StringBody(tipoArchivo));
			entity.addPart("codigo", new StringBody(codigo));
			entity.addPart("userName", new StringBody(myProps.getUserName()));
			entity.addPart("md5_fileSigned", new StringBody(fileName));
			post.setEntity(entity);
			HttpResponse httpResponse = client.execute(post);
			int status = httpResponse.getStatusLine().getStatusCode();

			if (status != 200) {
				byte[] response = IOUtils.toByteArray(httpResponse.getEntity()
						.getContent());
				String responseString = new String(response, "UTF-8");
				post.releaseConnection();
				errorBuffer += "Error de recepción en el server :"
						+ responseString;
				this.setHttpFileError(errorBuffer);
				return false;
			}
			return true;
		} catch (Exception e) {
			cargarMensajeDeError("", e);
			return false;
		}
	}

	private HttpClient configureSSLHandling() {
		HttpClient client = new DefaultHttpClient();
		SchemeRegistry sr = client.getConnectionManager().getSchemeRegistry();
		sr.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		sr.register(new Scheme("https", 443, buildSSLSocketFactory()));
		return client;
	}

	private SSLSocketFactory buildSSLSocketFactory() {
		TrustStrategy ts = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] x509Certificates,
					String s) throws CertificateException {
				return true;
			}
		};

		SSLSocketFactory sf = null;
		try {
			sf = new SSLSocketFactory(ts,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		return sf;
	}
}
