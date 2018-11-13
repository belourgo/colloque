
/*
 * Created on 26 oct. 2018 ( Time 19:07:10 )
 * Generator tool : Telosys Tools Generator ( version 2.1.1 )
 * Copyright 2017 Savoir Faire Linux. All Rights Reserved.
 */

package ci.smile.colloque.helper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.RandomStringUtils;


/**
 * Utilities
 * 
 * @author SFL Back-End developper
 *
 */
public class Utilities {

	public static Date getCurrentDate() {
		return new Date();
	}

	/**
	 * Check if a String given is an Integer.
	 *
	 * @param s
	 * @return isValidInteger
	 *
	 */
	public static boolean isInteger(String s) {
		boolean isValidInteger = false;
		try {
			Integer.parseInt(s);

			// s is a valid integer
			isValidInteger = true;
		} catch (NumberFormatException ex) {
			// s is not an integer
		}

		return isValidInteger;
	}

	public static String generateCodeOld() {
		String formatted = null;
		formatted = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
		return formatted;
	}

	public static String generateCode() {
		String formatted = null;
		SecureRandom secureRandom = new SecureRandom();
		int num = secureRandom.nextInt(100000000);
		formatted = String.format("%05d", num);
		return formatted;
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Long.parseLong(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Check if a Integer given is an String.
	 *
	 * @param i
	 * @return isValidString
	 *
	 */
	public static boolean isString(Integer i) {
		boolean isValidString = true;
		try {
			Integer.parseInt(i + "");

			// i is a valid integer

			isValidString = false;
		} catch (NumberFormatException ex) {
			// i is not an integer
		}

		return isValidString;
	}

	public static boolean isValidEmail(String email) {
		String regex = "^(.+)@(.+)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}

	public static String encrypt(String str) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte[] hashedBytes = digest.digest(str.getBytes("UTF-8"));

		return convertByteArrayToHexString(hashedBytes);
	}

	public static boolean isDateValid(String date) {
		try {
			String simpleDateFormat = "dd/MM/yyyy";

			if (date.contains("-"))
				simpleDateFormat = "dd-MM-yyyy";
			else if (date.contains("/"))
				simpleDateFormat = "dd/MM/yyyy";
			else
				return false;

			DateFormat df = new SimpleDateFormat(simpleDateFormat);
			df.setLenient(false);
			df.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static String GenerateValueKey(String code) {
		String result = null;
		// String prefix = prefixe;
		String suffix = null;
		String middle = null;
		String separator = "-";
		final String defaut = "000001";
		try {

			SimpleDateFormat dt = new SimpleDateFormat("yy-MM-dd-ss");
			String _date = dt.format(new Date());
			String[] spltter = _date.split(separator);
			middle = spltter[0] + spltter[1] + spltter[2] + spltter[3];
			if (code != null) {
				// Splitter le code pour recuperer les parties
				// String[] parts = code(separator);
				String part = code.substring(1);
				System.out.println("part" + part);

				if (part != null) {
					int cpt = new Integer(part);
					cpt++;

					String _nn = String.valueOf(cpt);

					switch (_nn.length()) {
						case 1:
							suffix = "00000" + _nn;
							break;
						case 2:
							suffix = "0000" + _nn;
							break;
						case 3:
							suffix = "000" + _nn;
							break;
						case 4:
							suffix = "00" + _nn;
							break;
						case 5:
							suffix = "0" + _nn;
							break;
						default:
							suffix = _nn;
							break;
					}
					// result = prefix + separator + middle + separator +
					// suffix;
					result = middle + separator + suffix;
				}
			} else {
				// result = prefix + separator + middle + separator + defaut;
				result = middle + separator + defaut;
			}
		} catch (Exception e) {

		}
		return result;
	}

	public static Integer getAge(Date dateNaissance) throws ParseException, Exception {
		Integer annee = 0;

		if (dateNaissance == null) {
			annee = 0;
		}
		Calendar birth = new GregorianCalendar();
		birth.setTime(dateNaissance);
		Calendar now = new GregorianCalendar();
		now.setTime(new Date());
		int adjust = 0;
		if (now.get(Calendar.DAY_OF_YEAR) - birth.get(Calendar.DAY_OF_YEAR) < 0) {
			adjust = -1;
		}
		annee = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + adjust;
		return annee;
	}

	public static Boolean AvailableCode(String code) {
		if (code == null || code.isEmpty()) {
			return false;
		}
		Locale local = new Locale(code, "");
		return LocaleUtils.isAvailableLocale(local);

	}

	public static String normalizeFileName(String fileName) {
		String fileNormalize = null;
		fileNormalize = fileName.trim().replaceAll("\\s+", "_");
		fileNormalize = fileNormalize.replace("'", "");
		fileNormalize = Normalizer.normalize(fileNormalize, Normalizer.Form.NFD);
		fileNormalize = fileNormalize.replaceAll("[^\\p{ASCII}]", "");

		return fileNormalize;
	}

	private static String convertByteArrayToHexString(byte[] arrayBytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuffer.toString();
	}

	public static SimpleDateFormat findDateFormat(String date) {
		SimpleDateFormat simpleDateFormat = null;
		String regex_dd_MM_yyyy = "\\A0?(?:3[01]|[12][0-9]|[1-9])[/.-]0?(?:1[0-2]|[1-9])[/.-][0-9]{4}\\z";

		if (date.matches(regex_dd_MM_yyyy))
			if (date.contains("-"))
				simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			else if (date.contains("/"))
				simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		return simpleDateFormat;
	}

	/**
	 * @return Permet de retourner la date courante du systÃ¨me
	 *
	 */
	public static String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

	/**
	 * @param l
	 *            liste de vÃ©rification de doublons
	 * @return retourne le nombre de doublon trouvÃ©
	 *
	 */
	public static int getDupCount(List<String> l) {
		int cnt = 0;
		HashSet<String> h = new HashSet<>(l);

		for (String token : h) {
			if (Collections.frequency(l, token) > 1)
				cnt++;
		}

		return cnt;
	}

	public static boolean saveImage(String base64String, String nomCompletImage, String extension) throws Exception {

		BufferedImage image = decodeToImage(base64String);

		if (image == null) {

			return false;

		}

		File f = new File(nomCompletImage);

		// write the image

		ImageIO.write(image, extension, f);

		return true;

	}

	public static boolean saveVideo(String base64String, String nomCompletVideo) throws Exception {

		try {

			byte[] decodedBytes = Base64.getDecoder().decode(base64String);
			File file2 = new File(nomCompletVideo);
			FileOutputStream os = new FileOutputStream(file2, true);
			os.write(decodedBytes);
			os.close();

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

		return true;

	}

	public static BufferedImage decodeToImage(String imageString) throws Exception {

		BufferedImage image = null;

		byte[] imageByte;

		imageByte = Base64.getDecoder().decode(imageString);

		try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {

			image = ImageIO.read(bis);

		}

		return image;

	}

	public static String encodeToString(BufferedImage image, String type) {

		String imageString = null;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {

			ImageIO.write(image, type, bos);

			byte[] imageBytes = bos.toByteArray();

			imageString = new String(Base64.getEncoder().encode(imageBytes));

			bos.close();

		} catch (IOException e) {

			e.printStackTrace();

		}

		return imageString;

	}

	public static String convertFileToBase64(String pathFichier) {
		File originalFile = new File(pathFichier);
		String encodedBase64 = null;
		try {
			FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
			byte[] bytes = new byte[(int) originalFile.length()];
			fileInputStreamReader.read(bytes);
			encodedBase64 = new String(Base64.getEncoder().encodeToString((bytes)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return encodedBase64;
	}

	public static String getImageExtension(String str) {
		String extension = "";
		int i = str.lastIndexOf('.');
		if (i >= 0) {
			extension = str.substring(i + 1);
			return extension;
		}
		return null;
	}

	public static boolean fileIsImage(String image) {

		String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)";
		Pattern pattern = Pattern.compile(IMAGE_PATTERN);
		Matcher matcher = pattern.matcher(image);

		return matcher.matches();

	}

	public static boolean fileIsVideo(String video) {

		String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(mp4|avi|camv|dvx|mpeg|mpg|wmv|3gp|mkv))$)";
		Pattern pattern = Pattern.compile(IMAGE_PATTERN);
		Matcher matcher = pattern.matcher(video);

		return matcher.matches();

	}

	public static void createDirectory(String chemin) {
		File file = new File(chemin);
		if (!file.exists()) {
			try {
				FileUtils.forceMkdir(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void deleteFolder(String chemin) {
		File file = new File(chemin);
		try {
			if (file.exists() && file.isDirectory()) {
				FileUtils.forceDelete(new File(chemin));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteFile(String chemin) {
		File file = new File(chemin);
		try {
			if (file.exists() && file.getName() != null && !file.getName().isEmpty()) {

				FileUtils.forceDelete(new File(chemin));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean notBlank(String str) {
		return str != null && !str.isEmpty() && !str.equals("\n");
	}
	
	public static boolean notEmpty(List<String> lst) {
		return lst != null && !lst.isEmpty() && lst.stream().noneMatch(s -> s.equals("\n")) && lst.stream().noneMatch(s -> s.equals(null));
	}
	
	public static <T> boolean isNotEmpty(List<T> list){
		return (list != null && !list.isEmpty());
	}

	static public String GetCode(String Value, Map<String, String> Table) {

		for (Entry<String, String> entry : Table.entrySet()) {
			if (entry.getValue().equals(Value)) {
				return entry.getKey();
			}
		}
		return Value;
	}

//	public static boolean anObjectFieldsMapAllFieldsToVerify(List<Object> objets, Map<String, Object> fieldsToVerify) {
//		for (Object objet : objets) {
//			boolean oneObjectMapAllFields = true;
////			JSONObject jsonObject = new JSONObject(objet);
//			for (Map.Entry<String, Object> entry : fieldsToVerify.entrySet()) {
//				// slf4jLogger.info("jsonObject " +jsonObject);
//				String key = entry.getKey();
//				Object value = entry.getValue();
//				try {
//					if (!jsonObject.get(key).equals(value)) {
//						oneObjectMapAllFields = false;
//						break;
//					}
//				} catch (Exception e) {
//					oneObjectMapAllFields = false;
//					break;
//				}
//			}
//			if (oneObjectMapAllFields)
//				return true;
//		}
//
//		return false;
//	}

	public static String generateAlphanumericCode(Integer nbreCaractere) {
		String formatted = null;
		formatted = RandomStringUtils.randomAlphanumeric(nbreCaractere).toUpperCase();
		return formatted;
	}

	public static Boolean verifierEmail(String email) {
		Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher emailMatcher = emailPattern.matcher(email);
		return emailMatcher.matches();
	}
	
	
	//------------------------------
	//METHOD FOR VALID INTEGER VALUE 
	//------------------------------
	public static boolean validId( Integer value) {
		return value != null && value >1 ;
		
	}
}
