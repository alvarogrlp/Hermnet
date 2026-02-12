package com.hermnet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
    @NotBlank(message = "El ID es obligatorio")
    @Pattern(regexp = "^HNET-[A-Za-z0-9]{5,}$", message = "El ID debe empezar por HNET- y tener caracteres alfanuméricos")
    String id,

    @NotBlank(message = "La clave pública es obligatoria")
    String publicKey,

    String pushToken // Opcional
) {}