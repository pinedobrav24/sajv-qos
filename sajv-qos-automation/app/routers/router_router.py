from fastapi import APIRouter, HTTPException

from app.schemas.router_schema import RouterConexionRequest
from app.services.conexion import conexion_cisco

router = APIRouter(
    prefix="/api/router",
    tags=["Conexión con Router"]
)

@router.post("/conexion")
def probar_conexion(datos: RouterConexionRequest):
    resultado = conexion_cisco.probar_conexion(datos)
    if not resultado["exitoso"]:
        raise HTTPException(status_code=502, detail=resultado["mensaje"])
    return resultado
