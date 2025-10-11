package es.bitnomio.utilities.utils;

import io.micronaut.core.annotation.Nullable;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import es.bitnomio.utilities.config.security.encryption.EncryptionConfigProperties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import jakarta.inject.Singleton;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <h3>Encryption utils for Bitnomio data</h3>
 * <p>This should not be necessary to explain but, in any case and knowing the kind of people we're dealing with...</p>
 * <p>
 * This compliant solution uses the Advanced Encryption Standard (AES) algorithm in Cipher Block Chaining (CBC)
 * mode to perform the encryption. It uses the "AES/CBC/PKCS5Padding" transformation, which the Java documentation
 * guarantees to be available on all conforming implementations of the Java platform.
 * The Advanced Encryption Standard (AES) is the algorithm trusted as the standard by the U.S. Government
 * and numerous organizations. Although it is extremely efficient in 128-bit form, AES also uses keys of 192 and
 * 256 bits for heavy duty encryption purposes
 * </p>
 * <p>
 * How does AES encryption and decryption work?
 * Encryption works by taking plain text and converting it into cipher text, which is made up of seemingly random
 * characters. Only those who have the special key can decrypt it. AES uses symmetric key encryption,
 * which involves the use of only one secret key to cipher and decipher information
 * </p>
 * <p>
 * Is AES 256 crackable?
 * AES 256 is virtually impenetrable using brute-force methods. While a 56-bit DES key can
 * be cracked in less than a day, AES would take billions of years to break using current computing technology.
 * Hackers know this type of attack is a waste of time.
 * </p>
 * <p>
 * AES is chose over RSA because we want to speed things up a little, as stated before if we need to hardened our
 * encryption we could upgrade from 128 to 256 bit.<br/>
 * Some literature about this:&nbsp;
 * <a href="https://crypto.stackexchange.com/questions/13235/how-do-institutions-like-banks-do-rsa-with-big-primes">
 *     RSA vs AES on bank industry</a>
 * </p>
 * <p>
 * 2024 [Peter]
 * </p>
 */
@Singleton
public final class EncryptionUtils {

    private static final Logger log = LoggerFactory.getLogger(EncryptionUtils.class.getName());

    public static final Boolean IS_AN_URL = Boolean.TRUE;
    public static final Boolean NOT_AN_URL = Boolean.FALSE;

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";

    private final EncryptionConfigProperties encryptionConfigProperties;

    public EncryptionUtils(EncryptionConfigProperties encryptionConfigProperties) {
        this.encryptionConfigProperties = encryptionConfigProperties;
    }

    /**
     * Encrypt the string with this internal algorithm.
     *
     * @param toBeEncrypt string object to be encrypted.
     * @param isUrl       the param to encrypt is an url
     * @return returns encrypted string.
     */
    public String encrypt(String toBeEncrypt, @Nullable Boolean isUrl) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(toBeEncrypt.getBytes());
            if (isUrl != null && !isUrl) {
                return Base64.encodeBase64String(encrypted);
            }
            else {
                return Base64.encodeBase64URLSafeString(encrypted);
            }
        }
        catch (
            IllegalBlockSizeException
                | InvalidKeyException
                | InvalidAlgorithmParameterException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | BadPaddingException e
        ) {
            log.error("Error encrypting string", e);
        }
        return toBeEncrypt;
    }

    /**
     * Decrypt this string with the internal algorithm. The passed argument should be encrypted using
     * {@link #encrypt(String, Boolean) encrypt} method of this class.
     *
     * @param encrypted encrypted string that was encrypted using {@link #encrypt(String, Boolean) encrypt} method.
     * @return decrypted string.
     */
    public String decrypt(String encrypted) {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);

            byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(decryptedBytes);
        }
        catch (
            InvalidAlgorithmParameterException
                | InvalidKeyException
                | BadPaddingException
                | IllegalBlockSizeException
                | NoSuchPaddingException
                | NoSuchAlgorithmException e
        ) {
            log.error("Error decrypting string", e);
        }

        return encrypted;
    }

    /**
     * @return Cipher config instance
     * @throws NoSuchAlgorithmException throwable exception
     * @throws NoSuchPaddingException throwable exception
     * @throws InvalidKeyException throwable exception
     * @throws InvalidAlgorithmParameterException throwable exception
     */
    private Cipher getCipher(int type)
        throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(
            encryptionConfigProperties.ivParameter().getBytes(UTF_8)
        );
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            encryptionConfigProperties.key().getBytes(UTF_8),
            ENCRYPTION_ALGORITHM
        );

        Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
        cipher.init(type, secretKeySpec, ivParameterSpec);

        return cipher;
    }


}
