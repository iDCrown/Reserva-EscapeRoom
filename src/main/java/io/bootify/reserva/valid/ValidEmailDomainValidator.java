package io.bootify.reserva.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class ValidEmailDomainValidator implements ConstraintValidator<ValidEmailDomain, String> {

    private static final Set<String> VALID_DOMAINS = Set.of(
        // 🌍 Dominios globales populares
        "gmail.com", "hotmail.com", "outlook.com", "yahoo.com",
        "icloud.com", "aol.com", "live.com", "protonmail.com",
        "zoho.com", "mail.com", "gmx.com", "yandex.com",
        "tutanota.com", "fastmail.com", "me.com", "msn.com",

        // 🎓 Dominios educativos
        "edu.co", "edu.com", "edu.mx", "edu.ar", "edu.pe",
        "edu.ec", "edu.ve", "edu.cl", "edu.bo", "edu.uy",
        "edu.br", "edu.es", "edu.us", "ac.uk", "ac.cr", "ac.jp",

        // 💼 Dominios empresariales y profesionales
        "office.com", "microsoft.com", "googlemail.com", "hey.com",
        "inbox.com", "hushmail.com", "runbox.com", "mail.ru",
        "yandex.ru", "icloud.es", "icloud.fr",

        // 🇨🇴 Dominios comunes en Colombia y Latinoamérica
        "hotmail.es", "outlook.es", "yahoo.es", "live.es",
        "gmail.com.co", "outlook.com.co", "hotmail.com.co"
    );

    private static final Set<String> VALID_TLDS = Set.of(
        "com", "org", "net", "edu", "gov", "co", "es", "mx", "ar", "pe", "br", "cl", "ec", "uy"
    );

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || !email.contains("@")) return false;

        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        // 🔒 Evita dominios demasiado cortos
        if (domain.length() < 5) return false;

        // 🌍 Rechaza nombres de dominio de una sola letra (como d.com)
        String[] parts = domain.split("\\.");
        if (parts.length < 2 || parts[0].length() < 2) return false;

        // 🧩 Verifica extensión válida (TLD)
        String tld = parts[parts.length - 1];
        if (!VALID_TLDS.contains(tld)) return false;

        // ✅ Acepta dominio si está en lista o formato razonable
        return VALID_DOMAINS.contains(domain);
    }
}
