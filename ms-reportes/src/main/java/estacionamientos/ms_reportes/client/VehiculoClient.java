package estacionamientos.ms_reportes.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ms-vehiculos")
public interface VehiculoClient {
    

}
