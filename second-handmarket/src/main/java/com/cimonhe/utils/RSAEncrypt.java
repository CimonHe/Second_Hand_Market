package com.cimonhe.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAEncrypt {

	private static final String privateKey =   "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIqtuuwB/W9WRcXTnh4aUKpFhCKf+jV96MPI9MQC858UcKAh9Jj9qyU2FK1GdKozgyVd0vyIz7orGxDmyEbeoXfN8YxirCBBSDDj5oT/FyZrvEuUaMuxQbTynSX70Brp0FTerQRY3mGduK1I86GxQVDuKsaE6aSkDbfcaxyPPVhtAgMBAAECgYAVng9NSuIewOwPcnVra963LC701stCG2Z1XpYJGAyx95W24JQoZSKQomWGvTziazU+VenVw93dv2pwsAj4QywYLfyK0xNgUAuKyi38BxlsLZniLXuh1R1ITgdMY33bD3nHw7TijRY5iWj8QbBSktVlusmv/SEGUlK9wYnNfTJAAQJBAMkE+853J/K3D+H9wIGQtzeLt+OAg5kpM9FpFznP9L5Q42m91QxHd5vL5+85qk156ZNlOU86Unpq4nWjYjODSm0CQQCwm8FNfHPpO6hhtuU9cEzyr0eHQ7aIyC5Ar+MvmfIdV/g8SQn2Xjcyk2t5FeAOYSZlz+gNXXFaH4Yb0MYCuYYBAkEAk2NoDXYogDnhF14qq9UjwUJcTh6IXuBq8fwP33+uf2xnkbxyXVJ5TG4xXKH3UF8296+cWY71snR0HrTw57w38QJAetg8gzt4HNnEbAOePTgqLfzCElKFW2ty0MgHjQrWIJcaMkTTyy0J1AI7FYSjzxzAar1NOk6vE/Mtg5eaxDZsAQJBAJDnYFzPLF3k3IWGMGKZqVzkqG9yOMYHBIqe42y0ypPP+erv7Kz99/rKPMw9ikiOWnBNhuw5c8bb7qYGu87VeEE=";

	private static final String publicKey ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKrbrsAf1vVkXF054eGlCqRYQin/o1fejDyPTEAvOfFHCgIfSY/aslNhStRnSqM4MlXdL8iM+6KxsQ5shG3qF3zfGMYqwgQUgw4+aE/xcma7xLlGjLsUG08p0l+9Aa6dBU3q0EWN5hnbitSPOhsUFQ7irGhOmkpA233Gscjz1YbQIDAQAB";

	private static final Map<Integer, String> keyMap = new HashMap<>();  //用于封装随机产生的公钥与私钥

	public static void main(String[] args) throws Exception {
		String message = "1234567";
		System.out.println("随机生成的公钥为:" + publicKey);
		System.out.println("随机生成的私钥为:" + privateKey);
		String messageEn = encrypt(message,publicKey);
		System.out.println(message + "\t加密后的字符串为:" + messageEn);
		String messageDe = decrypt(messageEn,privateKey);
		System.out.println("还原后的字符串为:" + messageDe);

	}

	public static void genKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024,new SecureRandom());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
		keyMap.put(0,publicKeyString);  //0表示公钥
		keyMap.put(1,privateKeyString);  //1表示私钥
	}

	public static String encrypt( String str, String publicKey ) throws Exception{
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
	}

	public static String decrypt(String str, String privateKey) throws Exception{
		byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
		byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));  
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		return new String(cipher.doFinal(inputByte));
	}

	public static String getPublicKeyString(){
		return keyMap.get(0);
	}

	public static String getPrivateKeyString(){
		return keyMap.get(1);
	}


}
