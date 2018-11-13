//package ci.smile.colloque.helper;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ParamsUtils {
//
//	// -----------------------------
//	// ATTRIBUTES
//	// -----------------------------
//
//	@Value("${smtp.mail.host}")
//	private String	smtpHost;
//
//	@Value("${smtp.mail.port}")
//	private Integer	smtpPort;
//
//	@Value("${smtp.mail.login}")
//	private String	smtpLogin;
//
//	@Value("${smtp.mail.password}")
//	private String	smtpPassword;
//	
//	
//	@Value("${code.validity}")
//	private Integer	codeValidity;
//
//	@Value("${files.directory}")
//	private String	filesDirectory;
//	
//	@Value("${files.directory.report}")
//	private String	filesDirectoryReport;
//
//	
//	/*
//	 * ES
//	 */
//	
//	/*
//	 * COLLECTE
//	 */
//	@Value("${es.collecte.indexName}")
//	private String esCollecteIndexName;
//	@Value("${es.collecte.indexType}")
//	private String esCollecteIndexType;
//	@Value("${es.collecte.templateName}")
//	private String esCollecteTemplateName;
//	
//
//	public static final Integer MAX_RESULT_WINDOW = 1000000000;
//	public static final Integer max_scrool = 1000000;
//	
//	
//	// -----------------------------
//	// GETTERS & SETTERS
//	// -----------------------------
//
//
//	public String getFilesDirectoryReport() {
//		String baseDir = System.getenv("CATALINA_HOME");
//		System.out.println(baseDir);
//		
//		if(baseDir!=null) {
//			if (!baseDir.endsWith("/"))
//				baseDir += "/";
//		}else {
//			baseDir = "/";
//		}
//		return baseDir + filesDirectoryReport;
//	}
//
//	/**
//	 * @return the filesDirectory
//	 */
////	public String getFilesDirectory() {
////		return filesDirectory;
////	}
//	public String getFilesDirectory() {
//		String baseDir = System.getenv("CATALINA_HOME");
//		System.out.println(baseDir);
//		
//		if(baseDir!=null) {
//			if (!baseDir.endsWith("/"))
//				baseDir += "/";
//		}else {
//			baseDir = "/";
//		}
//		return baseDir + filesDirectory;
//	}
//
//	/**
//	 * @param filesDirectory
//	 *            the filesDirectory to set
//	 */
//	public void setFilesDirectory(String filesDirectory) {
//		this.filesDirectory = filesDirectory;
//	}
//
//	public String getSmtpHost() {
//		return smtpHost;
//	}
//
//	public void setSmtpHost(String smtpHost) {
//		this.smtpHost = smtpHost;
//	}
//
//	public Integer getSmtpPort() {
//		return smtpPort;
//	}
//
//	public void setSmtpPort(Integer smtpPort) {
//		this.smtpPort = smtpPort;
//	}
//
//	public String getSmtpLogin() {
//		return smtpLogin;
//	}
//
//	public void setSmtpLogin(String smtpLogin) {
//		this.smtpLogin = smtpLogin;
//	}
//
//	public String getSmtpPassword() {
//		return smtpPassword;
//	}
//
//	public void setSmtpPassword(String smtpPassword) {
//		this.smtpPassword = smtpPassword;
//	}
//
//	public Integer getCodeValidity() {
//		return codeValidity;
//	}
//
//	public void setCodeValidity(Integer codeValidity) {
//		this.codeValidity = codeValidity;
//	}
//
//	public void setFilesDirectoryReport(String filesDirectoryReport) {
//		this.filesDirectoryReport = filesDirectoryReport;
//	}
//
//	public String getEsCollecteIndexName() {
//		return esCollecteIndexName;
//	}
//
//	public String getEsCollecteIndexType() {
//		return esCollecteIndexType;
//	}
//
//	public String getEsCollecteTemplateName() {
//		return esCollecteTemplateName;
//	}
//
////	public String getFilesUrl() {
////		return filesUrl;
////	}
////
////	public void setFilesUrl(String filesUrl) {
////		this.filesUrl = filesUrl;
////	}
////	
//
//
//	
//	 
//}
