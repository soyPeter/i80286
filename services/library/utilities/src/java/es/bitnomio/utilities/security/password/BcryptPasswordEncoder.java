package es.bitnomio.utilities.security.password;

import es.bitnomio.utilities.security.config.hash.HashingConfigProperties;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.Introspected;

import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCrypt;
import jakarta.validation.constraints.NotBlank;

/**
 * <h3>Bitnomio password encode util</h3>
 * <p>
 * Passwords shouldn't be encrypted. Because of the special nature of this kind of data
 * passwords should be hashed. Performing hash over data is a one-way operation, this means
 * it's irreversible, you apply the secure hash algorithm, and you cannot get the original string back.
 * This prevents attackers from decrypting and using users' passwords if the encryption key is leaked.
 * </p>
 * <p>
 * BCrypt:
 * Implementation of PasswordEncoder that uses the BCrypt strong hashing function.
 * Clients can optionally supply a "version" ($2a, $2b, $2y) and a "strength"
 * (a.k.a. log rounds in BCrypt) and a SecureRandom instance. The larger the strength
 * parameter the more work will have to be done (exponentially) to hash the passwords.
 * The default value is 10.
 * </p>
 * <p>
 * MD5 (produces max 32 chars) and SHA1 (produces max 40 chars) should be avoided to hash passwords
 * because those functions output hexadecimal values so every char will only have 16 possibilities
 * to guess. See <a href="https://en.wikipedia.org/wiki/MD5#Security">MD5 Security</a>
 * </p>
 * <p>
 * Bitnomio 2024 [Peter]
 * </p>
 */
@Singleton
@Introspected

public class BcryptPasswordEncoder implements PasswordEncoder {

  private final HashingConfigProperties hashingConfigProperties;

  private final String salt;

  public BcryptPasswordEncoder(HashingConfigProperties hashingConfigProperties,
      @Value("${app.security.hash.salt}") String salt) {
    this.hashingConfigProperties = hashingConfigProperties;
    this.salt = salt;
  }

  @Override
  public String encode(@NotBlank String rawPassword) {
    return BCrypt.hashpw(rawPassword, getSaltToPass());
  }

  @Override
  public boolean matches(@NotBlank String rawPassword, @NotBlank String encodedPassword) {
    return BCrypt.checkpw(rawPassword, encodedPassword);
  }

  private String getSaltToPass() {
    return hashingConfigProperties.salt() != null ? hashingConfigProperties.salt() : salt;
  }
}
