package ar.edu.ungs.billetera;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import static org.junit.Assert.*;


public class BilleteraTestPropio {
    private IBilletera billetera;

    @Before
    public void setUp() {
        billetera = new Billetera();
        billetera.registrarUsuario("11111111", "Alice", "123", "a@test.com");
        billetera.registrarUsuario("22222222", "Bob", "456", "b@test.com");
        Utilitarios.definirHoy(LocalDate.now());
        Utilitarios.actualizarCotizacion("USD", 1000);
        Utilitarios.actualizarCotizacion("EUR", 1100);
    }
    
    
    
    @Test
    public void testProcesarInversionesQueVencenHoy() {
        String cvu = billetera.crearCuentaPremium("11111111", "alice.venc", 2000000);
        billetera.realizarInversionRentaFija("11111111", cvu, 500000, 30);
        assertEquals(500000.0, billetera.obtenerTotalInvertido("11111111"), 0.01);

        Utilitarios.definirHoy(Utilitarios.hoy().plusDays(30));
        billetera.procesarInversionesQueVencenHoy();

        assertEquals(0.0, billetera.obtenerTotalInvertido("11111111"), 0.01);
        // saldo = (2M - 500K invertido) + (500K + intereses acreditados) = 2M + intereses
        assertTrue(billetera.obtenerSaldoDisponible(cvu) > 2000000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCeroCuentasConMayorVolumen() {
        billetera.cuentasConMayorVolumen(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCuentasConMayorVolumenCantidadMayorALaCantidadTotalDeCuentas() {
        String cvu = billetera.crearCuentaPremium("11111111", "alice.venc", 2000000);
        String cvu2 = billetera.crearCuentaPremium("11111111", "alice.venc.dos", 4000000);
        
        billetera.realizarTransferencia(cvu, cvu2, 15000);
        billetera.realizarTransferencia(cvu2, cvu, 15000);
        
        // ingreso un valor invalido, porque hay 2 cuentas
        billetera.cuentasConMayorVolumen(4);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testPrecancelarInversionLiquidezFallaPorNoSerPrecancelable() {
        billetera.registrarEmpresa("30-99999999-9", "Empresa Test", "123", "e@test.com", "Juan");
        billetera.agregarPersonaAutorizada("30-99999999-9", "11111111");
        
        String cvuPremium = billetera.crearCuentaPremium("11111111", "alice.pre", 25000000);
        String cvuCorp = billetera.crearCuentaCorporativa("11111111", "corp.test", "30-99999999-9");
        
        billetera.realizarTransferencia(cvuPremium, cvuCorp, 20000000);
        
        int id = billetera.realizarInversionLiquidez("11111111", cvuCorp, 20000000, 30);

        // debe lanzar excepcion ya que inversion FLE no es precancelable
        billetera.precancelarInversion("11111111", cvuCorp, id);   
    }

    @Test
    public void testInversionLiquidezExito() {
        billetera.registrarEmpresa("30-88888888-8", "Empresa OK", "456", "ok@test.com", "Pedro");
        billetera.agregarPersonaAutorizada("30-88888888-8", "11111111");
        
        String cvuPremium = billetera.crearCuentaPremium("11111111", "alice.liq", 25000000);
        String cvuCorp = billetera.crearCuentaCorporativa("11111111", "corp.liq", "30-88888888-8");
        
        // transfiero porque para hacer inversion liquidez hace falta cta corpo y no es posible crear una con dinero en cuenta
        billetera.realizarTransferencia(cvuPremium, cvuCorp, 20000000);
        billetera.realizarInversionLiquidez("11111111", cvuCorp, 20000000, 30);
        
        assertEquals(20000000.0, billetera.obtenerTotalInvertido("11111111"), 0.01);
        assertEquals(0.0, billetera.obtenerSaldoDisponible(cvuCorp), 0.01);
    }

}
