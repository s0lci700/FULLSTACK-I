package estacionamientos.ms_espacios.repository;

import estacionamientos.ms_espacios.model.Espacio;
import estacionamientos.ms_espacios.model.TipoEspacio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EspacioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EspacioRepository espacioRepository;

    private TipoEspacio tipo;

    @BeforeEach
    void setUp() {
        tipo = entityManager.persist(new TipoEspacio(null, "Normal", "Espacio estándar", new BigDecimal("1.00")));
        entityManager.persist(new Espacio(null, "A-01", "Norte", 1, tipo, true, true));
        entityManager.persist(new Espacio(null, "A-02", "Norte", 1, tipo, false, true));
        entityManager.persist(new Espacio(null, "B-01", "Sur", 2, tipo, true, true));
        entityManager.flush();
    }

    @Test
    @DisplayName("findByDisponibleTrue debe retornar solo espacios disponibles")
    void findByDisponibleTrue_debeFiltrarDisponibles() {
        // Act
        List<Espacio> disponibles = espacioRepository.findByDisponibleTrue();

        // Assert
        assertThat(disponibles).hasSize(2);
        assertThat(disponibles).allMatch(Espacio::getDisponible);
    }

    @Test
    @DisplayName("findByZona debe retornar los espacios de la zona indicada")
    void findByZona_debeFiltrarPorZona() {
        // Act
        List<Espacio> norte = espacioRepository.findByZona("Norte");

        // Assert
        assertThat(norte).hasSize(2);
        assertThat(norte).allMatch(e -> e.getZona().equals("Norte"));
    }

    @Test
    @DisplayName("existsByNumero debe detectar números duplicados")
    void existsByNumero_debeDetectarDuplicados() {
        // Act + Assert
        assertThat(espacioRepository.existsByNumero("A-01")).isTrue();
        assertThat(espacioRepository.existsByNumero("Z-99")).isFalse();
    }
}
