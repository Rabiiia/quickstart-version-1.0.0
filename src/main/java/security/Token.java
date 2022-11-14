package security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.List;


// We have pasted already existing code from LoginEndPoint and pasted in this new class we created
// This is a class which creates a token
public class Token {

    public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
    private final String TOKEN;

    public Token(String username, List<String> roles) throws JOSEException {
        TOKEN = createToken(username, roles);
    }

    public static String createToken(String username, List<String> roles) throws JOSEException {
        StringBuilder res = new StringBuilder();
        for (String string : roles) {
            res.append(string);
            res.append(",");
        }
        String rolesAsString = res.length() > 0 ? res.substring(0, res.length() - 1) : "";

        JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
        Date date = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("username", username)
                .claim("roles", rolesAsString)
                .issueTime(date)
                .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public String toString() {
        return TOKEN;
    }
}

