package edu.sustech.hpc.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@SuppressWarnings("unused")
public class JwtUtil {
	public static final long DEFAULT_TTL=3600; //60*60=1h, JWT的TTL

	public static final String BASE64 = "eyJhbGciOaJub25lIn0VGhlIHTydWUgc2lnbiBvZicpbnRlbGxpZ2VuY2UgaiMgbm90IGtAb3esZWRnsSBidXQghW1hO2luYXRpb24u";

	//解加密使用的密钥
	public static final SecretKey KEY = new SecretKeySpec(Base64.getDecoder().decode(BASE64),"HmacSHA256");

	public static final String ISSUER="hpc.sustech.edu.cn"; //签发者

	public static final Logger LOGGER = LogManager.getLogger(JwtUtil.class);

	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-","");
	}

	/**
	 * @param subject Token中要存放的数据(Json格式)
	 */
	public static String createJwt(String subject){
		return createJwt(subject,null);
	}

	/**
	 * @param subject Token中要存放的数据(Json格式)
	 */
	public static String createJwt(String subject,Long ttlMills){
		return createJwt(subject,ttlMills,getUUID());
	}

	/**
	 * @param subject Token中要存放的数据(Json格式)
	 */
	public static String createJwt(String subject,Long ttlMills,String uuid){
		return getJwtBuilder(subject,ttlMills,uuid).compact();
	}

	/**
	 * @param subject Token中要存放的数据(Json格式)
	 */
	private static JwtBuilder getJwtBuilder(String subject, Long ttlMills, String uuid){
		if(ttlMills==null){
			ttlMills= DEFAULT_TTL*1000;
		}
		return Jwts.builder()
					.id(uuid)
					.subject(subject)
					.issuer(ISSUER)
					.issuedAt(new Date())
					.signWith(KEY, Jwts.SIG.HS256)
					.expiration(new Date(System.currentTimeMillis() + ttlMills));
	}

	public static Claims parseJwt(String jwt){
		try{
			return Jwts.parser()
					   .verifyWith(KEY)
					   .build()
					   .parseSignedClaims(jwt)
					   .getPayload();
		}catch (Exception e){
			if(e instanceof ExpiredJwtException){
				LOGGER.info("Expired JWT: {}",jwt);
				throw new BadCredentialsException(String.format("JWT %s is expired",jwt),e);
			}else{
				LOGGER.info("Invalid JWT: {}",jwt);
				throw new BadCredentialsException(String.format("JWT %s is invalid",jwt),e);
			}
		}
	}
}
