package ar.gob.onti.firmador.model;
/*
 * PropsConfig.java
 * author: mpin
 * owner : ONTI
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Clase encargada de modelizar el archivo de configuracion de la applicacion
 * @author ocaceres
 *
 */
public final class PropsConfig {
	private static PropsConfig instance=null;
	private ResourceBundle myProps=null;
	private String nombreArchivo="";
	private String nombreArchivoTemporal="";
	private boolean visible;
    // Directorio en cliente
	private Logger 		appLogFile;
   	private String sourceDir;
	// Alamcenamiento de Certificados
    private String storeType;
	private String browser;
    // Propiedades generales de firma
	private String reason = "";
	private String location = "";
	private String usuario = "";
	private PreguntasRespuestas preguntas;
	private String objetoDominio = "";
	private String tipoArchivo = "";
	//Autoridades certificantes
	private List<String> autoCertificantes;
	private Map<String, String> mapaDatosUsuarioFirma;
	// Conexion servidor
	private String uploadURL = "";
	private String uplBoundary = "";
	private String downloadURL = "";
	// Mensaje error
    private String propsError;
    public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public Logger getAppLogFile() {
		return appLogFile;
	}
	public void setAppLogFile(Logger appLogFile) {
		this.appLogFile = appLogFile;
	}



	public static synchronized  PropsConfig getInstance() {
		if (PropsConfig.instance == null){
			PropsConfig.instance = new PropsConfig();
		}
		return PropsConfig.instance;
	}
    public Map<String, String> getMapaDatosUsuarioFirma() {
		return mapaDatosUsuarioFirma;
	}
	public void setMapaDatosUsuarioFirma(Map<String, String> mapaDatosUsuarioFirma) {
		this.mapaDatosUsuarioFirma = mapaDatosUsuarioFirma;
	}
	
	
	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public void setNombreArchivoTemporal(String nombreArchivoTemporal) {
		this.nombreArchivoTemporal = nombreArchivoTemporal;
	}

	public String getNombreArchivoTemporal() {
		return nombreArchivoTemporal;
	}

	public String getUserName() {
		return usuario;
	}

	public void setUserName(String usuario) {
		this.usuario = usuario;
	}

	public PreguntasRespuestas getPreguntas() {
		return preguntas;
	}

	public void setPreguntas(PreguntasRespuestas preguntas) {
		this.preguntas = preguntas;
	}

	
	public List<String> getAutoCertificantes() {
		return autoCertificantes;
	}

	

	public String getSourceDir() {
		return sourceDir;
	}


	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}





	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}


	public String getUploadURL() {
		return uploadURL;
	}

	public void setUploadURL(String uploadURL) {
		this.uploadURL = uploadURL;
	}

	public String getUplBoundary() {
		return uplBoundary;
	}

	public void setUplBoundary(String uplBoundary) {
		this.uplBoundary = uplBoundary;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}



	public void setPropsError(String propsError) {
		this.propsError = propsError;
	}
    public String getString(String clave){
    	return this.myProps.getString(clave);
    }
	private PropsConfig() {
		sourceDir = "";
		reason = "";
		location = "";
		uploadURL = "";
		uplBoundary = "";
		downloadURL = "";
		propsError = "";
		autoCertificantes= new ArrayList<String>();
		this.myProps = ResourceBundle.getBundle("properties.firma",new Locale("es","AR"));

	}
	
	
	/**
	 *  Configuracion emisores aceptados
	 *  Si no se especifico ninguna AC, entonces se aceptan todas
	 */
	public void cargarIssuers(){
		String readProperty="";
		String issuerCN="";
		int itera = 1;
		while (true) {
			 readProperty = "IssuerCN" + itera;
			if (!myProps.containsKey(readProperty)) {
				break;
			}
			issuerCN = myProps.getString(readProperty).trim();
			autoCertificantes.add(itera-1, issuerCN);
			itera++;
		}
		if (itera == 1){
			autoCertificantes.add(itera-1, "*");
		}
	}
	/**
	 * carga el directorio temporal donde se almacenaran los archvos 
	 * para eralizar la firma del documento
	 * @throws IOException
	 */
	public void cargarDirectorioTemporal() throws IOException{
		String rutalTemp=System.getProperty("java.io.tmpdir");
		File srcDirectory=new File(rutalTemp);
		if ((srcDirectory.isDirectory() && srcDirectory.exists())) {
			if(!rutalTemp.endsWith(File.separator)){
				rutalTemp += File.separator;
			}
			sourceDir = rutalTemp;
		} else {
			throw new IOException(myProps.getString("errorDirectorioTemporal")) ;
		}
	}
	public boolean readProps() {
		
		try {
            cargarDirectorioTemporal();
            cargarIssuers();
            this.visible=true;
			uplBoundary = myProps.getString("UploadBoundary").trim();
			this.mapaDatosUsuarioFirma= new HashMap<String, String>();
		} catch (IOException e) {
			propsError = "M�todo PropsConfig.ReadProps(): Error de lectura en archivo propiedades";
			if (e.getMessage() != null) {
				propsError += "\nMensaje JVM: " + e.getMessage();
			}
			return false;
		} 

		return true;
	}
	
	public String getPropsError () {
		return propsError;
	}
	
	
	


	
	
}
