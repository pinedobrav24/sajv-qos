from typing import Optional

from pydantic import BaseModel, Field, IPvAnyAddress, SecretStr, field_validator


class QosOperacionRequest(BaseModel):

    audiencia_id: Optional[int] = None

    equipo_id: int = Field(gt=0)

    hostname: str = Field(min_length=1, max_length=100)

    ip: IPvAnyAddress

    puerto: int = Field(default=22, ge=1, le=65535)

    usuario: str = Field(min_length=1, max_length=100)

    contrasena: SecretStr

    contrasena_enable: Optional[SecretStr] = None

    interfaz: str = Field(min_length=1, max_length=100)

    class_map: str = Field(min_length=1, max_length=100)

    policy_map: str = Field(min_length=1, max_length=100)

    direccion: str = Field(default="output")

    @field_validator("direccion")
    @classmethod
    def validar_direccion(cls, valor):
        valor = valor.lower().strip()

        if valor not in ("input", "output"):
            raise ValueError(
                "La dirección debe ser input u output."
            )

        return valor