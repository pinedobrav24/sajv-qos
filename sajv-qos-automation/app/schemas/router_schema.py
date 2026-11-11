from typing import Optional
from pydantic import BaseModel, Field, IPvAnyAddress, SecretStr

class RouterConexionRequest(BaseModel):
    equipo_id: int
    hostname: str = Field(min_length=1, max_length=100)
    ip: IPvAnyAddress
    puerto: int = Field(default=22, ge=1, le=65535)
    usuario: Optional[str] = None
    contrasena: Optional[SecretStr] = None
    contrasena_enable: Optional[SecretStr] = None
    fabricante: str = "Cisco"
    modelo: str = "3660"
