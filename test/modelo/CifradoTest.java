package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CifradoTest {

    @Test
    void elHashTiene64CaracteresYNoEsLaContrasena() {
        String hash = Cifrado.sha256("123");
        assertEquals(64, hash.length());
        assertNotEquals("123", hash);
    }

    @Test
    void laMismaContrasenaDaElMismoHash() {
        assertEquals(Cifrado.sha256("abc"), Cifrado.sha256("abc"));
        assertNotEquals(Cifrado.sha256("abc"), Cifrado.sha256("abd"));
    }

    @Test
    void daElHashConocidoDeSHA256() {
        assertEquals("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
                Cifrado.sha256("123"));
    }

    @Test
    void usuarioVerificaLaContrasenaContraSuHash() {
        Usuario u = new Usuario(1, "ana", "x", new Rol(1, "Mesero"));
        u.setPassword(Cifrado.sha256("secreta"));
        assertTrue(u.verificarPassword("secreta"));
        assertTrue(!u.verificarPassword("otra"));
        assertTrue(!u.verificarPassword(null));
    }

    @Test
    void noCifraTextoNulo() {
        assertThrows(IllegalArgumentException.class, () -> Cifrado.sha256(null));
    }
}
