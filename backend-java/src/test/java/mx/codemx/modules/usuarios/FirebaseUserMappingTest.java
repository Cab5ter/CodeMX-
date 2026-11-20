package mx.codemx.modules.usuarios;

import java.util.Optional;
import mx.codemx.modules.auth.CredencialesInvalidasException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FirebaseUserMappingTest {
    private UsuarioRepository repo;
    private UsuarioService service;
    @BeforeEach void setUp() {
        repo = mock(UsuarioRepository.class);
        service = new UsuarioService(repo, new BCryptPasswordEncoder());
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }
    @Test void createsIdentityWithoutCopyingFirebasePassword() {
        when(repo.findByFirebaseUid("uid")).thenReturn(Optional.empty());
        when(repo.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
        var user = service.buscarOCrearDesdeFirebase("uid", "Test@tecdesoftware.edu.mx", null, false);
        assertEquals("uid", user.getFirebaseUid());
        assertEquals("test@tecdesoftware.edu.mx", user.getEmail());
        assertEquals("test", user.getNombre());
        assertTrue(user.getPasswordHash().startsWith("$2"));
    }
    @Test void repeatedLoginKeepsSameStudentAndProgress() {
        var user = new Usuario(); user.setId(42L); user.setFirebaseUid("uid");
        when(repo.findByFirebaseUid("uid")).thenReturn(Optional.of(user));
        assertSame(user, service.buscarOCrearDesdeFirebase("uid", "test@tecdesoftware.edu.mx", null, false));
        verify(repo, never()).save(any());
    }
    @Test void unverifiedEmailCannotTakeOverWebAccount() {
        when(repo.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(new Usuario()));
        assertThrows(CredencialesInvalidasException.class, () -> service.buscarOCrearDesdeFirebase(
                "uid", "test@tecdesoftware.edu.mx", "Test", false));
        verify(repo, never()).save(any());
    }
    @Test void verifiedEmailLinksExistingAccountWithoutLosingData() {
        var user = new Usuario(); user.setId(42L); user.setPasswordHash("existing-hash");
        when(repo.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));
        var linked = service.buscarOCrearDesdeFirebase("uid", "test@tecdesoftware.edu.mx", "Test", true);
        assertEquals(42L, linked.getId());
        assertEquals("existing-hash", linked.getPasswordHash());
        assertEquals("uid", linked.getFirebaseUid());
    }
    @Test void differentUidCannotTakeOverLinkedAccount() {
        var user = new Usuario(); user.setFirebaseUid("another-uid");
        when(repo.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(user));
        assertThrows(CredencialesInvalidasException.class, () -> service.buscarOCrearDesdeFirebase(
                "uid", "test@tecdesoftware.edu.mx", "Test", true));
    }
}
